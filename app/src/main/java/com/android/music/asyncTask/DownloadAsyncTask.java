package com.android.music.asyncTask;

import android.os.AsyncTask;

import com.android.music.interfaces.IDownload;
import com.android.music.util.HttpUtil;
import com.android.music.util.StateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:下载歌曲的异步操作
 */
public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {
    private IDownload mIDownload;
    private boolean isPause = false;
    private boolean isCancelled = false;
    private int lastProcess = 0;

    public DownloadAsyncTask(IDownload IDownload) {
        mIDownload = IDownload;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream;
        RandomAccessFile saveFile;
        File file = null;
        String songUrl = strings[0];
        String fileName = strings[1];
        String storeDir = strings[2];
        long downloadLength = 0;

        try{

            file = new File(storeDir+"/"+fileName);
            if(file.exists()){
                downloadLength = file.length();
            }
            Request request = new Request.Builder()
                    //断点下载，指定从哪个字节开始下载
                    .addHeader( "RANGE", "bytes=" + downloadLength + "-")
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                    .url(songUrl)
                    .build();
            Response response = HttpUtil.client.newCall(request).execute();
            long contentLength = Objects.requireNonNull(response.body()).contentLength();
            if(contentLength==0){
                return StateUtil.DOWNLOAD_FAILED;
            }else if(contentLength == downloadLength){
                return StateUtil.DOWNLOAD_SUCCESS;
            }
            inputStream = response.body().byteStream();
            saveFile = new RandomAccessFile(file,"rw");
            saveFile.seek(downloadLength);
            byte[] all = new byte[1024];
            int len = -1;
            int total = 0;
            while ((len = inputStream.read(all))!=-1) {
                if(isCancelled){
                    return StateUtil.DOWNLOAD_CANCEL;
                }else if(isPause){
                    return StateUtil.DOWNLOAD_PAUSE;
                }else {
                    total+=len;
                    saveFile.write(all,0,len);
                    int process = (int) ((total+downloadLength)*100/contentLength);
                    publishProgress(process);
                }

            }
            Objects.requireNonNull(response.body()).close();
            inputStream.close();
            saveFile.close();
            return StateUtil.DOWNLOAD_SUCCESS;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(isCancelled()&&file!=null){
                file.delete();
            }
        }
        return StateUtil.DOWNLOAD_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
       int process = values[0];
       if(process>lastProcess){
           mIDownload.onProgress(process);
           lastProcess = process;
       }
    }

    @Override
    protected void onPostExecute(Integer state) {
        switch (state){
            case StateUtil.DOWNLOAD_SUCCESS:
                mIDownload.onSuccess();
                break;
            case StateUtil.DOWNLOAD_FAILED:
                mIDownload.onFailed();
                break;
            case StateUtil.DOWNLOAD_CANCEL:
                mIDownload.onCanceled();
                break;
            case StateUtil.DOWNLOAD_PAUSE:
                mIDownload.onPaused();
                break;
        }
    }
    public void pauseDownload(){
        isPause = true;
    }
    public void cancelDownload(){
        isCancelled = true;
    }
}
