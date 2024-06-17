package com.android.music.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static OkHttpClient client = new OkHttpClient();
    public static String BASE_API = "http://10.0.2.2:8001";

    //playlist相关api
    private static String PLAYLIST_PRE = "/adminService/song-list";
    public static String PLAYLIST_FINDALL = BASE_API+PLAYLIST_PRE+"/findAll";
    public static String PLAYLIST_GETSONGLISTINFO = BASE_API+PLAYLIST_PRE+"/getSongListInfo/";
    public static String PLAYLIST_GETUSERPLAYLIST = BASE_API+PLAYLIST_PRE+"/getUserPlaylist/";
    public static String PLAYLIST_ADDPLAYLIST = BASE_API+PLAYLIST_PRE+"/addSongList";
    //list song相关
    private static String LISTSONG_PRE = "/adminService/list-song";
    public static String LISTSONG_ADD = BASE_API+LISTSONG_PRE+"/addSongToPlaylist";
    public static String LISTSONG_DELETE = BASE_API+LISTSONG_PRE+"/";
    //song相关api
    private static String SONG_PRE = "/adminService/song";
    public static String SONG_SEARCH = BASE_API+SONG_PRE+"/search";
    //banner相关api
    private static String BANNER_PRE = "/adminService/banner";
    public static String BANNER_GETNEWBANNER = BASE_API+BANNER_PRE+"/getNewBanner";

    //排行榜相关api
    private static String RANK_PRE = "/adminService/rank";
    public static String RANK_FINDALL = BASE_API+RANK_PRE+"/findAll";
    public static String RANK_GETSONGS = BASE_API+RANK_PRE+"/getRankSongs";

    //user相关api，如登录注册
    private static String USER_PRE = "/adminService/user";
    public static String USER_LOGIN = BASE_API+USER_PRE+"/login";
    public static String USER_REGISTER = BASE_API+USER_PRE+"/register";
    public static String USER_GETINFO = BASE_API+USER_PRE+"/getUserByToken";
    //用户历史听歌记录
    private static String HISTORY_PRE = "/adminService/user-history";
    public static String HISTORY_SONG = BASE_API+HISTORY_PRE+"/getUserHistorySongByUserId";
    public static String HISTORY_ADD = BASE_API+HISTORY_PRE+"/addUserHistory";
    //用户下载记录
    private static String DOWNLOAD_PRE = "/adminService/user-download";
    public static String DOWNLOAD_SONG = BASE_API+DOWNLOAD_PRE+"/getUserDownloadSongByUserId";
    public static String DOWNLOAD_ADD = BASE_API+DOWNLOAD_PRE+"/addUserDownload";
    public static String DOWNLOAD_DELETE = BASE_API+DOWNLOAD_PRE+"/deleteUserDownload";
    //用户收藏相关
    private static String COLLECT_PRE = "/adminService/collect";
    public static String COLLECT_SONG = BASE_API+COLLECT_PRE+"/getCollectSongByUserId";
    public static String COLLECT_ADD = BASE_API+COLLECT_PRE+"/addCollect";
    public static String COLLECT_DELETE = BASE_API+COLLECT_PRE+"/deleteCollect";
    //推荐相关
    private static String RECOMMEND_PRE = "/adminService/recommend";
    public static String RECOMMEND_CF = BASE_API+RECOMMEND_PRE+"/recommendByCF";

    public static void sendRequestPost(final String address, okhttp3.Callback callback,Map<String,String> map) {
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),jsonObject.toString());
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendRequestGet(final String address, okhttp3.Callback callback,Map<String,String> map) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(address).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendRequestDelete(final String address, okhttp3.Callback callback,Map<String,String> map) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(address).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build()).delete().build();
        client.newCall(request).enqueue(callback);
    }
    public static void getUserInfo(final String address, okhttp3.Callback callback,Map<String,String> map,String token) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(address)).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .addHeader("token",token)
                .url(urlBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendQQRequestGet(final String address, okhttp3.Callback callback,Map<String,String> map) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(address)).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("referer","https://c.y.qq.com/")
                .addHeader("host","c.y.qq.com")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendUQQRequestGet(final String address, okhttp3.Callback callback,Map<String,String> map) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(address).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("referer","https://u.y.qq.com/")
                .addHeader("host","u.y.qq.com")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                .build();
        client.newCall(request).enqueue(callback);
    }

    //出错处理
    public static void failed(Context context,String text){
        Looper.prepare();
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    //成功处理
    public static void success(Context context,String text){
        Looper.prepare();
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
