package com.android.music.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.bean.Playlist;
import com.android.music.util.ApplicationUtil;
import com.android.music.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:
 */
public class PopPlaylistAdapter extends RecyclerView.Adapter<PopPlaylistAdapter.ViewHolder> {
    private Context mContext;
    private List<Playlist> playlist;
    private String songId;
    public PopPlaylistAdapter(@NonNull Context context, List<Playlist> playlist,String songId) {
        this.mContext = context;
        this.playlist = playlist;
        this.songId = songId;
    }

    @NonNull
    @Override
    public PopPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopPlaylistAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_playlist,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Playlist playListSimple = playlist.get(position);
        holder.textView.setText(playListSimple.getTitle());
        //使用第三方库glide加载图片
        String url = playListSimple.getPic();
        Glide.with(mContext)
                .load(Uri.parse(url))
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.loading_error)
                .into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = HttpUtil.LISTSONG_ADD;
                HashMap<String,String> map = new HashMap<>();
                map.put("songId", songId);
                map.put("songListId", playlist.get(position).getId());
                //获取歌单数据
                HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response){
                        HttpUtil.success(ApplicationUtil.getContext(),"歌曲已添加到"+playlist.get(position).getTitle());
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //在这里对异常情况进行处理
                        HttpUtil.failed(ApplicationUtil.getContext(),"添加歌曲记录失败");
                    }
                },map);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.playlist_text);
            imageView = itemView.findViewById(R.id.playlist_iv);
            linearLayout = itemView.findViewById(R.id.playlist_layout);
        }
    }
}
