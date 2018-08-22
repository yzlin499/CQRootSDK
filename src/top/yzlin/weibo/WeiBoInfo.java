package top.yzlin.weibo;

import java.util.Date;
import java.util.Objects;

public class WeiBoInfo {
    private String text;
    private String repostText;
    private String url;
    private boolean isRepost;
    private Date date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRepostText() {
        return repostText;
    }

    public void setRepostText(String repostText) {
        this.repostText = repostText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRepost() {
        return isRepost;
    }

    public void setRepost(boolean repost) {
        isRepost = repost;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeiBoInfo)) return false;
        WeiBoInfo weiBoInfo = (WeiBoInfo) o;
        return isRepost == weiBoInfo.isRepost &&
                (Math.abs(date.getTime() - weiBoInfo.date.getTime()) < 1000 * 120) &&
                Objects.equals(url, weiBoInfo.url) &&
                Objects.equals(text, weiBoInfo.text) &&
                Objects.equals(repostText, weiBoInfo.repostText);
    }

    @Override
    public String toString() {
        return "WeiBoInfo{" +
                "text='" + text + '\'' +
                (isRepost ? (", repostText='" + repostText + '\'') : "") +
                ", isRepost=" + isRepost +
                ", date=" + date +
                ", url='" + url + '\'' +
                '}';
    }
}
