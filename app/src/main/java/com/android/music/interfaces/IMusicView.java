package com.android.music.interfaces;

/**
 * Created by 皮皮虾出击 on 2024/4/13.
 * qq邮箱： 1272447726@qq.com
 * Describe:改变音乐界面的接口
 */
public interface IMusicView {
    void songChange();//当歌曲改变时，界面更新
    void musicStateChange(int state);//播放暂停，界面更新
}
