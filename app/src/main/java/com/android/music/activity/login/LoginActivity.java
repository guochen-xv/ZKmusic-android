package com.android.music.activity.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.music.R;
import com.android.music.activity.MainActivity;
import com.android.music.annotation.ViewById;
import com.android.music.base.BaseActivity;
import com.android.music.util.HttpUtil;
import com.android.music.util.StateUtil;
import com.android.music.util.UserUtil;
import com.android.music.util.ViewBind;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private Context mContext;
    private final int  REQUEST_CODE_STORE = 1;
    // 关联用户手机号、密码和登录、注册按钮.
    @ViewById(R.id.login_mobile)
    private EditText userMobile;
    @ViewById(R.id.login_pwd)
    private EditText passWord;
    @ViewById(R.id.login_btn)
    private Button loginBtn;
    @ViewById(R.id.login_register)
    private Button registerBtn;
    private String token;
    private MyHandler handler = new MyHandler(this);
    class MyHandler extends Handler{
        // 弱引用 ，防止内存泄露
        private WeakReference<LoginActivity> weakReference;
        MyHandler(LoginActivity handlerMemoryActivity){
            weakReference = new WeakReference<>(handlerMemoryActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 通过  软引用  看能否得到activity示例
            LoginActivity loginActivity = weakReference.get();
            // 防止内存泄露
            if ( loginActivity != null) {
                // 如果当前Activity，进行UI的更新
                switch (msg.what){
                    case StateUtil.LOGIN_SUCCESS:
                        Toast.makeText(mContext, "登录成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case StateUtil.LOGIN_FAILED:
                        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case StateUtil.USER_SUCCESS:
                        // 跳转到主界面
                        Intent intent = new Intent(mContext, MainActivity.class);

                        startActivity(intent);
                        finish();
                    default:
                        break;
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewBind.inJect(this);
        initView();
        initEvent();
        initPermission();
        if(IsToken()){
            // 获取用户信息
            UserUtil.token = token;
            getUserInfo();
        }
    }
    private void initPermission() {
        //请求权限
        if (ContextCompat.checkSelfPermission (LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{ Manifest. permission. WRITE_EXTERNAL_STORAGE},REQUEST_CODE_STORE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝权限将无法使用程序哦", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void initView() {
        mContext = this;
    }
    private void initEvent(){
        // 登录按钮监听器
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户名和密码
                String strUserMobile = userMobile.getText().toString().trim();
                String strPassWord = passWord.getText().toString().trim();
                String url = HttpUtil.USER_LOGIN;
                HashMap<String,String> map = new HashMap<>();
                map.put("mobile",strUserMobile);
                map.put("password",strPassWord);
                HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String data = Objects.requireNonNull(response.body()).string();
                            JSONObject jsonObject = new JSONObject(data);
                            Message message = new Message();
                            if(jsonObject.getBoolean("success")){
                                String token = jsonObject.getJSONObject("data").getString("token");
                                Log.d("token",token);
                                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                                //获取SharedPreferences.Editor对象
                                SharedPreferences.Editor edit=  sharedPreferences.edit();
                                //添加数据
                                edit.putString("token",token);
                                //提交
                                edit.apply();
                                message.what = StateUtil.LOGIN_SUCCESS;
                                handler.sendMessage(message);
                                UserUtil.token = token;
                                getUserInfo();
                            }else{
                                message.what = StateUtil.LOGIN_FAILED;
                                message.obj = jsonObject.getJSONObject("data").getString("message");
                                handler.sendMessage(message);
                                passWord.setText("");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        HttpUtil.failed(mContext,"网络请求失败-登录界面");
                    }
                },map);
            }
        });
        // 注册按钮监听器
        registerBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转到注册界面
                        Intent intent = new Intent(mContext, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
    //判断是否有token
    private boolean IsToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
        return token.length() > 0;
    }

    private void getUserInfo(){
        String url = HttpUtil.USER_GETINFO;
        HttpUtil.getUserInfo(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(responseData).getJSONObject("data").getJSONObject("user");
                    UserUtil.id = jsonObject.getString("id");
                    UserUtil.name = jsonObject.getString("name");
                    UserUtil.pic = jsonObject.getString("avatar");
                    UserUtil.mobile = jsonObject.getString("phoneNum");
                    UserUtil.intro = jsonObject.getString("introduction");
                    Message message = new Message();
                    message.what = StateUtil.USER_SUCCESS;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //在这里对异常情况进行处理
                HttpUtil.failed(mContext,"查询用户失败");
            }
        },null,UserUtil.token);
    }
}
