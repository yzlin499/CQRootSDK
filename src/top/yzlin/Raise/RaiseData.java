package top.yzlin.Raise;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Objects;

public class RaiseData {
    public static RaiseData empty;

    static {
        empty = new RaiseData();
        empty.userID = "";
    }

    private String userID;
    private String nickName = "";
    private double raiseMoney = 0;
    private long payTime = 0;

    @JSONField(name = "user_id")
    public String getUserID() {
        return userID;
    }

    @JSONField(name = "user_id")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JSONField(name = "nickname")
    public String getNickName() {
        return nickName;
    }

    @JSONField(name = "nickname")
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @JSONField(name = "backer_money")
    public double getRaiseMoney() {
        return raiseMoney;
    }

    @JSONField(name = "backer_money")
    public void setRaiseMoney(double raiseMoney) {
        this.raiseMoney = raiseMoney;
    }

    @JSONField(name = "pay_time", format = "yyyy-MM-dd HH:mm:ss")
    public long getPayTime() {
        return payTime;
    }

    @JSONField(name = "pay_time", format = "yyyy-MM-dd HH:mm:ss")
    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    @Override
    public String toString() {
        return "RaiseData{" +
                "userID='" + userID + '\'' +
                ", nickName='" + nickName + '\'' +
                ", raiseMoney='" + raiseMoney + '\'' +
                ", payTime=" + payTime +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaiseData)) return false;
        RaiseData raiseData = (RaiseData) o;
        return payTime == raiseData.payTime &&
                Objects.equals(userID, raiseData.userID) &&
                Objects.equals(nickName, raiseData.nickName) &&
                Objects.equals(raiseMoney, raiseData.raiseMoney);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, nickName, raiseMoney, payTime);
    }
}
