package com.android.music.interfaces;

/**
 * Created by 皮皮虾出击 on 2024/4/16.
 * qq邮箱： 1272447726@qq.com
 * Describe:下载的接口
 */
public interface IDownload {
    void onProgress(int progress);
    void onSuccess();
    void onFailed() ;
    void onPaused() ;
    void onCanceled();
}
