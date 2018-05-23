package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class GroupMemberIncreaseEventInfo extends EventInfo {
    private String beingOperateQQ;
    protected GroupMemberIncreaseEventInfo(JSONObject text) {
        super(text);
        this.beingOperateQQ=text.getString("beingOperateQQ");
    }

    final public String getBeingOperateQQ() {
        return beingOperateQQ;
    }
}
