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
import com.android.music.bean.Rank;

import java.util.List;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:歌曲列表的适配器
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>{
    private Context mContext;
    private List<Rank> list;

    public RankAdapter(@NonNull Context context, List<Rank> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_top_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RankAdapter.ViewHolder holder, final int position) {
        //设置图片
        Rank rank= list.get(position);
        Glide.with(mContext)
                .load(Uri.parse(rank.getPic()))
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.loading_error)
                .into(holder.imageView);
        //设置前三首的歌
        /*List<TopList.SongListBean> songList = topList.getSongList();
        holder.song1.setText(songList.get(0).getSongname()+"-"+songList.get(0).getSingername());
        holder.song2.setText(songList.get(1).getSongname()+"-"+songList.get(1).getSingername());
        holder.song3.setText(songList.get(2).getSongname()+"-"+songList.get(2).getSingername());*/

        holder.song1.setText(rank.getTitle());
        holder.song2.setText("收藏量："+rank.getCommentNum()/1000+"万");
        holder.song3.setText("播放量："+rank.getPlayNum()/1000+"万");
        //点击跳转到相应的歌单详情页
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", list.get(position).getId());
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_SongListFragment,bundle);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView song1;
        private TextView song2;
        private TextView song3;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            song1 = itemView.findViewById(R.id.playlist_song1);
            song2 = itemView.findViewById(R.id.playlist_song2);
            song3 = itemView.findViewById(R.id.playlist_song3);
            imageView = itemView.findViewById(R.id.playlist_iv);
            linearLayout = itemView.findViewById(R.id.playlist_layout);
        }
    }
}
