package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONObject;

final public class PersonMsgInfo extends MsgInfo{
    final public static int MSG_FORM_TYPE_FRIEND=11;
    final public static int MSG_FORM_TYPE_ONLINE=1;
    final public static int MSG_FORM_TYPE_GROUP=2;
    final public static int MSG_FORM_TYPE_DISCUSS=2;

    public PersonMsgInfo(JSONObject text) {
        super(text);
    }
}
