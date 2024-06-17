package com.android.music.interfaces;

import com.android.music.bean.Song;

/**
 * Created by 皮皮虾出击 on 2024/4/13.
 * qq邮箱： 1272447726@qq.com
 * Describe:控制音乐播放的接口
 */
public interface IMusic {
    void play();
    void pause();
    void changeSong(Song song);
    void nextSong();
    void lastSong();
    void seekTo(int position);
    void like(Song song);
    void dislike(Song song);
}
