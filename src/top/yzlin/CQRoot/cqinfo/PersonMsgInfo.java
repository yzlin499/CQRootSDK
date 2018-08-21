package top.yzlin.CQRoot.cqinfo;

final public class PersonMsgInfo extends MsgInfo{
    /**
     * 消息来自好友
     */
    final public static int SUBTYPE_FRIEND = 11;
    /**
     * 消息来自在线状态（非好友）
     */
    final public static int SUBTYPE_ONLINE = 1;
    /**
     * 消息来自群
     */
    final public static int SUBTYPE_GROUP = 2;
    /**
     * 消息来自讨论组
     */
    final public static int SUBTYPE_DISCUSS = 2;
}
