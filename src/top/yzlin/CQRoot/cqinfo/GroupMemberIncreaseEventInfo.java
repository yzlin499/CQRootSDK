package top.yzlin.CQRoot.cqinfo;

final public class GroupMemberIncreaseEventInfo extends EventInfo {
    final public static int SUBTYPE_APPROVAL = 1;
    final public static int SUBTYPE_INVITE = 2;

    private String beingOperateQQ;
    private String fromGroup;

    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getBeingOperateQQ() {
        return beingOperateQQ;
    }

    public void setBeingOperateQQ(String beingOperateQQ) {
        this.beingOperateQQ = beingOperateQQ;
    }
}
