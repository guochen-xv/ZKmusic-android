package com.android.music.bean;

/**
 * Created by 移动应用开发三人组 on 2024/4/7.
 * qq邮箱： 1272447726@qq.com
 * Describe:
 */
public class Playlist {
    /**
     * id : 2651768083
     * creatorId : 39842094
     * title : 欧美电音 ⎢盐系性感沙绵女声
     * pic : http://p4.music.126.net/F-c2qL2bivNAbLGj_7Pc1g==/109951163835352873.jpg
     * introduction : 可盐可甜，可性感也可甜美。
     无缝切换，女生就是这么可爱又迷人。
     心有猛虎，嗷呜嗷呜。
     * style : null
     * commentNum : 6
     * collectNum : 1090
     * playNum : 82663
     * createTime : 2021-02-01 22:21:25
     * updateTime : 2021-02-01 22:21:25
     * isDeleted : 0
     */
    private String id;
    private String creatorId;
    private String title;
    private String pic;
    private String introduction;
    private Object style;
    private int commentNum;
    private int collectNum;
    private int playNum;
    private String createTime;
    private String updateTime;
    private int isDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Object getStyle() {
        return style;
    }

    public void setStyle(Object style) {
        this.style = style;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
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

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
