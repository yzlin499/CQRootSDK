package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;
import top.yzlin.CQRoot.CQRoot;

public abstract class AbstractInfo {
    private String act;
    private String fromQQ;
    private String sendTime;
    private String subType;

    protected AbstractInfo(JSONObject text){
        this.act=text.getString("act");
        this.sendTime=text.getString("sendTime");
        this.subType=text.getString("subType");
        if("102".equals(act) && "1".equals(subType)){
            this.fromQQ="";
        }else if("101".equals(act)){
            this.fromQQ="";
        }else{
            this.fromQQ=text.getString("fromQQ");
        }
    }

    final public String getAct() {
        return act;
    }

    /**
     * 当不存在该字段时，该字段为空字符串，即fromQQ="";
     * 一般是群事件-管理员变动与群事件-群成员减少（成员自己离开）才会有空
     * @return
     */
    final public String getFromQQ() {
        return fromQQ;
    }

    final public String getSendTime() {
        return sendTime;
    }

    final public String getSubType() {
        return subType;
    }

    public static AbstractInfo getInfo(JSONObject data){
        switch(data.getInt("act")){
            case CQRoot.GET_GROUP_MSG:
                return new GroupMsgInfo(data);
            case CQRoot.GET_DISCUSS_MSG:
                return new DiscussMsgInfo(data);
            case CQRoot.GET_PERSON_MSG:
                return new PersonMsgInfo(data);
            case CQRoot.GET_GROUP_ADMIN_CHANGE:
                return new GroupAdminChangeEventInfo(data);
            case CQRoot.GET_GROUP_MEMBER_DECREASE:
                return new GroupMemberDecreaseEventInfo(data);
            case CQRoot.GET_GROUP_MEMBER_INCREASE:
                return new GroupMemberIncreaseEventInfo(data);
            case CQRoot.GET_GROUP_REQUEST:
                return new GroupMemberRequestEventInfo(data);
            case CQRoot.GET_FRIEND_INCREASE:
                return new FriendIncreaseEventInfo(data);
            case CQRoot.GET_FRIEND_REQUEST:
                return new FriendRequestEventInfo(data);
        }
        return null;
    }

}
