package com.android.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music.R;
import com.android.music.adapter.SongListAdapter;
import com.android.music.bean.Song;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SongListFragment extends Fragment {

    private Context mContext;
    private View view;
    private List<Song> mList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_song_list,container,false);
        initSongList();
        return view;
    }

    private final int UPDATE_SONGLIST = 1;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_SONGLIST) {//更新歌曲
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                RecyclerView recyclerView = view.findViewById(R.id.song_list_recycler);
                recyclerView.setLayoutManager(layoutManager);
                SongListAdapter adapter = new SongListAdapter(mContext, mList);
                recyclerView.setAdapter(adapter);
            }
            return false;
        }
    });

    //初始化歌单信息（包括歌单拥有的歌曲）
    private void initSongList(){
        Bundle bundle =  getArguments();
        String disssid = (String) bundle.get("dissid");
        //加载歌单的歌曲
        if(disssid!=null){
            String url = HttpUtil.PLAYLIST_GETSONGLISTINFO+disssid;
            //获取歌单数据
            HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    mList = JsonUtil.getSongList(response);
                    Message message = new Message();
                    message.what = UPDATE_SONGLIST;
                    handler.sendMessage(message);
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(mContext,"加载歌曲失败");
                }
            },null);
        }else {
            //加载排行榜的歌曲
            String id =  bundle.getString("id");
            String url = HttpUtil.RANK_GETSONGS+"/"+id;
            //获取歌单数据
            HttpUtil.sendRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response){
                    mList = JsonUtil.getList(response,Song.class);
                    Message message = new Message();
                    message.what = UPDATE_SONGLIST;
                    handler.sendMessage(message);
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(mContext,"加载排行榜歌曲失败");
                }
            },null);
        }

    }
}
