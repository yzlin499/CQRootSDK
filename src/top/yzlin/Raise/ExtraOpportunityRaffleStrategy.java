package top.yzlin.Raise;

public class ExtraOpportunityRaffleStrategy implements RaiseStrategy {

    private double raiseMoney;
    private double minLimit;

    private double minMoney;
    private int extraOpportunity;

    public ExtraOpportunityRaffleStrategy(double minMoney, int extraOpportunity) {
        this.minMoney = minMoney;
        this.extraOpportunity = extraOpportunity;
    }

    @Override
    public boolean isRaiseMoney(double raiseMoney, double minLimit) {
        if (raiseMoney >= minMoney) {
            raiseMoney += minLimit * extraOpportunity * ((int) (raiseMoney / minMoney));
        }
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
