package com.android.music.fragment.bottom;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.adapter.PopSongAdapter;
import com.android.music.broadcast.SongChangeReceiver;
import com.android.music.interfaces.IMusicView;
import com.android.music.util.ApplicationUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.StateUtil;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Describe:音乐底部
 */
public class MusicBottomFragment extends Fragment {
    private String Tag = "MusicBottomFragment";
    private Context mContext;
    private View view;

    private ImageView mPlayView;//播放，暂停图片
    private ImageView songImg;//歌曲图片
    private TextView mLycView;//歌词
    private TextView mSongName;//歌名
    private String lyric;
    private ObjectAnimator objectAnimation;//歌曲图片的旋转动画

    private LocalBroadcastManager localBroadcastManager;//本地广播管理器
    private SongChangeReceiver songChangeReceiver;//接受歌曲改变的广播

    private View popView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_music_bottom, container, false);
        popView =  inflater.inflate(R.layout.pop_song_list,container,false);
        initView();
        initBroadcast();
        initEvent();
        //有歌曲则刷新界面
        if(MusicUtil.musicBind.getSong()!=null){
            mIMusicView.songChange();
            if(MusicUtil.musicBind.isIsplay()){
                mIMusicView.musicStateChange(StateUtil.PLAY_SONG);
            }else{
                mLycView.setText(MusicUtil.musicBind.updateLyric());
                mIMusicView.musicStateChange(StateUtil.PAUSE_SONG);
            }
        }
        return view;
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initView() {
        songImg = view.findViewById(R.id.current_img);
        mPlayView = view.findViewById(R.id.current_play);
        mLycView = view.findViewById(R.id.current_song_lyc);
        mSongName = view.findViewById(R.id.current_song_name);

        //初始化旋转动画
        objectAnimation = (ObjectAnimator) AnimatorInflater.loadAnimator(ApplicationUtil.getContext(),R.animator.rotate);
        objectAnimation.setTarget(songImg);

    }
    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.broadcast.SongChangeReceiver");
        songChangeReceiver = new SongChangeReceiver(mIMusicView);//当歌曲改变时，改变底部播放栏状态
        //注册本地广播监听器
        localBroadcastManager.registerReceiver(songChangeReceiver, intentFilter);
    }
    private void initEvent() {
        //播放音乐,暂停音乐
        final ImageView playMusic = view.findViewById(R.id.current_play);
        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(MusicUtil.musicBind.isIsplay()){
                mIMusicView.musicStateChange(StateUtil.PAUSE_SONG);
            }else{
                mIMusicView.musicStateChange(StateUtil.PLAY_SONG);
            }
            }
        });
        //点击进入歌曲页面
        view.findViewById(R.id.current_song_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayMusicActivity.class);
                startActivity(intent);
            }
        });

        //点击展示歌曲播放列表
        view.findViewById(R.id.current_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow = new PopupWindow(popView,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (MusicUtil.screen_height*0.7));
                //设置动画
                popupWindow.setAnimationStyle(R.style.popWindow);
                // 使其聚集
                popupWindow.setFocusable(true);
                // 设置允许在外点击消失
                popupWindow.setOutsideTouchable(true);

                //初始化视图
                final TextView mode_tv = popView.findViewById(R.id.song_play_tv);
                final ImageView mode_iv = popView.findViewById(R.id.song_play_mode);
                ImageView mode_del_all = popView.findViewById(R.id.song_play_delete);
                TextView mode_close = popView.findViewById(R.id.song_play_close);
                //设置布局
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                RecyclerView recyclerView = popView.findViewById(R.id.pop_song_list_recycler);
                recyclerView.setLayoutManager(layoutManager);
                PopSongAdapter adapter = new PopSongAdapter(mContext,MusicUtil.musicBind.getSongsPlay());
                recyclerView.setAdapter(adapter);

                //初始化播放方式
                int mode = MusicUtil.musicBind.getPlay_mode();
                if(mode==0){
                    mode_iv.setBackgroundResource(R.drawable.ic_single_loop);
                    mode_tv.setText(R.string.single_loop);
                }else if(mode==1){
                    mode_iv.setBackgroundResource(R.drawable.ic_play_order);
                    mode_tv.setText(R.string.play_order);
                }else{
                    mode_iv.setBackgroundResource(R.drawable.ic_play_random);
                    mode_tv.setText(R.string.play_random);
                }
                //点击事件
                mode_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int mode = MusicUtil.musicBind.getPlay_mode();
                        mode = (mode+1)%3;
                        if(mode==0){
                            mode_iv.setBackgroundResource(R.drawable.ic_single_loop);
                            mode_tv.setText(R.string.single_loop);
                        }else if(mode==1){
                            mode_iv.setBackgroundResource(R.drawable.ic_play_order);
                            mode_tv.setText(R.string.play_order);
                            MusicUtil.musicBind.setSongsPlay(new ArrayList<>(MusicUtil.musicBind.getSongs()));
                        }else{
                            mode_iv.setBackgroundResource(R.drawable.ic_play_random);
                            mode_tv.setText(R.string.play_random);
                            MusicUtil.musicBind.setSongsPlay(new ArrayList<>(MusicUtil.musicBind.getSongs()));
                            Collections.shuffle(MusicUtil.musicBind.getSongsPlay());
                        }
                        MusicUtil.musicBind.setPlay_mode(mode);
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data",Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit=  sharedPreferences.edit();
                        //添加数据
                        edit.putInt("play_mode",mode);
                        //提交
                        edit.apply();
                        //发送广播，通知底部音乐栏更新
                        Intent intent = new Intent("com.android.music.broadcast.PlayModeChangeReceiver");
                        intent.setComponent(new ComponentName("com.android.music","com.android.music.broadcast.PlayModeChangeReceiver"));
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                    }
                });
                mode_del_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.dialog_tips)
                                .setMessage(getString(R.string.dialog_remove_play_songs))
                                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MusicUtil.musicBind.recover();
                                        mIMusicView.musicStateChange(StateUtil.PAUSE_SONG);
                                        popupWindow.dismiss();
                                    }
                                })
                                .setNegativeButton(getString(R.string.dialog_no), null)
                                .show();
                    }
                });
                mode_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
            }
        });
    }
    private IMusicView mIMusicView = new IMusicView() {
        @Override
        public void songChange() {
            //设置图片
            Glide.with(mContext)
                    .load(Uri.parse(MusicUtil.musicBind.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.loading_error)
                    .into(songImg);
            //设置歌名
            mSongName.setText(MusicUtil.musicBind.getSong().getName());
        }

        @Override
        public void musicStateChange(int state) {
            switch (state){
                case StateUtil.CHANGE_SONG:
                    if(MusicUtil.musicBind.isIsplay()){
                        MusicUtil.musicBind.play();
                        songImg.clearAnimation();
                        startAnimation();
                    }else {
                        MusicUtil.musicBind.play();
                        songImg.clearAnimation();
                        startAnimation();
                        mPlayView.setBackgroundResource(R.drawable.ic_pause);
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PLAY_SONG:
                    if ("".equals(MusicUtil.musicBind.getSong().getUrl())) {
                        Toast.makeText(mContext, "当前没有要播放的歌曲哦~~", Toast.LENGTH_SHORT).show();
                    } else {
                        mPlayView.setBackgroundResource(R.drawable.ic_pause);
                        startAnimation(); //开启动画
                        MusicUtil.musicBind.play();
                        //用于更新歌词的线程
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PAUSE_SONG:
                    mPlayView.setBackgroundResource(R.drawable.ic_play);
                    MusicUtil.musicBind.pause();
                    pauseAnimation();//暂停动画
            }
        }
    };
    private void startAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (objectAnimation.isPaused()){
                objectAnimation.resume();
            }else {
                objectAnimation.start();
            }
        }
    }
    private void pauseAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            objectAnimation.pause();
        }
    }
    private final int UPDATE_LYRIC = 1;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_LYRIC) {//更新歌词
                mLycView.setText(lyric);
            }
            return false;
        }
    });
    class LyricThread extends Thread{
        @Override
        public void run() {
            while (MusicUtil.musicBind.isIsplay()){
                try {
                    Thread.sleep(200);
                    lyric  = MusicUtil.musicBind.updateLyric();
                    Message message = new Message();
                    message.what = UPDATE_LYRIC;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(songChangeReceiver);
    }
}