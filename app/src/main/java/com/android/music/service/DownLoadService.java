package com.android.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.music.R;
import com.android.music.asyncTask.DownloadAsyncTask;
import com.android.music.bean.Song;
import com.android.music.interfaces.IDownload;
import com.android.music.util.ApplicationUtil;
import com.android.music.util.HttpUtil;
import com.android.music.util.MusicUtil;
import com.android.music.util.UserUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class DownLoadService extends Service {
    private DownloadAsyncTask mDownloadAsyncTask;
    private NotificationManager manager;
    private IDownload mIDownload = new IDownload() {

        @Override
        public void onProgress(int progress) {
            Notification notification = getNotification("正在下载音乐",progress);
            manager.notify(2,notification);
        }

        @Override
        public void onSuccess() {
            mDownloadAsyncTask = null;
            stopForeground(true);
            Notification notification = getNotification("下载成功",100);
            manager.notify(2,notification);
            final Song song = mDownLoadBinder.getSong();
            String url = HttpUtil.DOWNLOAD_ADD;
            HashMap<String,String> map = new HashMap<>();
            map.put("songId", song.getId());
            map.put("userId", UserUtil.id);
            map.put("url", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()
                    +"/"+song.getName()+"-"+song.getSingerName()+".mp3");
            //更新媒体数据库
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(map.get("url")));
            intent.setData(uri);
            Log.d("DownloadAsyncTask",map.get("url"));
            ApplicationUtil.getContext().sendBroadcast(intent);
            //获取歌单数据
            HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    MusicUtil.download_song_ids.add(song.getId());
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    //在这里对异常情况进行处理
                    HttpUtil.failed(ApplicationUtil.getContext(),"add-download-song-failed");
                }
            },map);
        }

        @Override
        public void onFailed() {
            mDownloadAsyncTask = null;
            stopForeground(true);
            Notification notification = getNotification("下载失败",-1);
            manager.notify(2,notification);
        }

        @Override
        public void onPaused() {
            mDownloadAsyncTask = null;
            //Notification notification = getNotification("暂停下载",-1);
        }

        @Override
        public void onCanceled() {
            mDownloadAsyncTask.cancelDownload();
            mDownloadAsyncTask = null;
            stopForeground(true);
        }
    };
    private DownLoadBinder mDownLoadBinder = new DownLoadBinder();
    private String downloadUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        //创建通道id
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("DownloadService", "DownloadService", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            manager.createNotificationChannel(channel);
        }
    }
    private Notification getNotification(String title, int progress) {
       /* Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent, 0);*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DownLoadService.this,"DownloadService")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.app_img)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.loading_spinner))
                .setProgress(100,progress,false)
                .setAutoCancel(true);
        if (progress > 0) {
            //当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%") ;
            builder.setProgress(100,progress,false) ;
        }
        return builder.build();
    }

    public class DownLoadBinder extends Binder {
        private Song mSong;//下载的歌曲
        public void startDownload(String url,String filename,String storeDir) {
            if (mDownloadAsyncTask == null&&url!=null&&filename!=null) {
                downloadUrl = url;
                mDownloadAsyncTask = new DownloadAsyncTask(mIDownload);
                mDownloadAsyncTask.execute(downloadUrl,filename,storeDir);
               // mSong.setDownloadUrl(storeDir+"/zkMusic/"+filename);
                startForeground(2, getNotification("Downloading...", 0));
            }
        }
        /*public void pauseDownload() {
            if (mDownloadAsyncTask != null) {
                mDownloadAsyncTask.pauseDownload();
            }
        }
        public void cancelDownload() {
            if (mDownloadAsyncTask!= null) {
                mDownloadAsyncTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    //取消下载时需将文件删除，并将通知关闭
                    manager.cancel(1);
                    stopForeground(true);
                }
            }
        }*/

        public Song getSong() {
            return mSong;
        }
        public void setSong(Song song) {
            mSong = song;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mDownLoadBinder;
    }
}
