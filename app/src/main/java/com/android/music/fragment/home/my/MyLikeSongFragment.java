package com.android.music.fragment.home.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.adapter.SongListAdapter;
import com.android.music.bean.Song;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;
import com.android.music.util.StateUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class MyLikeSongFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Song> mSongList;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == StateUtil.COLLECT_SONG) {
                if (mSongList != null) {
                    SongListAdapter adapter = new SongListAdapter(mContext, mSongList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(mContext, "赶紧收藏一首歌再来这里吧", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_like_song, container, false);
        initView();
        initMyLikeSong();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        mRecyclerView = view.findViewById(R.id.like_song_recycle_view);
    }
    //从数据库加载"我喜欢"的歌曲
    private void initMyLikeSong() {
        String url = HttpUtil.COLLECT_SONG+"/"+ UserUtil.id;
        //获取歌单数据
        HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                mSongList = JsonUtil.getList(response,Song.class);
                Message message = new Message();
                message.what = StateUtil.COLLECT_SONG;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //在这里对异常情况进行处理
                HttpUtil.failed(mContext,"查询下载歌曲失败");
            }
        },null);
    }
}
