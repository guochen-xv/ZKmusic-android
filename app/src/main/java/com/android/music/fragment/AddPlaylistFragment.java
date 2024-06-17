package com.android.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.android.music.R;
import com.android.music.bean.Song;
import com.android.music.util.HttpUtil;
import com.android.music.util.UserUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class AddPlaylistFragment extends Fragment {
    private Context mContext;
    private View view;
    private List<Song> mList;
    private EditText et_title;
    private EditText et_intro;
    private EditText et_pic_url;
    private ImageView iv_playlist;
    private final int code = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_add_playlist,container,false);
        initView();
        initEvent();
        return view;
    }

    private void initView() {
        et_title = view.findViewById(R.id.add_playlist_title);
        et_intro = view.findViewById(R.id.add_playlist_intro);
        et_pic_url = view.findViewById(R.id.add_playlist_pic);
        iv_playlist = view.findViewById(R.id.add_playlist_iv);

    }

    private void initEvent() {
        view.findViewById(R.id.add_playlist_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString().trim();
                String intro = et_intro.getText().toString().trim();
                String pic_url = et_pic_url.getText().toString().trim();
                if(title.length()>0&&intro.length()>0&&pic_url.length()>0){
                    String url= HttpUtil.PLAYLIST_ADDPLAYLIST;
                    HashMap<String,String> map = new HashMap<>();
                    map.put("title",title);
                    map.put("introduction",intro);
                    map.put("pic",pic_url);
                    map.put("creatorId", UserUtil.id);
                    HttpUtil.sendRequestPost(url,new okhttp3.Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            HttpUtil.success(mContext,"新建歌单成功");
                        }
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            //在这里对异常情况进行处理
                            HttpUtil.failed(mContext,"新建歌单失败");
                        }
                    },map);
                }else{
                    Toast.makeText(mContext, "所填内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.add_select_pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, code);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == code) {
            // 从相册返回的数据
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    et_pic_url.setText(uri.toString());
                    Glide.with(mContext)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.loading_spinner)
                            .error(R.drawable.loading_error)
                            .into(iv_playlist);
                }
            }
        }
    }
}
