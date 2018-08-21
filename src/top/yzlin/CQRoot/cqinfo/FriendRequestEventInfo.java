package top.yzlin.CQRoot.cqinfo;

final public class FriendRequestEventInfo extends EventInfo{
    private String msg;
    private String responseFlag;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResponseFlag() {
        return responseFlag;
    }

    public void setResponseFlag(String responseFlag) {
        this.responseFlag = responseFlag;
    }
}
