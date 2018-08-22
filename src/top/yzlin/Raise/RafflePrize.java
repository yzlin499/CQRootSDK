package top.yzlin.Raise;

/**
 * 抽奖的实体类
 * 设置奖品与中奖几率
 *
 * @param <T>
 */
public class RafflePrize<T> implements Comparable<RafflePrize> {
    private T prize;
    private int probability;


    private String picturePath;


    public RafflePrize(T prize, int probability) {
        this.prize = prize;
        this.probability = probability;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public T getPrize() {
        return prize;
    }

    public void setPrize(T prize) {
        this.prize = prize;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @Override
    public int compareTo(RafflePrize o) {
        return Integer.compare(this.getProbability(), o.getProbability());
    }
}
