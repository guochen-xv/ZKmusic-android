package com.android.music.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music.R;
import com.android.music.activity.MainActivity;
import com.android.music.adapter.HistorySearchAdapter;
import com.android.music.adapter.HotSearchAdapter;
import com.android.music.adapter.SongListAdapter;
import com.android.music.bean.SearchKey;
import com.android.music.bean.Song;
import com.android.music.interfaces.IonItemClick;
import com.android.music.util.HttpUtil;
import com.android.music.util.JsonUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SearchFragment extends Fragment {
    private Context mContext;
    private View view;

    private RecyclerView recyclerView ;//存放搜索歌曲的recycleview
    private SwipeRefreshLayout refreshLayout;//实现上拉加载的SwipeRefreshLayout
    private LinearLayoutManager linearLayoutManager;//存放搜索歌曲的recycleview的manager
    private SongListAdapter SearchSongAdapter;//存放搜索歌曲的Adapter
    private int VisibleItemIndex;//当前滚动item的索引

    private EditText editText;//搜索框
    private String w="" ;//查询歌曲的关键字

    private LinearLayout linearLayout;//存放历史记录的layout
    private List<SearchKey> list;//历史搜索记录
    private HistorySearchAdapter adapter;//历史记录的适配器
    private RecyclerView historyView ;//存放历史记录的recycleview

    private final int UPDATE_HOTKEY = 1;
    private final int UPDATE_SONGS = 2;
    private final int REFRESH_SONGS = 3;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_HOTKEY:
                    break;
                //查询歌曲
                case UPDATE_SONGS:
                    linearLayoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    SearchSongAdapter= new SongListAdapter(mContext,mSongs);
                    recyclerView.setAdapter(SearchSongAdapter);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private List<Song> mSongs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_search,container,false);
        initView();
        initEvent();
        return view;
    }

    private void initView(){
        linearLayout = view.findViewById(R.id.search_history_linear_layout);
        recyclerView = view.findViewById(R.id.search_song_list);
        editText = view.findViewById(R.id.search_edit_text);
        historyView = view.findViewById(R.id.search_user_history_recycler_view);
        refreshLayout = view.findViewById(R.id.search_refresh);
        initHotKey();
        initHistoryKey();
    }
    private void initEvent() {
        //点击返回
        ImageView imageView = view.findViewById(R.id.search_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回退到上一个页面
                getActivity().onBackPressed();
            }
        });
        //回车搜索
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //回车键搜索歌词
                    String key= editText.getText().toString();
                    searchSong(key);
                }
                return true;
            }
        });
        //监听文本框变化事件
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if("".equals(s.toString())){
                    //输入框字符为空
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //清空历史记录
        TextView cleanView = view.findViewById(R.id.search_clean_history);
        cleanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAllData();
            }
        });
        //上拉加载
        refreshLayout.setColorSchemeColors(R.attr.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //处理网络请求等耗时操作,这里我没有实现刷新功能故省略

                //操作处理完成后，通知刷新结束
                refreshLayout.setRefreshing(false);
            }
        });
        //下拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && VisibleItemIndex + 1 == SearchSongAdapter.getItemCount()) {
                    refreshLayout.setRefreshing(true);
                    refreshSong();
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                VisibleItemIndex = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }
    //从网络加载热搜词
    private void initHotKey(){
        //TODO 实际应该根据业务情况决定热搜词，这里写死
        //加载热搜词
        final List<String> hotKeys = new ArrayList<>();
        hotKeys.add("双笙");
        hotKeys.add("许嵩");
        hotKeys.add("千本樱");
        hotKeys.add("年少有为");
        hotKeys.add("塞壬唱片");
        hotKeys.add("起风了");
        hotKeys.add("宝贝快睡");
        hotKeys.add("PDD洪荒之力");
        hotKeys.add("说谎");
        RecyclerView hotView = view.findViewById(R.id.search_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
        hotView.setLayoutManager(gridLayoutManager);
        HotSearchAdapter adapter = new HotSearchAdapter(mContext, hotKeys);
        adapter.setIonItemClick(new IonItemClick() {
            @Override
            public void onClick(int position) {
                searchSong(hotKeys.get(position));
            }
        });
        hotView.setAdapter(adapter);
    }
    //根据关键字搜索歌曲 返回的结果包括歌手，歌单，歌曲
    private void searchSong(String key){
        key = key.trim();
        if(!"".equals(key)&&!key.equals(w)){
            w=key;
            Map<String,String> map = new ArrayMap<>();
            map.put("key",w);
            HttpUtil.sendRequestGet(HttpUtil.SONG_SEARCH,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    mSongs = JsonUtil.getSongList(response);
                    Message message = new Message();
                    message.what = UPDATE_SONGS;
                    handler.sendMessage(message);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HttpUtil.failed(mContext,"搜索歌曲失败");
                }
            },map);
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            //将搜索框里的文字改为key
            editText.setText(key);
            //将搜索记录添加到历史记录里
            SearchKey searchKey = new SearchKey(key);
            adapter.addData(searchKey);
        }
    }
    //上拉加载歌曲
    private void refreshSong(){
        //这里取消的原因为，本地数据库歌曲样本太少，很少有分页的情况

    }
    //从数据库加载历史记录
    private void initHistoryKey(){
        list = LitePal.findAll(SearchKey.class);
        if(list!=null){
            final int size = list.size();
            adapter = new HistorySearchAdapter(mContext,list);
            adapter.setIonItemClick(new IonItemClick() {
                @Override
                public void onClick(int position) {
                    String key = list.get(size-1-position).getKey();
                    searchSong(key);
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            historyView.setLayoutManager(layoutManager);
            historyView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.getSupportActionBar().show();
    }
}
