package com.android.music.fragment.home.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.adapter.MyOptionAdapter;
import com.android.music.adapter.MyPlaylistAdapter;
import com.android.music.bean.ImageText;
import com.android.music.bean.Playlist;
import com.android.music.component.MyViewFlipper;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.StateUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MyFragment extends Fragment {
    private View view;
    private Context context;
    private List<Playlist> list = new ArrayList<>();
    private List<Playlist> mPlaylist;
    private List<Playlist> mMyPlaylist;
    private TextView tv_hot_playlist;
    private TextView tv_playlist;
    private ImageView iv_no_data;
    //轮播图
    private int[] resIds = new int[]{R.drawable.a, R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f};

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == StateUtil.UPDATE_PLAYLIST_MY) {//更新playlist
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                RecyclerView recyclerView = view.findViewById(R.id.recycler_my_playlist);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new MyPlaylistAdapter(context, list));
            }
            return false;
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        context = view.getContext();
        initView();
        initEvent();
        return view;
    }

    //初始化各种View
    public void initView(){
        tv_hot_playlist = view.findViewById(R.id.my_hot_playlist);
        tv_playlist = view.findViewById(R.id.my_playlist);
        //iv_no_data = view.findViewById(R.id.my_no_data);
        //初始化轮播图
        MyViewFlipper myViewFlipper = view.findViewById(R.id.my_view_flipper);
        myViewFlipper.setFlipInterval(5000);
        myViewFlipper.setInAnimation(context,R.anim.right_in);
        myViewFlipper.setOutAnimation(context,R.anim.right_out);
        myViewFlipper.startFlipping();//轮播图自动播放
        //添加轮播图子视图
        for (int resId : resIds) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setBackgroundResource(resId);
            myViewFlipper.addView(iv);
        }
        //初始化轮播图的圆点
        LinearLayout linearLayout = view.findViewById(R.id.dot_linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,0,0,0);//图片间距
        for(int i=0;i<resIds.length;i++){
            ImageView view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if(i==0){
                view.setBackgroundResource(R.drawable.dot_select);
                linearLayout.addView(view);
            }else{
                view.setBackgroundResource(R.drawable.dot_unselect);
                linearLayout.addView(view,lp);
            }
        }
        myViewFlipper.setDotLinearLayout(linearLayout);//给轮播图添加圆点

        //初始化首页的6大图标
        List<ImageText> imageTexts = new ArrayList<>();
        int[] imageId = {R.drawable.ic_music,R.drawable.ic_song_download,R.drawable.ic_lately,R.drawable.ic_song_like,R.drawable.ic_listen,R.drawable.ic_zk};
        String[] text = {"本地音乐","下载音乐","最近播放","我喜欢","听歌识曲","关于我们"};
        for(int i=0;i<text.length;i++){
            ImageText imageText = new ImageText(imageId[i],text[i]);
            imageTexts.add(imageText);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_option);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new MyOptionAdapter(context, imageTexts));

        //初始化playlist(歌单)
        getMyPlaylist();
        getPlayList();
    }
    private void initEvent() {
        tv_hot_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_hot_playlist.setTextColor(getResources().getColor(R.color.colorBlack));
                tv_playlist.setTextColor(getResources().getColor(R.color.colorGray));
                getPlayList();
            }
        });

        tv_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_hot_playlist.setTextColor(getResources().getColor(R.color.colorGray));
                tv_playlist.setTextColor(getResources().getColor(R.color.colorBlack));
                getMyPlaylist();
            }
        });
    }

    public void getMyPlaylist(){
        String url = HttpUtil.PLAYLIST_GETUSERPLAYLIST+ UserUtil.id;
        //获取歌单数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                mMyPlaylist = JsonUtil.getList(response,Playlist.class);
                list = mMyPlaylist;
                MusicUtil.userPlayList = mMyPlaylist;
                Message message = new Message();
                message.what = StateUtil.UPDATE_PLAYLIST_MY;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(context,"网络请求失败");
            }
        },null);
    }
    public void getPlayList(){
       /* if(mPlaylist!=null){
            list = mPlaylist;
            Message message = new Message();
            message.what = StateUtil.UPDATE_PLAYLIST_MY;
            handler.sendMessage(message);
            return;
        }*/
        String url = HttpUtil.PLAYLIST_FINDALL;
        //获取歌单数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                mPlaylist = JsonUtil.getList(response,Playlist.class);
                list = mPlaylist;
                Message message = new Message();
                message.what = StateUtil.UPDATE_PLAYLIST_MY;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(context,"网络请求失败");
            }
        },null);
    }
}