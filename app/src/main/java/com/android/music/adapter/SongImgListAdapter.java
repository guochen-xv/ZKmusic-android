package com.android.music.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.android.music.R;
import com.android.music.bean.Song;
import com.android.music.util.MusicUtil;

import java.util.List;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:歌曲列表的适配器
 */
public class SongImgListAdapter extends RecyclerView.Adapter<SongImgListAdapter.ViewHolder>{
    private List<Song> list;
    private Context mContext;
    private String currentId = MusicUtil.musicBind.getSong()==null?"":MusicUtil.musicBind.getSong().getId();//当前播放歌曲的id
    private final TypedValue mTypedValue;//获取主题颜色

    public SongImgListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        mTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, mTypedValue, true);
    }

    @NonNull
    @Override
    public SongImgListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_song_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SongImgListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSingerName());
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
        }else {
            holder.songName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.songSinger.setTextColor(mContext.getResources().getColor(R.color.colorGray));
        }

        //设置图片大小
        RoundedCorners roundedCorners = new RoundedCorners(20);
        //通过RequestOptions扩展功能,
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(mContext)
                .load(song.getPic())
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .apply(options)
                .error(R.drawable.loading_error)
                .into(holder.imageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
                currentId = song.getId();
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private ImageView imageView;
        private LinearLayout linearLayout;
        ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songSinger= itemView.findViewById(R.id.song_singer);
            imageView = itemView.findViewById(R.id.song_img);
            linearLayout = itemView.findViewById(R.id.song_layout);
        }
    }
}
