package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class DiscussMsgInfo extends MsgInfo{
    private String fromDiscuss;

    public DiscussMsgInfo(JSONObject text) {
        super(text);
        this.fromDiscuss=text.getString("fromDiscuss");
    }

    final public String getFromDiscuss() {
        return fromDiscuss;
    }
}
