package top.yzlin.CQRoot.cqinfo;


public abstract class AbstractInfo {
    private int act = -1;
    private String fromQQ = "";
    private long sendTime;
    private int subType = -1;

    public int getAct() {
        return act;
    }

    public void setAct(int act) {
        this.act = act;
    }

    public String getFromQQ() {
        return fromQQ;
    }

    public void setFromQQ(String fromQQ) {
        this.fromQQ = fromQQ;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }
}
