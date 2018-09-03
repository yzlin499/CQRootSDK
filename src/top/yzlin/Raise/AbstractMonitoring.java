package top.yzlin.Raise;

import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

public abstract class AbstractMonitoring implements Runnable {
    //基础信息
    private String QQGID;
    private CQRoot QQMT;

    private RaiseEvent raiseEvent = null;
    //额外信息
    private String title;
    private String goalMoney;
    private String moneyUrl;
    private String endTime;
    private long frequency = 8000;

    protected AbstractMonitoring(String QQGID, CQRoot QQMT) {
        this.QQGID = QQGID;
        this.QQMT = QQMT;
    }

    final public void setRaiseEvent(RaiseEvent raiseEvent) {
        raiseEvent.transferInfo(QQGID, QQMT);
        this.raiseEvent = raiseEvent;
    }

    final protected void setGoalMoney(String goalMoney) {
        this.goalMoney = goalMoney;
    }

    final protected void setMoneyUrl(String moneyUrl) {
        this.moneyUrl = Tools.getTinyURL(moneyUrl);
    }

    final protected void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    final public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    final public long getFrequency() {
        return frequency;
    }

    final protected void setTitle(String title) {
        this.title = title;
    }


    /**
     * 发送信息
     *
     * @param raiseData
     */
    final void sendMsg(RaiseData raiseData, String nowMoney) {
        Tools.print("[" + title + "]项目" + raiseData.getNickName() + "资助" + raiseData.getRaiseMoney() + "元");
        QQMT.sendGroupMsg(QQGID,
                sendText(raiseData.getNickName(), raiseData.getRaiseMoney(), nowMoney, title, goalMoney, moneyUrl, endTime) +
                        (raiseEvent == null ? "" : raiseEvent.eventTrigger(raiseData.getNickName(), raiseData.getRaiseMoney())));
    }


    /**
     * 重写这个方法来自定义发送信息的样式
     *
     * @param name      集资人的名字
     * @param money     集资金额
     * @param nowMoney  当前集资进度
     * @param title     集资项目标题
     * @param goalMoney 目标金额
     * @param moneyUrl  集资链接的短地址
     * @param endTime   结束时间
     * @return 这个方法的返回值会发给这个群
     */
    protected String sendText(String name, double money, String nowMoney, String title, String goalMoney, String moneyUrl, String endTime) {
        return "感谢" + name + "聚聚支持了【" + title + "】" + money + "元\n" +
                "当前进度:" + nowMoney + "元,目标 ：" + goalMoney + "元\n" +
                "集资链接:" + moneyUrl;
    }

}
