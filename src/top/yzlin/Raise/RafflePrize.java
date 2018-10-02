package top.yzlin.Raise;

import java.util.Objects;

/**
 * 抽奖的实体类
 * 设置奖品与中奖几率
 *
 */
public class RafflePrize implements Comparable<RafflePrize> {
    static RafflePrize empty;

    static {
        empty = new RafflePrize("bug奖品", 0x7fffffff);
    }

    private String prize;
    private int probability;

    private String picturePath;

    public RafflePrize(String prize, int probability) {
        this.prize = prize;
        this.probability = probability;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @Override
    public String toString() {
        return "RafflePrize{" +
                "prize='" + prize + '\'' +
                ", probability=" + probability +
                ", picturePath='" + picturePath + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(prize, probability);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RafflePrize)) {
            return false;
        }
        RafflePrize that = (RafflePrize) o;
        return probability == that.probability &&
                Objects.equals(prize, that.prize) &&
                Objects.equals(picturePath, that.picturePath);
    }

    @Override
    public int compareTo(RafflePrize o) {
        return Integer.compare(this.getProbability(), o.getProbability());
    }
}
