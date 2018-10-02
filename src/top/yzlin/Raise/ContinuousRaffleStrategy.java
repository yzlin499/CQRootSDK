package top.yzlin.Raise;

/**
 * 通用普通连抽策略
 */
public class ContinuousRaffleStrategy implements RaiseStrategy {
    private double raiseMoney;
    private double minLimit;

    @Override
    public boolean isRaiseMoney(double raiseMoney, double minLimit) {
        this.raiseMoney = raiseMoney;
        this.minLimit = minLimit;
        return raiseMoney >= minLimit;
    }

    @Override
    public boolean raiseName(String name) {
        return true;
    }

    @Override
    public boolean nextRaffle() {
        boolean bu = raiseMoney >= minLimit;
        raiseMoney -= minLimit;
        return bu;
    }
}
