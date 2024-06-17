package com.android.music.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.activity.login.LoginActivity;
import com.android.music.annotation.ViewById;
import com.android.music.base.BaseActivity;
import com.android.music.bean.Playlist;
import com.android.music.bean.Song;
import com.android.music.service.DownLoadService;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.UserUtil;
import com.android.music.util.ViewBind;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private final String tag = "MainActivity";
    private Context mContext;
    @ViewById(R.id.nav_view)
    private NavigationView mNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    private Intent mIntentDownload;

    private ServiceConnection connectionDownload = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicUtil.downLoadBinder = (DownLoadService.DownLoadBinder) service ;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MusicUtil.downLoadBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBind.inJect(this);
        init();
        initService();
        initUser();
        getScreenDisplay();
    }

    //初始化用户信息
    private void initUser() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = mNavigationView.findViewById(R.id.user_img);
                TextView name_txt = mNavigationView.findViewById(R.id.user_name);
                TextView name_intro = mNavigationView.findViewById(R.id.user_intro);
                if(imageView!=null&&name_intro!=null&name_intro!=null){
                    Glide.with(mContext)
                            .load(Uri.parse(UserUtil.pic))
                            .centerCrop()
                            .placeholder(R.drawable.loading_spinner)
                            .error(R.drawable.loading_error)
                            .into(imageView);
                    name_txt.setText(UserUtil.name);
                    name_intro.setText(UserUtil.intro);
                }
            }
        }, 3000); //设置时间，3秒后自动跳转
        //加载下载歌曲记录
        String url = HttpUtil.DOWNLOAD_SONG+"/"+ UserUtil.id;
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                List<Song> list = JsonUtil.getList(response, Song.class);
                if(list==null){
                    return;
                }
                for(Song song : list){
                    MusicUtil.download_song_ids.add(song.getId());
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //在这里对异常情况进行处理
                HttpUtil.failed(mContext,"查询下载歌曲失败");
            }
        },null);
        //加载收藏歌曲记录
        url = HttpUtil.COLLECT_SONG+"/"+ UserUtil.id;
        //获取歌单数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                List<Song> list = JsonUtil.getList(response, Song.class);
                if(list==null){
                    return;
                }
                for(Song song : list){
                    MusicUtil.collect_song_ids.add(song.getId());
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //在这里对异常情况进行处理
                HttpUtil.failed(mContext,"查询收藏歌曲失败");
            }
        },null);

        url = HttpUtil.PLAYLIST_GETUSERPLAYLIST+ UserUtil.id;
        //获取歌单数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                MusicUtil.userPlayList = JsonUtil.getList(response, Playlist.class);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
        //将本地歌曲信息添加的musicutil
        List<Song> mSongList = LitePal.findAll(Song.class);
        if(mSongList !=null&&mSongList.size()>0){
            for(Song song : mSongList){
                MusicUtil.local_song_ids.add(song.getId());
            }
        }
        //加载上次播放歌曲进度
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String str = sharedPreferences.getString("song","");
        MusicUtil.musicBind.setCurrentIndex(sharedPreferences.getInt("currentIndex",0));
        MusicUtil.musicBind.setCurrentTime(sharedPreferences.getInt("currentTime",0));
        List<Song> list = new ArrayList<>();
        if(str.length()>0){
            Gson gson = new Gson();
            try {
                JSONArray array = new JSONArray(str);
                for(int i=0;i<array.length();i++){
                    Song song = gson.fromJson(array.getJSONObject(i).toString(),Song.class);
                    list.add(song);
                }
                if(list.size()==0){
                    return;
                }
                MusicUtil.musicBind.setSongs(list);
                MusicUtil.musicBind.setSong(list.get(MusicUtil.musicBind.getCurrentIndex()));
                MusicUtil.musicBind.recoverSong(list.get(MusicUtil.musicBind.getCurrentIndex()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        mContext = this;
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setElevation(0);
        //侧边导航与导航组件
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        //注册toolbar点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_search) {
                    Objects.requireNonNull(getSupportActionBar()).hide();
                    mNavController.navigate(R.id.action_global_nav_search);
                }else if(item.getItemId() == R.id.action_add){
                    mNavController.navigate(R.id.action_global_nav_add);
                }
                return false;
            }
        });
    }
    private void initService() {

        //音乐下载服务
        mIntentDownload = new Intent(this, DownLoadService.class);
        startService(mIntentDownload);
        bindService(mIntentDownload,connectionDownload,Context.BIND_AUTO_CREATE);
    }

    public void login_out(MenuItem item){
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        //获取SharedPreferences.Editor对象
        SharedPreferences.Editor edit=  sharedPreferences.edit();
        //添加数据
        edit.putString("token","");
        //提交
        edit.apply();
        // 跳转到登录界面
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void getScreenDisplay(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        MusicUtil.screen_weight = displayMetrics.widthPixels;
        MusicUtil.screen_height = displayMetrics.heightPixels;
    }

    public void update_version(MenuItem item){
        //SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        //int version = sharedPreferences.getInt("version",0);
        Toast.makeText(mContext,"已是最新版本",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存退出时音乐上下文
        saveMusicContext();
        //销毁服务
        /*unbindService(connection);
        stopService(mIntent);*/
        unbindService(connectionDownload);
        stopService(mIntentDownload);
    }
    //保存音乐上下文，用于打开应用恢复 音乐关闭时的状态
    private void saveMusicContext(){
        //获取SharedPreferences 对象
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取SharedPreferences.Editor对象
        SharedPreferences.Editor edit=  sharedPreferences.edit();
        List<Song> songs = MusicUtil.musicBind.getSongs();

        String str = new Gson().toJson(songs);
        //添加数据
        edit.putString("song",str);
        edit.putInt("currentIndex",MusicUtil.musicBind.getCurrentIndex());
        edit.putInt("currentTime",MusicUtil.musicBind.getCurrentTime());
        //提交
        edit.apply();
    }

    public void changeTheme(View view){
        Button button = (Button) view;
        String text = (String) button.getText();
        changeThemeByName(text);
        //获取SharedPreferences 对象
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取SharedPreferences.Editor对象
        SharedPreferences.Editor edit=  sharedPreferences.edit();
        //添加数据
        edit.putString("theme",text);
        //提交
        edit.apply();
        //重启程序
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        //android.os.Process.killProcess(android.os.Process.myPid());

    }
}
