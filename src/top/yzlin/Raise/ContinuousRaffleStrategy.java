package top.yzlin.Raise;

public class ContinuousRaffleStrategy implements RaiseStrategy {
    private static class t {
        private static ContinuousRaffleStrategy instance = new ContinuousRaffleStrategy();
    }

    private ContinuousRaffleStrategy() {
    }

    public static ContinuousRaffleStrategy getInstance() {
        return t.instance;
    }

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
