package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class GroupAdminChangeEventInfo extends EventInfo {
    private String fromGroup;
    private String beingOperateQQ;
    protected GroupAdminChangeEventInfo(JSONObject text) {
        super(text);
        this.fromGroup=text.getString("fromGroup");
        this.beingOperateQQ=text.getString("beingOperateQQ");
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public String getBeingOperateQQ() {
        return beingOperateQQ;
    }
}
