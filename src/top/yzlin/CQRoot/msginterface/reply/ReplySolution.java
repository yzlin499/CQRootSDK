package top.yzlin.CQRoot.msginterface.reply;

import top.yzlin.CQRoot.cqinfo.MsgInfo;

public interface ReplySolution<T extends MsgInfo> {
    boolean filter(T from);

    String replyMsg(T a);
}
