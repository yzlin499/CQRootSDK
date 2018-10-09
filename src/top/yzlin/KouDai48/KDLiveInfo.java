package top.yzlin.KouDai48;

import java.util.Arrays;
import java.util.Date;


public class KDLiveInfo {
    public static final int TYPE_LIVE = 1;
    public static final int TYPE_RADIO = 2;


    private String liveId;
    private String title;
    private String subTitle;
    private String[] picPath;
    private Date startTime;
    private int memberId;
    private int liveType;
    private String streamPath;

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String[] getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        String[] s = picPath.split(",");
        this.picPath = new String[s.length];
        for (int i = 0; i < this.picPath.length; i++) {
            this.picPath[i] = "https://source.48.cn" + s[i];
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getLiveType() {
        return liveType;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public String getStreamPath() {
        return streamPath;
    }

    public void setStreamPath(String streamPath) {
        this.streamPath = streamPath;
    }

    @Override
    public String toString() {
        return "KDLiveInfo{" +
                "liveId='" + liveId + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", picPath='" + Arrays.toString(picPath) + '\'' +
                ", startTime=" + startTime +
                ", memberId=" + memberId +
                ", liveType=" + liveType +
                ", streamPath='" + streamPath + '\'' +
                '}';
    }
}
