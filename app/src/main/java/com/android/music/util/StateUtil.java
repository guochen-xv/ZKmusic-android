package com.android.music.util;

/**
 * Created by 皮皮虾出击 on 2024/4/14.
 * qq邮箱： 1272447726@qq.com
 * Describe:
 */
public class StateUtil {
    //歌曲播放状态
    public static final int CHANGE_SONG = 1;
    public static final int PLAY_SONG = 2;
    public static final int PAUSE_SONG =3 ;
    //下载歌曲的状态
    public static final int DOWNLOAD_SUCCESS = 4;
    public static final int DOWNLOAD_FAILED = 5;
    public static final int DOWNLOAD_CANCEL = 6;
    public static final int DOWNLOAD_PAUSE = 7;

    //登录注册的状态
    public static final int LOGIN_SUCCESS = 8;
    public static final int LOGIN_FAILED = 9;
    public static final int REGISTER_SUCCESS = 10;
    public static final int REGISTER_FAILED = 11;

    //获取用户信息
    public static final int USER_SUCCESS = 12;

    //获取用户历史听歌记录
    public static final int HISTORY_SONG = 13;
    //获取用户下载歌曲记录
    public static final int DOWNLOAD_SONG = 14;
    //我的页面歌单加载
    public static final int UPDATE_PLAYLIST_MY = 15;
    //音乐馆页面歌单加载
    public static final int UPDATE_PLAYLIST_HALL = 16;
    //初始化轮播图
    public static final int INIT_VIEW = 17;
    //加载排行榜
    public static final int UPDATE_RANK = 18;
    //获取用户收藏歌曲记录
    public static final int COLLECT_SONG = 19;
    //用户推荐歌曲
    public static final int INIT_RECOMMEND_SONG = 20;
}
