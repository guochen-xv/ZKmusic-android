package com.android.music.util;

import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.music.R;
import com.android.music.bean.Playlist;
import com.android.music.bean.Song;
import com.android.music.interfaces.IMusic;
import com.android.music.service.DownLoadService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 皮皮虾出击 on 2024/4/19.
 * qq邮箱： 1272447726@qq.com
 * Describe:定义音乐相关的全局变量
 */
public class MusicUtil {
    public static DownLoadService.DownLoadBinder downLoadBinder;
    public static HashSet<String> download_song_ids = new HashSet<>();
    public static HashSet<String> collect_song_ids = new HashSet<>();
    public static HashSet<String> local_song_ids = new HashSet<>();
    public static List<Playlist> userPlayList = new ArrayList<>();
    public static int screen_height ;
    public static int screen_weight ;
    public static MusicBind musicBind = new MusicBind();
    public static class MusicBind implements IMusic, Serializable {
        private MediaPlayer mediaPlayer = new MediaPlayer();//唯一MediaPlayer
        private boolean isplay = false;                     //当前是否正在播放音乐
        private Song song = null;                           //当前正在播放的歌曲
        private List<Song> songs = new ArrayList<>();       //当前播放歌曲上下文中，包含的所有歌曲
        private int currentIndex =0;                        //当前播放歌曲的索引
        private String imageUrl="";                         //当前歌曲的图片
        private List<String> songLyric;                     //歌词
        private List<Float> timeLyric;                      //歌词对应的时间
        private int duration;                               //歌曲总时长
        private int currentTime;                            //当前播放的时间

