package com.android.music.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.bean.Song;
import com.android.music.util.HttpUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:搜索得到的歌曲 适配器
 */
public class SearchSongListAdapter extends RecyclerView.Adapter<SearchSongListAdapter.ViewHolder>{
    List<Song> list;
    private Context mContext;
    Drawable top;//上箭头
    Drawable down;//下箭头
    private String currentId = MusicUtil.musicBind.getSong()==null?"":MusicUtil.musicBind.getSong().getId();//当前播放歌曲的id
    private View popView; //为popupWindows的视图

    public SearchSongListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        top= mContext.getResources().getDrawable(R.drawable.ic_arrow_top);//上箭头
        down= mContext.getResources().getDrawable(R.drawable.ic_arrow_down);//下箭头
        }

    @NonNull
    @Override
    public SearchSongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        popView = LayoutInflater.from(mContext).inflate(R.layout.pop_song,parent,false);
        return new SearchSongListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_search_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchSongListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSingerName());
        holder.songIndex.setText(position+1+"");
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
            }
        });

        //点击按钮弹出相应弹窗
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow = new PopupWindow(popView,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                //设置动画
                popupWindow.setAnimationStyle(R.style.popWindow);
                // 使其聚集
                popupWindow.setFocusable(true);
                // 设置允许在外点击消失
                popupWindow.setOutsideTouchable(true);

                //设置点击事件
                Button play_bth = popView.findViewById(R.id.pop_play_song_btn);
                Button like_bth = popView.findViewById(R.id.pop_like_song_btn);
                Button download_bth = popView.findViewById(R.id.pop_download_song_btn);
                Button share_btn = popView.findViewById(R.id.pop_share_song_btn);
                Button delete_bth = popView.findViewById(R.id.pop_delete_song_btn);
                play_bth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicUtil.musicBind.songItemClick(position,song,list);
                        currentId = song.getId();
                        notifyDataSetChanged();
                        popupWindow.dismiss();
                    }
                });
                if(MusicUtil.collect_song_ids.contains(song.getId())){
                    like_bth.setText("取消收藏");
                }else {
                    like_bth.setText("收藏歌曲");
                }
                like_bth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(MusicUtil.collect_song_ids.contains(song.getId())){
                            MusicUtil.musicBind.dislike(song);
                        }else {
                            MusicUtil.musicBind.like(song);
                        }
                        popupWindow.dismiss();
                    }
                });
                //下载歌曲
                if(MusicUtil.download_song_ids.contains(song.getId())||MusicUtil.local_song_ids.contains(song.getId())){
                    download_bth.setVisibility(View.GONE);
                }else{
                    download_bth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MusicUtil.downLoadBinder.setSong(song);
                            MusicUtil.downLoadBinder.startDownload(song.getUrl(),
                                    song.getName()+"-"+song.getSingerName()+".mp3",
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
                            popupWindow.dismiss();
                        }
                    });
                }
                //删除歌曲
                if(MusicUtil.download_song_ids.contains(song.getId())||MusicUtil.local_song_ids.contains(song.getId())){
                    delete_bth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            File file = new File(song.getUrl());
                            if(file.delete()){
                                Toast.makeText(mContext,"删除歌曲成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,"删除歌曲失败",Toast.LENGTH_SHORT).show();
                            }
                            if(MusicUtil.download_song_ids.contains(song.getId())){
                                String url = HttpUtil.DOWNLOAD_DELETE+"/"+ UserUtil.id+"/"+song.getId();
                                HttpUtil.sendRequestDelete(url,new okhttp3.Callback() {
                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response){

                                    }
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    }
                                },null);
                                MusicUtil.download_song_ids.remove(song.getId());
                            }
                            if(MusicUtil.local_song_ids.contains(song.getId())){
                                LitePal.delete(Song.class,song.getLitePalId());
                                MusicUtil.local_song_ids.remove(song.getId());
                            }
                            list.remove(position);
                            notifyDataSetChanged();
                            popupWindow.dismiss();
                        }
                    });
                }else{
                    delete_bth.setVisibility(View.GONE);
                }

                //分享歌曲
                share_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                        Toast.makeText(mContext,"待完成",Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addSong(List<Song> songs){
        for (Song item: songs) {
            list.add(item);
        }
        notifyDataSetChanged();
    }
    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private TextView songIndex;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private TextView moreSong;//更多版本
        private RecyclerView recyclerView;//更多版本里的歌曲列表
        private View view;
        public ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songSinger= itemView.findViewById(R.id.song_singer);
            songIndex = itemView.findViewById(R.id.song_index);
            imageView = itemView.findViewById(R.id.song_more_option);
            linearLayout = itemView.findViewById(R.id.song_layout);
            moreSong = itemView.findViewById(R.id.search_song_more);
            recyclerView = itemView.findViewById(R.id.search_song_more_recycle_view);
            view = itemView;
        }
    }
}
