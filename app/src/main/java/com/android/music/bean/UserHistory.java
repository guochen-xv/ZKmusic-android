package com.android.music.bean;


public class UserHistory {

    /**
     * id : 2
     * songId : 1
     * userId : 2
     * createTime : 2021-12-08 18:26:06
     * updateTime : 2021-12-08 18:26:09
     * isDeleted : false
     */

    private String id;
    private String songId;
    private int userId;
    private String createTime;
    private String updateTime;
    private boolean isDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
