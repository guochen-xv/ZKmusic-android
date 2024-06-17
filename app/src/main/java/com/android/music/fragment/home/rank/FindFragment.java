package com.android.music.fragment.home.rank;

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
import com.android.music.adapter.RankAdapter;
import com.android.music.bean.Rank;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;
import com.android.music.util.StateUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
//本来想做发现页面的,懒得写，就写个排行榜了
public class FindFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView recyclerView;
    private List<Rank> mRanks;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == StateUtil.UPDATE_RANK) {//更新排行榜
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                recyclerView = view.findViewById(R.id.recycler_top_playlist);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new RankAdapter(mContext, mRanks));
            }
            return false;
        }
    });
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find,container,false);
        mContext = view.getContext();
        recyclerView = view.findViewById(R.id.recycler_top_playlist);
        initTopList();
        return view;
    }
    //初始化排行榜
    private void initTopList() {
        String url = HttpUtil.RANK_FINDALL;
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                mRanks = JsonUtil.getList(response,Rank.class);
                Message message = new Message();
                message.what = StateUtil.UPDATE_RANK;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"网络请求失败-rank");
            }
        },null);
    }
}
