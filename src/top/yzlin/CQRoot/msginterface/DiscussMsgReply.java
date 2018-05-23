package top.yzlin.CQRoot.msginterface;

import top.yzlin.CQRoot.cqinfo.DiscussMsgInfo;

public interface DiscussMsgReply {
    boolean filter(DiscussMsgInfo from);
    String replyMsg(DiscussMsgInfo a);
}
