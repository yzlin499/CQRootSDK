package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class GroupMemberDecreaseEventInfo extends EventInfo{
    private String fromGroup;
    private String beingOperateQQ;

    protected GroupMemberDecreaseEventInfo(JSONObject text) {
        super(text);
        this.fromGroup=text.getString("fromGroup");
        this.beingOperateQQ=text.getString("beingOperateQQ");
    }


    final public String getFromGroup() {
        return fromGroup;
    }

    final public String getBeingOperateQQ() {
        return beingOperateQQ;
    }
}
