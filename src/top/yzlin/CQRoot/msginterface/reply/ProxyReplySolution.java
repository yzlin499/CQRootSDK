package top.yzlin.CQRoot.msginterface.reply;

import top.yzlin.CQRoot.CQRoot;
import top.yzlin.CQRoot.cqinfo.DiscussMsgInfo;
import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.cqinfo.MsgInfo;
import top.yzlin.CQRoot.msginterface.EventSolution;

import java.util.function.Function;

public class ProxyReplySolution implements EventSolution<MsgInfo> {
    private ReplySolution replySolution;
    private Function<MsgInfo, Void> strategy;

    public ProxyReplySolution(CQRoot cqRoot, ReplySolution replySolution, int type) {
        this.replySolution = replySolution;
        switch (type) {
            case CQRoot.GET_GROUP_MSG:
                strategy = m -> {
                    String text = replySolution.replyMsg(m);
                    if (text != null) {
                        cqRoot.sendGroupMsg(((GroupMsgInfo) m).getFromGroup(), text);
                    }
                    return null;
                };
                break;
            case CQRoot.GET_PERSON_MSG:
                strategy = m -> {
                    String text = replySolution.replyMsg(m);
                    if (text != null) {
                        cqRoot.sendPersonMsg(m.getFromQQ(), text);
                    }
                    return null;
                };
                break;
            case CQRoot.GET_DISCUSS_MSG:
                strategy = m -> {
                    String text = replySolution.replyMsg(m);
                    if (text != null) {
                        cqRoot.sendDiscussMsg(((DiscussMsgInfo) m).getFromDiscuss(), text);
                    }
                    return null;
                };
                break;
        }
    }

    @Override
    public void msgSolution(MsgInfo Msg) {
        if (replySolution.filter(Msg)) {
            strategy.apply(Msg);
        }
    }
}
