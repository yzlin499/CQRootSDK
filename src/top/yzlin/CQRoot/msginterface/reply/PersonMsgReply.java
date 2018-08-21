package top.yzlin.CQRoot.msginterface.reply;

import top.yzlin.CQRoot.cqinfo.PersonMsgInfo;
import top.yzlin.CQRoot.msginterface.PersonMsgSolution;

public interface PersonMsgReply extends PersonMsgSolution, ReplySolution<PersonMsgInfo> {
    /**
     * 方法无效
     *
     * @param Msg
     */
    @Override
    default void msgSolution(PersonMsgInfo Msg) {
    }

    @Override
    default boolean filter(PersonMsgInfo from) {
        return fromQQ(from.getFromQQ()) && checkMsg(from.getMsg());
    }

    default boolean fromQQ(String from) {
        return true;
    }

    default boolean checkMsg(String from) {
        return true;
    }

    String replyMsg(PersonMsgInfo a);
}
