package top.yzlin.CQRoot.cqinfo;

/**
 * 群成员减少
 */
final public class GroupMemberDecreaseEventInfo extends EventInfo{
    final public static int SUBTYPE_LEAVE = 1;
    final public static int SUBTYPE_BEFIRED = 2;
    final public static int SUBTYPE_OWN_BEFIRED = 3;

    private String fromGroup;
    private String beingOperateQQ;

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
