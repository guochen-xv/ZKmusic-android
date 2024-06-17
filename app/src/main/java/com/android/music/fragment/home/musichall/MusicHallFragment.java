package com.android.music.fragment.home.musichall;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.adapter.MusicHallPlaylistAdapter;
import com.android.music.adapter.SongImgListAdapter;
import com.android.music.bean.Playlist;
import com.android.music.bean.Song;
import com.android.music.component.MyViewFlipper;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;
import com.android.music.util.StateUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class MusicHallFragment extends Fragment {
    private View view;
    private Context mContext;

    private List<String> linkUrl = new ArrayList<>();
    private List<String> picUrl = new ArrayList<>();

    private List<Playlist> mPlayList;
    private List<Playlist> mList = new ArrayList<>();
    List<Song> recommend_songs;
    private int current = 0;//当前页
    private int TotalPage ;//总页数
    private MusicHallPlaylistAdapter mMusicHallPlaylistAdapter;
    //private MyViewFlipper myViewFlipper;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case StateUtil.INIT_VIEW:
                    //初始化轮播图
                    MyViewFlipper myViewFlipper = view.findViewById(R.id.my_view_flipper);
                    myViewFlipper.setFlipInterval(5000);
                    myViewFlipper.setInAnimation(mContext,R.anim.right_in);
                    myViewFlipper.setOutAnimation(mContext,R.anim.right_out);
                    myViewFlipper.startFlipping();//轮播图自动播放
                    //初始化轮播图的圆点
                    LinearLayout linearLayout = view.findViewById(R.id.dot_linear_layout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10,0,0,0);//图片间距
                    for(int i=0;i<picUrl.size();i++){
                        ImageView view = new ImageView(mContext);
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
                    //添加轮播图子视图
                    for(int i=0;i<picUrl.size();i++){
                        ImageView iv = new ImageView(mContext);
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Glide.with(mContext)
                                .load(Uri.parse(picUrl.get(i)))
                                .centerCrop()
                                .placeholder(R.drawable.loading_spinner)
                                .error(R.drawable.loading_error)
                                .into(iv);
                        final String url = linkUrl.get(i);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("url",url);
                                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_blank_fragment,bundle);
                            }
                        });
                        myViewFlipper.addView(iv);
                    }
                    break;
                case StateUtil.UPDATE_PLAYLIST_HALL:
                    //更新playlist
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
                    RecyclerView recyclerView = view.findViewById(R.id.recycler_music_playlist);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(mMusicHallPlaylistAdapter);
                    break;
                case StateUtil.INIT_RECOMMEND_SONG:
                    if (recommend_songs != null) {
                        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_music_songs);
                        SongImgListAdapter adapter = new SongImgListAdapter(mContext, recommend_songs);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setAdapter(adapter);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music_hall, container, false);
        initView();
        initPageView();
        initPlayList();
        initRecommendSong();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        mMusicHallPlaylistAdapter = new MusicHallPlaylistAdapter(mContext, mList);
        Button btn = view.findViewById(R.id.music_hall_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current++;
                int page = (current)%TotalPage;
                changePlayList(page);
            }
        });



    }
    //初始化轮播图
    private void initPageView(){
        String url = HttpUtil.BANNER_GETNEWBANNER;
        if(linkUrl.size()>0){
            Message message = new Message();
            message.what = StateUtil.INIT_VIEW;
            handler.sendMessage(message);
            return;
        }
        //获取轮播图数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String data = Objects.requireNonNull(response.body()).string();
                    JSONArray jsonArray = new JSONObject(data).getJSONObject("data").getJSONArray("bannerList");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        linkUrl.add(jsonObject.getString("linkUrl"));
                        picUrl.add(jsonObject.getString("imageUrl"));
                    }
                    Message message = new Message();
                    message.what = StateUtil.INIT_VIEW;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }

    private void initRecommendSong(){

        String url = HttpUtil.RECOMMEND_CF+"/"+ UserUtil.id;
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                recommend_songs = JsonUtil.getList(response,Song.class);
                Message message = new Message();
                message.what = StateUtil.INIT_RECOMMEND_SONG;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }
    //初始化推荐歌单
    private void initPlayList(){
        if(mPlayList!=null){
            changePlayList(0);
            Message message = new Message();
            message.what = StateUtil.UPDATE_PLAYLIST_HALL;
            handler.sendMessage(message);
            return;
        }
        String url = HttpUtil.BASE_API+"/adminService/song-list/findAll";
        //获取歌单数据
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                mPlayList = JsonUtil.getList(response,Playlist.class);
                assert mPlayList != null;
                TotalPage = mPlayList.size()/6;
                changePlayList(0);
                Message message = new Message();
                message.what = StateUtil.UPDATE_PLAYLIST_HALL;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }
    private void changePlayList(int page){
        mList.clear();
        for(int i = page * 6;i<page*6+6;i++){
            mList.add(mPlayList.get(i));
        }
        mMusicHallPlaylistAdapter.changePlayListSimples(mList);
    }
}
