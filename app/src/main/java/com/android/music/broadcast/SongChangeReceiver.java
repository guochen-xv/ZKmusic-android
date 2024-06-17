package com.android.music.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.music.interfaces.IMusicView;
import com.android.music.util.StateUtil;

public class SongChangeReceiver extends BroadcastReceiver {
    private IMusicView mIMusicView;
    public SongChangeReceiver(IMusicView iMusicView) {
        this.mIMusicView = iMusicView;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("state", StateUtil.CHANGE_SONG);
        mIMusicView.songChange();
        mIMusicView.musicStateChange(state);
    }
}
