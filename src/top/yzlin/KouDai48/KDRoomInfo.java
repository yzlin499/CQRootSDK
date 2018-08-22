package top.yzlin.KouDai48;

/**
 * 自定义信息发送方式
 * 发送时间
 * 发送人
 * 发送信息
 * 当为普通信息时为普通信息
 * 当为图片时为图片的短地址
 * 当为语音时为语音的短地址
 * 当为直播时为直播的短地址
 * 当为翻牌信息时为小偶像的回复
 * "text"普通信息
 * "faipaiText"翻牌信息
 * "live"直播开了
 * "audio"语音信息
 * "image"图片信息
 * "idolFlip"付费翻牌
 * 一般为空字符串，只有当有翻牌信息时才有，信息为翻牌的内容
 */
public class KDRoomInfo {
    /**
     * 消息时间
     */
    private long msgTime;
    /**
     * 发送消息人
     */
    private String sender;
    /**
     * 如果为文字就为文字，如果为图片或语音就为图片地址,如果是翻牌信息就是小偶像的信息
     */
    private String msg;
    /**
     * 消息类型
     */
    private KDRoomType msgType;
    /**
     * 额外文本，一般是翻牌才会有，否则为空
     */
    private String text = "";

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public KDRoomType getMsgType() {
        return msgType;
    }

    public void setMsgType(KDRoomType msgType) {
        this.msgType = msgType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "KDRoomInfo{" +
                "msgTime=" + msgTime +
                ", sender='" + sender + '\'' +
                ", msg='" + msg + '\'' +
                ", msgType=" + msgType +
                ", text='" + text + '\'' +
                '}';
    }
}
