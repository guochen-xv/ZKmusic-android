package com.android.music.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.TypedValue;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.bean.ImageText;
import com.android.music.bean.Song;
import com.android.music.util.HttpUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:歌曲列表的适配器
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{
    private List<Song> list;
    private Context mContext;
    private View popView; //为popupWindows的视图
    private View popView_share; //为分享歌曲的视图
    private View popView_add; //为添加歌曲的视图
    private String currentId = MusicUtil.musicBind.getSong()==null?"":MusicUtil.musicBind.getSong().getId();//当前播放歌曲的id
    private final TypedValue mTypedValue;//获取主题颜色

    public SongListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        mTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, mTypedValue, true);
    }

    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        popView = LayoutInflater.from(mContext).inflate(R.layout.pop_song,parent,false);
        popView_share= LayoutInflater.from(mContext).inflate(R.layout.pop_share_song,parent,false);
        popView_add= LayoutInflater.from(mContext).inflate(R.layout.pop_add_playsong,parent,false);
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SongListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSingerName());
        holder.songIndex.setText(position+1+"");
        if(MusicUtil.download_song_ids.contains(song.getId())||MusicUtil.local_song_ids.contains(song.getId())){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_download_success);
            drawable.setBounds( 0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            holder.songSinger.setCompoundDrawables(drawable,null,null,null);
        }else{
            holder.songSinger.setCompoundDrawables(null,null,null,null);
        }
        if(currentId.equals(song.getId())){
            holder.songName.setTextColor(mTypedValue.data);
            holder.songSinger.setTextColor(mTypedValue.data);
            holder.songIndex.setTextColor(mTypedValue.data);
        }else {
            holder.songName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.songSinger.setTextColor(mContext.getResources().getColor(R.color.colorGray));
            holder.songIndex.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
                currentId = song.getId();
                notifyDataSetChanged();
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
                Button next_bth = popView.findViewById(R.id.pop_next_song_btn);
                Button like_bth = popView.findViewById(R.id.pop_like_song_btn);
                Button download_bth = popView.findViewById(R.id.pop_download_song_btn);
                Button share_btn = popView.findViewById(R.id.pop_share_song_btn);
                Button add_btn = popView.findViewById(R.id.pop_share_add_btn);
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
                next_bth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicUtil.musicBind.addPlaySong(song);
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
                //添加至我的歌单
                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupWindow popupWindow_add = new PopupWindow(popView_add,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                        //设置动画
                        popupWindow_add.setAnimationStyle(R.style.popWindow);
                        // 使其聚集
                        popupWindow_add.setFocusable(true);
                        // 设置允许在外点击消失
                        popupWindow_add.setOutsideTouchable(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                        RecyclerView recyclerView = popView_add.findViewById(R.id.pop_add_recycler);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(new PopPlaylistAdapter(mContext, MusicUtil.userPlayList,song.getId()));
                        popView_share.findViewById(R.id.pop_share_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow_add.dismiss();
                            }
                        });
                        popupWindow.dismiss();
                        popupWindow_add.showAtLocation(popView_share, Gravity.BOTTOM,0,0);
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
                        final PopupWindow popupWindow_share = new PopupWindow(popView_share,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        //设置动画
                        popupWindow_share.setAnimationStyle(R.style.popWindow);
                        // 使其聚集
                        popupWindow_share.setFocusable(true);
                        // 设置允许在外点击消失
                        popupWindow_share.setOutsideTouchable(true);
                        List<ImageText> imageTexts = new ArrayList<>();
                        int[] imageId = {
                                R.drawable.ic_qq,R.drawable.ic_qq_zoom,R.drawable.ic_vx,R.drawable.ic_vx_friend,
                                R.drawable.ic_xinlang,R.drawable.ic_baidu_tieba,R.drawable.ic_zhihu,R.drawable.ic_douyin};
                        String[] text = {
                                mContext.getString(R.string.pop_share_qq),
                                mContext.getString(R.string.pop_share_qqzoom),
                                mContext.getString(R.string.pop_share_vx),
                                mContext.getString(R.string.pop_share_vx_friend),
                                mContext.getString(R.string.pop_share_xinlang),
                                mContext.getString(R.string.pop_share_tieba),
                                mContext.getString(R.string.pop_share_zhihu),
                                mContext.getString(R.string.pop_share_douyin)};
                        for(int i=0;i<text.length;i++){
                            ImageText imageText = new ImageText(imageId[i],text[i]);
                            imageTexts.add(imageText);
                        }
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,4);
                        RecyclerView recyclerView = popView_share.findViewById(R.id.pop_share_recycler);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(new ShareIconAdapter(mContext, imageTexts,song));

                        popView_share.findViewById(R.id.pop_share_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow_share.dismiss();
                            }
                        });
                        popupWindow.dismiss();
                        popupWindow_share.showAtLocation(popView_share, Gravity.BOTTOM,0,0);

                    }
                });
                popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);

            }
        });
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private TextView songIndex;
        private ImageView imageView;
        private LinearLayout linearLayout;
        ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songSinger= itemView.findViewById(R.id.song_singer);
            songIndex = itemView.findViewById(R.id.song_index);
            imageView = itemView.findViewById(R.id.song_more_option);
            linearLayout = itemView.findViewById(R.id.song_layout);
        }
    }
}
