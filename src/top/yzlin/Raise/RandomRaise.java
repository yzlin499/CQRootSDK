package top.yzlin.Raise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomRaise {

    private static final Random RAND = new Random();
    private int probabilitySum;
    private double minLimit;
    private ArrayList<RafflePrize> rafflePrizeList = new ArrayList<>();

    public double getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(double minLimit) {
        this.minLimit = minLimit;
    }

    public int getProbabilitySum() {
        return probabilitySum;
    }

    public Random getRand() {
        return RAND;
    }

    public ArrayList<RafflePrize> getRafflePrizeList() {
        return rafflePrizeList;
    }

    public void setRafflePrizeList(ArrayList<RafflePrize> rafflePrizeList) {
        this.rafflePrizeList = rafflePrizeList;
    }

    public void addRafflePrize(RafflePrize... prize) {
        rafflePrizeList.addAll(Arrays.asList(prize));
        probabilitySum = rafflePrizeList.stream()
                .mapToInt(RafflePrize::getProbability)
                .sum();
    }


    public RafflePrize raffle() {
        int rand = RAND.nextInt(probabilitySum) + 1;
        int tempCount = 0;
        for (RafflePrize tempPrize : rafflePrizeList) {
            //随机位置
            tempCount += tempPrize.getProbability();
            if (tempCount >= rand) {
                return tempPrize;
            }
        }
        return rafflePrizeList.get(RAND.nextInt(rafflePrizeList.size()));
    }

    @Override
    public String toString() {
        return "RandomRaise{" +
                "probabilitySum=" + probabilitySum +
                ", minLimit=" + minLimit +
                ", rafflePrizeList=" + rafflePrizeList +
                '}';
    }
}
