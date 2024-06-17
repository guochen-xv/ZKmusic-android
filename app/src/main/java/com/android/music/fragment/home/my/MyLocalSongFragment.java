package com.android.music.fragment.home.my;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.adapter.SongListAdapter;
import com.android.music.bean.Song;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

/**
*author: 皮皮虾出击
*createTime:2024/4/16
*description:本地歌曲
**/
public class MyLocalSongFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Song> mSongList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_local_song, container, false);
        initView();
        initLocalSong();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        mRecyclerView = view.findViewById(R.id.local_song_recycle_view);
    }
    private void initLocalSong(){
        mSongList = LitePal.findAll(Song.class);
        if(mSongList !=null&&mSongList.size()>0){
            //逆序
            Collections.reverse(mSongList);
            SongListAdapter adapter = new SongListAdapter(mContext, mSongList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(mContext,"本地歌曲需要存储权限才能使用哦", Toast.LENGTH_SHORT).show();
        }

    }
}
