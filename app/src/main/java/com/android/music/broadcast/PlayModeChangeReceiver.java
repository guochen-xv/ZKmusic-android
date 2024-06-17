package com.android.music.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.music.interfaces.IMusicView;

public class PlayModeChangeReceiver extends BroadcastReceiver {
    private IMusicView mIMusicView;
    public PlayModeChangeReceiver(IMusicView iMusicView) {
        this.mIMusicView = iMusicView;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        mIMusicView.songChange();
    }
}
