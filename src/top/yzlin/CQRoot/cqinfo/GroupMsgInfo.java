package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class GroupMsgInfo extends MsgInfo {
    private String fromGroup;
    private String fromAnonymous;
    private String username;
    private String fromGroupName;

    public GroupMsgInfo(JSONObject text){
        super(text);
        this.fromGroup=text.getString("fromGroup");
        this.fromAnonymous=text.getString("fromAnonymous");
        this.username=text.containsKey("username")?text.getString("username"):"";
        this.fromGroupName=text.getString("fromGroupName");
    }

    final public String getFromGroup() {
        return fromGroup;
    }

    final public String getFromAnonymous() {
        return fromAnonymous;
    }

    final public String getUsername() {
        return username;
    }

    final public String getFromGroupName() {
        return fromGroupName;
    }
}
