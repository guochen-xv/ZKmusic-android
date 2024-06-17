package com.android.music.fragment.home.my;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.music.R;


public class ZKFragment extends Fragment {
    private View view;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_zk, container, false);
        initView();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        EditText editText = view.findViewById(R.id.zk_editText);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                "  该软件实现功能如下：\n"+
                "  1.基本的音乐播放器功能，如播放，暂停，切换歌曲\n"+
                "  2.主题切换功能，提供6大主题，虽然只是简单的换了个颜色\n"+
                "  3.歌曲搜索以及搜索历史\n"+
                "  4.本地音乐扫描功能\n"+
                "  5.音乐进度条拖动功能\n"+
                "  6.收藏音乐\n"+
                "  7.歌词显示\n"+
                "  8.排行榜功能\n"+
                "  该软件耗费我不少心血，但个人能力有限，不如官方的音乐播放器，但是音乐播放器的基本功能大致实现了，希望使用本软件的用户能够喜欢\n\n\n"+
                "  版本优化：\n"+
                "  1.改变主题时黑屏问题改善\n"+
                "  2.部分页面引入缓存\n"+
                "  3.引入正确的加载图片和加载图片错误图标,但不是很合适~~说实话有点丑\n"+
                "  4.点击歌曲右侧出现弹窗\n"+
                "  5.保存用户退出时的歌曲状态和歌曲列表\n\n\n"+
                "  下一步计划：\n"+
                "  1.完善本地歌曲\n"+
                "  2.添加app更新功能\n"+
                "  3.完善歌单界面\n"+
                "  4.实现自建歌单功能\n"
        );
        editText.setText(stringBuilder.toString());
        editText = view.findViewById(R.id.zk_editText2);
        stringBuilder.setLength(0);
        editText.setText(stringBuilder.toString());
    }

}