        private List<Song> songsPlay = new ArrayList<>();   //歌曲播放列表
        private int play_mode=1;
        MusicBind() {
            //当前歌曲播放完毕后自动播放下一曲
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(songs.size()>0){
                        nextSong();
                    }
                }
            });
        }

        @Override
        public void play() {
            if(!mediaPlayer.isPlaying()&&mediaPlayer.getDuration()>0){
                mediaPlayer.start();
                isplay = true;
                //添加到历史听歌记录
                addHistorySong(song);
            }
        }
        public void recover() {
            mediaPlayer.pause();
            isplay = false;
            setSongs(new ArrayList<Song>());
            setSongsPlay(new ArrayList<Song>());
            setIsplay(false);
            setSong(new Song());
            setCurrentTime(0);
        }
        public void removeSong(int index){
            if(index<currentIndex){
                songsPlay.remove(index);
                currentIndex--;
            }else if(currentIndex==index){
                Toast.makeText(ApplicationUtil.getContext(), R.string.dialog_del_failed,Toast.LENGTH_SHORT).show();
            }else{
                songsPlay.remove(index);
            }
        }
        public void addPlaySong(Song song){
            if(songsPlay.size()==0){
                songsPlay.add(song);
            }else{
                for(int i=0;i<songsPlay.size();i++){
                    if(songsPlay.get(i).getId().equals(song.getId())){
                        songsPlay.remove(i);
                        break;
                    }
                }
                songsPlay.add(currentIndex+1,song);
            }
        }
        @Override
        public void pause() {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                isplay = false;
            }
        }
        void addHistorySong(Song song){
            String url = HttpUtil.HISTORY_ADD;
            HashMap<String,String> map = new HashMap<>();
            map.put("songId", song.getId());
            map.put("userId",UserUtil.id);
            //获取歌单数据
            HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response){

                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(ApplicationUtil.getContext(),"添加历史歌曲记录失败");
                }
            },map);
        }

        @Override
        public void changeSong(Song song) {
            setImageUrl(song.getPic());
            setSong(song);
            initLyric();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ApplicationUtil.getContext(), "该歌曲需要VIP才能播放~~~", Toast.LENGTH_SHORT).show();
                return;
            }

            setDuration(mediaPlayer.getDuration()/1000);
            //发送广播，通知底部音乐栏更新
            Intent intent = new Intent("com.android.music.broadcast.SongChangeReceiver");
            intent.putExtra("state", StateUtil.CHANGE_SONG);
            intent.setComponent(new ComponentName("com.android.music","com.android.music.broadcast.SongChangeReceiver"));
            LocalBroadcastManager.getInstance(ApplicationUtil.getContext()).sendBroadcast(intent);
        }

        //复原歌曲
        public void recoverSong(Song song){
            setImageUrl(song.getPic());
            setSong(song);
            initLyric();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.prepare();
                setDuration(mediaPlayer.getDuration()/1000);
                mediaPlayer.seekTo(currentTime*1000);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ApplicationUtil.getContext(), "该歌曲需要VIP才能播放~~~", Toast.LENGTH_SHORT).show();
                return;
            }
            //发送广播，通知底部音乐栏更新
            Intent intent = new Intent("com.android.music.broadcast.SongChangeReceiver");
            intent.putExtra("state", StateUtil.PAUSE_SONG);
            intent.setComponent(new ComponentName("com.android.music","com.android.music.broadcast.SongChangeReceiver"));
            LocalBroadcastManager.getInstance(ApplicationUtil.getContext()).sendBroadcast(intent);

        }
        @Override
        public void nextSong() {
            if(songsPlay.size()==0){
                return;
            }
            if(play_mode==0){
                mediaPlayer.seekTo(0);
                return;
            }
            currentIndex = currentIndex+1>=songsPlay.size()?0:currentIndex+1;
            song = songsPlay.get(currentIndex);
            changeSong(song);
        }

        @Override
        public void lastSong() {
            if(songsPlay.size()==0){
                return;
            }
            if(play_mode==0){
                mediaPlayer.seekTo(0);
                return;
            }
            currentIndex = currentIndex-1<0?songsPlay.size()-1:currentIndex-1;
            song = songsPlay.get(currentIndex);
            changeSong(song);
        }

        @Override
        public void seekTo(int position) {
            int seekToTime = position*duration*10;
            mediaPlayer.seekTo(seekToTime);
        }

        @Override
        public void like(final Song song) {
            String url = HttpUtil.COLLECT_ADD;
            HashMap<String,String> map = new HashMap<>();
            map.put("songId", song.getId());
            map.put("userId", UserUtil.id);
            //获取歌单数据
            HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    collect_song_ids.add(song.getId());
                    HttpUtil.success(ApplicationUtil.getContext(),"歌曲已添加至我喜欢");
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(ApplicationUtil.getContext(),"歌曲已添加至我喜欢-失败");
                }
            },map);
        }

        @Override
        public void dislike(final Song song) {
            String url = HttpUtil.COLLECT_DELETE+"/"+UserUtil.id+"/"+song.getId();
            //获取歌单数据
            HttpUtil.sendRequestDelete(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    collect_song_ids.remove(song.getId());
                    HttpUtil.success(ApplicationUtil.getContext(),"歌曲已从我喜欢移出");
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(ApplicationUtil.getContext(),"歌曲已从我喜欢移出-失败");
                }
            },null);
        }


        public void songItemClick(final int index, final Song song, final List<Song> songs){

            //如果当前点击歌曲不一致
            if(getSong()==null||!song.getId().equals(getSong().getId())){
                int k  = 0;
                if(!isEqual(songs,getSongs())){
                    songsPlay = new ArrayList<>(songs);
                    if(play_mode==2) {
                        Collections.shuffle(songsPlay);
                    }
                }
                if(play_mode==2){
                    for(int i = 0;i<songsPlay.size();i++){
                        if(songsPlay.get(i).getId().equals(song.getId())){
                            k = i;
                            break;
                        }
                    }
                }
                final int randomIndex = k;

                setSongs(songs);
                if(play_mode==2){
                    setCurrentIndex(randomIndex);
                }else{
                    setCurrentIndex(index);
                }
                changeSong(song);
            }
        }

        //获取歌词
        void initLyric(){
            songLyric = new ArrayList<>();
            timeLyric = new ArrayList<>();
            String lyric = song.getLyric();
            //匹配时间的正则表达式[00:00.00(最后的至少2个0)
            String reg = "^\\[\\d{2}:\\d{2}([.:])\\d{2,}";
            if(lyric!=null){
                String[] lyrics = lyric.split("\n");
                //Log.d(Tag,lyric);
                for (String item:lyrics) {
                    String[] strs = item.trim().split("]");
                    if(!Pattern.matches(reg,strs[0])){
                        //Log.d(Tag,strs[0]);
                        continue;
                    }
                    //处理时间，使之变成浮动数
                    String[] time = strs[0].substring(1).split(":");
                    int min =Integer.parseInt(time[0])*60;//时间 分
                    float sec = Float.parseFloat(time[1]);//时间 秒
                    float timeSec = min + sec;//总时间保留2位小数
                    //处理歌词
                    if(strs.length>1){
                        String lrc = strs[1];
                        songLyric.add(lrc);
                        timeLyric.add(timeSec);
                        //Log.d("lry", lrc);
                    }
                }
            }

        }
        //更新歌词
        public String updateLyric(){

            if(mediaPlayer!=null){
                currentTime = mediaPlayer.getCurrentPosition()/1000;
                int index = 1;
                for(int i=index;i<timeLyric.size();i++){
                    if(timeLyric.get(i)>currentTime){
                        // this.word = this.timeLyric[i]+this.lyric[i-1]+currentTime
                        index = i;
                        return songLyric.get(i-1);
                    }
                }

                if(songLyric.size()>0){
                    return songLyric.get(songLyric.size()-1);
                } else {
                    return "";
                }
            }else {
                return "";
            }
        }


        public Song getSong() {
            return song;
        }
        public void setSong(Song song) {
            this.song = song;
        }
        public List<Song> getSongs() {
            return songs;
        }
        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }
        public int getCurrentIndex() {
            return currentIndex;
        }
        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }
        public String getImageUrl() {
            return imageUrl==null?"":imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public boolean isIsplay() {
            return isplay;
        }
        public void setIsplay(boolean isplay) {
            this.isplay = isplay;
        }
        public List<String> getSongLyric() {
            return songLyric;
        }
        public void setSongLyric(List<String> songLyric) {
            this.songLyric = songLyric;
        }
        public List<Float> getTimeLyric() {
            return timeLyric;
        }
        public void setTimeLyric(List<Float> timeLyric) {
            this.timeLyric = timeLyric;
        }
        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public int getCurrentTime() {
            return currentTime;
        }
        public void setCurrentTime(int currentTime) {
            this.currentTime = currentTime;
        }
        public List<Song> getSongsPlay() {
            return songsPlay;
        }

        public void setSongsPlay(List<Song> songsPlay) {
            this.songsPlay = songsPlay;
        }

        public int getPlay_mode() {
            return play_mode;
        }

        public void setPlay_mode(int play_mode) {
            this.play_mode = play_mode;
        }

        boolean isEqual(List<Song> s1, List<Song> s2) {
            if(s1.size()!=s2.size()){
                return false;
            }else{
                for(int i=0;i<s1.size();i++){
                    if(!s1.get(i).equals(s2.get(i))){
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
