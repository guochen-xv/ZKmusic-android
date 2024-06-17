package com.android.music.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {
    /**
     * id : 1
     * singerId : 1
     * singerName : android
     * name : 欢乐斗地主
     * introduction : 华语新时代歌曲华语新时代歌曲华语新时代歌曲华语新时代歌曲华语新时代歌曲华语新时代歌曲华语新时代歌曲华语新时代歌曲
     * pic : http://android-music.oss-cn-beijing.aliyuncs.com/android-music/songPic/25a1f6621b22401bab38bf0a403d5051file.png
     * lyric : [00:00:00]此歌曲为没有填词的纯音乐，请您欣赏
     * transLyric : null
     * url : http://android-music.oss-cn-beijing.aliyuncs.com/android-music/song/5bcfee9546414ec1afe2d51511dac16d纯音乐 - 欢乐斗地主.mp3
     * publishTime : 2021-01-10
     * subjectId : 1
     * price : 10
     * licenseNum : 10
     * commentNum : 412
     * createTime : null
     * updateTime : 2021-06-01 16:41:52
     * isDeleted : 0
     */
    @SerializedName("litepal_id")
    private int id;

    @SerializedName("id")
    private String songId;

    private String singerId;
    private String singerName;
    private String name;
    private String introduction;
    private String pic;
    private String lyric;
    private Object transLyric;
    private String url;
    private String publishTime;
    private int subjectId;
    private int price;
    private int licenseNum;
    private int commentNum;
    private Object createTime;
    private String updateTime;
    private int isDeleted;

    public void setLitePalId(int id) {
        this.id = id;
    }
    public int getLitePalId(){
        return this.id;
    }
    public String getId() {
        return songId;
    }
    public void setId(String id) {
        this.songId = id;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public Object getTransLyric() {
        return transLyric;
    }

    public void setTransLyric(Object transLyric) {
        this.transLyric = transLyric;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(int licenseNum) {
        this.licenseNum = licenseNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
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

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", songId='" + songId + '\'' +
                ", singerId='" + singerId + '\'' +
                ", singerName='" + singerName + '\'' +
                ", name='" + name + '\'' +
                ", introduction='" + introduction + '\'' +
                ", pic='" + pic + '\'' +
                ", url='" + url + '\'' +
                ", subjectId=" + subjectId +
                ", createTime=" + createTime +
                ", updateTime='" + updateTime + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
