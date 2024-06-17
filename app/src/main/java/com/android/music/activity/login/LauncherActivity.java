package com.android.music.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.music.R;

public class LauncherActivity extends AppCompatActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mContext = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, LoginActivity.class); //前者为跳转前页面，后者为跳转后页面
                startActivity(intent);
                finish();
            }
        }, 2000); //设置时间，3秒后自动跳转
    }
}
