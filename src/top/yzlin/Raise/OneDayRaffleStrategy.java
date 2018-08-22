package top.yzlin.Raise;

import top.yzlin.tools.Tools;

import java.util.HashSet;

/**
 * 一天抽一次的策略
 */
public class OneDayRaffleStrategy implements RaiseStrategy, Runnable {
    private static class t {
        private static OneDayRaffleStrategy instance = new OneDayRaffleStrategy();
    }

    private OneDayRaffleStrategy() {
        new Thread(this).start();
    }

    public static OneDayRaffleStrategy getInstance() {
        return t.instance;
    }

    HashSet<String> nameSet = new HashSet<>();
    boolean bu = false;

    @Override
    public boolean raiseName(String name) {
        return nameSet.add(name);
    }

    @Override
    public boolean nextRaffle() {
        return bu = !bu;
    }

    @Override
    public void run() {
        while (true) {
            nameSet.clear();
            Tools.print("数据重置了");
            Tools.sleep(Tools.todayRemainTime() / 200 + 30000);
        }
    }
}
