package top.yzlin.CQRoot.msginterface.reply;

import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.msginterface.GroupMsgSolution;

/**
 * 群信息回复
 */
public interface GroupMsgReply extends GroupMsgSolution, ReplySolution<GroupMsgInfo> {
    /**
     * 方法无效
     *
     * @param Msg
     */
    @Override
    default void msgSolution(GroupMsgInfo Msg) {
    }

    @Override
    default boolean filter(GroupMsgInfo from) {
        return fromQQ(from.getFromQQ()) &&
                fromGroup(from.getFromGroup()) &&
                checkMsg(from.getMsg());
    }

    default boolean fromQQ(String from) {
        return true;
    }

    default boolean fromGroup(String from) {
        return true;
    }

    default boolean checkMsg(String from) {
        return true;
    }

    String replyMsg(GroupMsgInfo a);
}
