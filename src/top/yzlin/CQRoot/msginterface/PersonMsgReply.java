package top.yzlin.CQRoot.msginterface;

import top.yzlin.CQRoot.cqinfo.PersonMsgInfo;

public interface PersonMsgReply {
    boolean filter(PersonMsgInfo from);
    String replyMsg(PersonMsgInfo a);
}
