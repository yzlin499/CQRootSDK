package top.yzlin.CQRoot.cqinfo;

/**
 * 管理变动
 */
final public class GroupAdminChangeEventInfo extends EventInfo {
    /**
     * 被取消管理
     */
    final public static int SUBTYPE_CANCEL = 1;
    /**
     * 被设置为管理
     */
    final public static int SUBTYPE_BESET = 2;

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
