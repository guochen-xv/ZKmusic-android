package com.android.music.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:搜索的关键词
 */
public class SearchKey extends LitePalSupport {
    private String key;

    public SearchKey() {
    }

    public SearchKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
