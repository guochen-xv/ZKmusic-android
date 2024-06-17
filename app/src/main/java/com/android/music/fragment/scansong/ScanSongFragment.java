package com.android.music.fragment.scansong;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.music.R;
import com.android.music.bean.Song;

import org.litepal.LitePal;

public class ScanSongFragment extends Fragment {

    private Button btn;
    private Context mContext;
    private View mView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_menu_scansong, container, false);
        initView();
        return mView;
    }
    private void initView(){
        mContext = mView.getContext();

        btn = mView.findViewById(R.id.scan_song_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanSong();
            }
        });
    }
    private void ScanSong(){
        // 媒体库查询语句
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String songId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                Song song =  LitePal.where("songid ="+songId).findFirst(Song.class);
                String str = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                if(str!=null){
                    if(Integer.parseInt(str)/1000<40){
                        continue;
                    }
                }
                if(song!=null){
                    continue;
                }else{
                    song = new Song();
                }
                song.setId(songId);
                song.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                //歌手名
                String singerName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌名
                String songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).split("-")[0];
                //专辑
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));// 设置封面图片路径

                song.setSingerName(singerName);
                song.setName(songName);
                Log.d("scansong",song.toString());
                song.save();
            }
            // 释放资源
            cursor.close();
        }
        Toast.makeText(mContext,"扫描歌曲成功",Toast.LENGTH_SHORT).show();
    }


}
