package com.android.music.util;

import android.util.Log;

import com.android.music.bean.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

/**
 * Created by 皮皮虾出击 on 2024/4/5.
 * qq邮箱： 1272447726@qq.com
 * Describe:
 */
public class JsonUtil {
    private static Gson gson = new Gson();

    //泛型 用于获取各种列表
    public static <T> List<T> getList(Response response,Class<T> tClass){
        List<T> list = new ArrayList<>();
        try {
            //得到服务器返回的具体内容
            String responseData = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(responseData);
            Log.d("-----",response.request().url()+jsonObject.toString());
            if(!jsonObject.getBoolean("success")){
                return null;
            }
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray tLists = jsonObject.getJSONArray("items");
            for (int i=0;i<tLists.length();i++){

                jsonObject = tLists.getJSONObject(i);

                T t = gson.fromJson(jsonObject.toString(), tClass);
                list.add(t);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //获取歌单里的歌曲
    public static List<Song> getSongList(Response response){
        List<Song> list = new ArrayList<>();
        //得到服务器返回的具体内容
        try {
            String responseData = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(responseData);
            if(!jsonObject.getBoolean("success")){
                return null;
            }
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("songs");
            for (int i=0;i<jsonArray.length();i++) {
                jsonObject = jsonArray.getJSONObject(i);
                Song song = gson.fromJson(jsonObject.toString(),Song.class);
                list.add(song);
            }
        }catch(JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
