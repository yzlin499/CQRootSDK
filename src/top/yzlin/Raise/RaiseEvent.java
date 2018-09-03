package top.yzlin.Raise;

import top.yzlin.CQRoot.CQRoot;

public interface RaiseEvent {
    //传递参数
    void transferInfo(String GID, CQRoot cqRoot);

    //设置集资监控的触发
    String eventTrigger(String name, double money);
}
