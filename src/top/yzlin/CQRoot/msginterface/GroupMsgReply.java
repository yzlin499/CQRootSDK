package top.yzlin.CQRoot.msginterface;

import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;

/**
 * 群信息回复
 */
public interface GroupMsgReply {
    boolean filter(GroupMsgInfo from);
    String replyMsg(GroupMsgInfo a);
}
