package com.android.music.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.bean.ImageText;
import com.android.music.bean.Song;

import java.util.List;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:我的界面里6个操作
 */
public class ShareIconAdapter extends RecyclerView.Adapter<ShareIconAdapter.ViewHolder> {
    private Context mContext;
    private List<ImageText> imageTexts;
    private Song song;
    public ShareIconAdapter(@NonNull Context context, List<ImageText> imageTexts, Song song) {
        this.mContext = context;
        this.imageTexts = imageTexts;
        this.song = song;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShareIconAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_share,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ImageText imageText = imageTexts.get(position);
        holder.textView.setText(imageText.getTitle());
        holder.imageView.setBackgroundResource(imageText.getImageId());
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //点击事件
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String url = song.getUrl();
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("url", song.getName()+"-"+song.getSingerName()+"\n"+url);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(mContext,"歌曲URL已复制到剪切板",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageTexts.size();
    }


    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            linearLayout = itemView.findViewById(R.id.pop_share_layout);
            textView = itemView.findViewById(R.id.share_tv);
            imageView = itemView.findViewById(R.id.share_iv);
        }
    }
}
