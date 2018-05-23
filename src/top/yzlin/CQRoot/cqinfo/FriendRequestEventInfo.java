package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class FriendRequestEventInfo extends EventInfo{
    private String msg;
    private String responseFlag;
    protected FriendRequestEventInfo(JSONObject text) {
        super(text);
        this.msg=text.getString("msg");
        this.responseFlag=text.getString("responseFlag");
    }

    final public String getMsg() {
        return msg;
    }

    final public String getResponseFlag() {
        return responseFlag;
    }
}
