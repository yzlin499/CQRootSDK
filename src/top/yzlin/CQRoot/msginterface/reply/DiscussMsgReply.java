package top.yzlin.CQRoot.msginterface.reply;

import top.yzlin.CQRoot.cqinfo.DiscussMsgInfo;
import top.yzlin.CQRoot.msginterface.DiscussMsgSolution;

public interface DiscussMsgReply extends DiscussMsgSolution, ReplySolution<DiscussMsgInfo> {

    /**
     * 方法无效
     *
     * @param Msg
     */
    @Override
    default void msgSolution(DiscussMsgInfo Msg) {
    }

    @Override
    default boolean filter(DiscussMsgInfo from) {
        return fromQQ(from.getFromQQ()) &&
                fromDiscuss(from.getFromDiscuss()) &&
                checkMsg(from.getMsg());
    }

    default boolean fromQQ(String from) {
        return true;
    }

    default boolean fromDiscuss(String from) {
        return true;
    }

    default boolean checkMsg(String from) {
        return true;
    }

    String replyMsg(DiscussMsgInfo a);
}
