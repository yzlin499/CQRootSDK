package top.yzlin.CQRoot.cqinfo;

final public class GroupMsgInfo extends MsgInfo {
    private String fromGroup = "";
    private String fromAnonymous = "";
    private String username = "";
    private String fromGroupName = "";

    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getFromAnonymous() {
        return fromAnonymous;
    }

    public void setFromAnonymous(String fromAnonymous) {
        this.fromAnonymous = fromAnonymous;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFromGroupName() {
        return fromGroupName;
    }

    public void setFromGroupName(String fromGroupName) {
        this.fromGroupName = fromGroupName;
    }

    @Override
    public String toString() {
        return "GroupMsgInfo{" +
                "fromGroup='" + fromGroup + '\'' +
                ", fromAnonymous='" + fromAnonymous + '\'' +
                ", username='" + username + '\'' +
                ", fromGroupName='" + fromGroupName + '\'' +
                '}';
    }
}
