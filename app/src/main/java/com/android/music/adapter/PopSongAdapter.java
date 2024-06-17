package com.android.music.adapter;

import android.content.Context;
import android.content.IntentFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.bean.Song;
import com.android.music.broadcast.PlayModeChangeReceiver;
import com.android.music.interfaces.IMusicView;
import com.android.music.util.MusicUtil;

import java.util.List;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:
 */
public class PopSongAdapter extends RecyclerView.Adapter<PopSongAdapter.ViewHolder> {
    private List<Song> list;
    private Context mContext;
    private String currentId = MusicUtil.musicBind.getSong().getId()==null?"": MusicUtil.musicBind.getSong().getId();//当前播放歌曲的id
    private final TypedValue mTypedValue;//获取主题颜色
    private IMusicView mIMusicView = new IMusicView() {
        @Override
        public void songChange() {
            list = MusicUtil.musicBind.getSongsPlay();
            notifyDataSetChanged();
        }

        @Override
        public void musicStateChange(int state) {
        }
    };
    public PopSongAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        mTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, mTypedValue, true);
        initBroadcast();
    }
    private void initBroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.broadcast.PlayModeChangeReceiver");

        PlayModeChangeReceiver playModeChangeReceiver = new PlayModeChangeReceiver(mIMusicView);//当歌曲改变时，改变底部播放栏状态
        //注册本地广播监听器
        localBroadcastManager.registerReceiver(playModeChangeReceiver, intentFilter);
    }
    @NonNull
    @Override
    public PopSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopSongAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_pop_song,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull PopSongAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSingerName());
        if(currentId.equals(song.getId())){
            holder.songName.setTextColor(mTypedValue.data);
            holder.songSinger.setTextColor(mTypedValue.data);
        }else {
            holder.songName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.songSinger.setTextColor(mContext.getResources().getColor(R.color.colorGray));
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
                currentId = song.getId();
                notifyDataSetChanged();
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.removeSong(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.pop_song_name);
            songSinger= itemView.findViewById(R.id.pop_song_singer_name);
            imageView = itemView.findViewById(R.id.pop_song_delete);
            linearLayout = itemView.findViewById(R.id.pop_song_layout);
        }
    }
}
