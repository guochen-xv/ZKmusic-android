package com.android.music.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.bean.Playlist;

import java.util.List;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:我的界面 ---歌单列表的适配器
 */
public class MusicHallPlaylistAdapter extends RecyclerView.Adapter<MusicHallPlaylistAdapter.ViewHolder> {
    private Context mContext;
    private List<Playlist> playListSimples;
    public MusicHallPlaylistAdapter(@NonNull Context context, List<Playlist> playListSimples) {
        this.mContext = context;
        this.playListSimples = playListSimples;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_playlist_hall, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Playlist playListSimple = playListSimples.get(position);
        holder.textView.setText(playListSimple.getTitle());
        //使用第三方库glide加载图片
        String url = playListSimple.getPic();
        Glide.with(mContext)
                .load(Uri.parse(url))
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.loading_error)
                .into(holder.imageView);

        //点击跳转到相应的歌单详情页
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("dissid", playListSimple.getId());
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_SongListFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playListSimples.size();
    }
    public void changePlayListSimples(List<Playlist> playListSimples){
        this.playListSimples = playListSimples;
        notifyDataSetChanged();
    }
    static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.playlist_text);
            imageView = itemView.findViewById(R.id.playlist_iv);
            linearLayout = itemView.findViewById(R.id.playlist_layout);
        }
    }
}
