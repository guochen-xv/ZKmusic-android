package com.android.music.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.music.R;
import com.android.music.annotation.ViewById;
import com.android.music.util.HttpUtil;
import com.android.music.util.StateUtil;
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

public class RegisterActivity extends AppCompatActivity {
    private Context mContext;
    // 关联用户名、密码、确认密码、邮箱和注册、返回登录按钮
    @ViewById(R.id.UserNameEdit)
    private EditText userName;
    @ViewById(R.id.MobileEdit)
    private EditText userMobile;
    @ViewById(R.id.PassWordEdit)
    private EditText passWord;
    @ViewById(R.id.PassWordAgainEdit)
    private EditText passWordAgain;
    @ViewById(R.id.SignUpButton)
    private Button signUpButton;
    @ViewById(R.id.BackLoginButton)
    private Button backLoginButton;


    private MyHandler handler = new MyHandler(this);
    class MyHandler extends Handler{
        // 弱引用 ，防止内存泄露
        private WeakReference<RegisterActivity> weakReference;
        MyHandler(RegisterActivity handlerMemoryActivity){
            weakReference = new WeakReference<>(handlerMemoryActivity);
        }
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            // 通过  软引用  看能否得到activity示例
            RegisterActivity registerActivity = weakReference.get();
            // 防止内存泄露
            if (registerActivity != null) {
                // 如果当前Activity，进行UI的更新
                switch (msg.what){
                    case StateUtil.REGISTER_SUCCESS:
                        Toast.makeText(mContext, "注册成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case StateUtil.REGISTER_FAILED:
                        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewBind.inJect(this);
        initView();
        initEvent();

    }
    private void initView() {
        mContext = this;
    }
    private void initEvent() {
        // 立即注册按钮监听器
        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strUserName = userName.getText().toString().trim();
                        String strPassWord = passWord.getText().toString().trim();
                        String strMobile = userMobile.getText().toString().trim();
                        String strPassWordAgain = passWordAgain.getText().toString().trim();
                        //注册格式粗检
                        if (strUserName.length() > 10) {
                            Toast.makeText(mContext, "用户名长度必须小于10！", Toast.LENGTH_SHORT).show();
                        } else if (strUserName.length() < 3) {
                            Toast.makeText(mContext, "用户名长度必须大于3！", Toast.LENGTH_SHORT).show();
                        } else if (strPassWord.length() > 16) {
                            Toast.makeText(mContext, "密码长度必须小于16！", Toast.LENGTH_SHORT).show();
                        } else if (strPassWord.length() < 6) {
                            Toast.makeText(mContext, "密码长度必须大于6！", Toast.LENGTH_SHORT).show();
                        } else if (userMobile.length() != 11) {
                            Toast.makeText(mContext, "手机号长度必须等于11！", Toast.LENGTH_SHORT).show();
                        } else if (!strPassWord.equals(strPassWordAgain)) {
                            Toast.makeText(mContext, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                        } else {
                            String url = HttpUtil.USER_REGISTER;
                            // 获取用户名和密码
                            HashMap<String,String> map = new HashMap<>();
                            map.put("mobile",strMobile);
                            map.put("password",strPassWord);
                            map.put("nickname",strUserName);
                            HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    try {
                                        String data = Objects.requireNonNull(response.body()).string();
                                        JSONObject jsonObject = new JSONObject(data);
                                        Message message = new Message();
                                        if(jsonObject.getBoolean("success")){
                                            message.what = StateUtil.REGISTER_SUCCESS;
                                            handler.sendMessage(message);
                                            //跳转到登录页面
                                            Intent intent = new Intent(mContext,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            message.what = StateUtil.REGISTER_FAILED;
                                            message.obj = jsonObject.getJSONObject("data").getString("message");
                                            handler.sendMessage(message);
                                            passWord.setText("");
                                            userMobile.setText("");
                                            userName.setText("");
                                            passWordAgain.setText("");
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    HttpUtil.failed(mContext,"网络请求失败-注册界面");
                                }
                            },map);

                        }
                    }
                }
        );
        // 返回登录按钮监听器
        backLoginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转到登录界面
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

}
