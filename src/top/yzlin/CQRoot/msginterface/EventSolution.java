package top.yzlin.CQRoot.msginterface;

import top.yzlin.CQRoot.cqinfo.AbstractInfo;

public interface EventSolution<T extends AbstractInfo> {
    void msgSolution(T Msg);
}
