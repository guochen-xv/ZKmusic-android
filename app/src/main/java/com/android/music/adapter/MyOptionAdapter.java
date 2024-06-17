package com.android.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.bean.ImageText;

import java.util.List;
/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:我的界面里6个操作
 */
public class MyOptionAdapter extends RecyclerView.Adapter<MyOptionAdapter.ViewHolder> {
    private Context mContext;
    private List<ImageText> imageTexts;
    public MyOptionAdapter(@NonNull Context context, List<ImageText> imageTexts) {
        this.mContext = context;
        this.imageTexts = imageTexts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOptionAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view_home_option,parent,false));
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
                switch (position){
                    case 0:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_local_song);
                        break;
                    case 1:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_download_song);
                        break;
                    case 2:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_history_song);
                        break;
                    case 3:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_like_song);
                        break;
                    case 4:
                        Toast.makeText(mContext,"听不起扫不了嘤嘤嘤",Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_zk);
                        break;

                }
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
            linearLayout = itemView.findViewById(R.id.option_layout);
            textView = itemView.findViewById(R.id.option_text);
            imageView = itemView.findViewById(R.id.option_icon);
        }
    }
}
