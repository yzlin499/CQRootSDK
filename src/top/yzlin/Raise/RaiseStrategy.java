package top.yzlin.Raise;


/**
 * 关于抽奖的策略接口
 * <p>
 * 关于接口使用的布局
 * //判断是否进入抽奖与两个数据的传入
 * if(raiseStrategy.isRaiseMoney(moneyTemp,minLimit) && raiseStrategy.raiseName(name)){
 * //迭代
 * while(raiseStrategy.nextRaffle()){
 * //抽卡
 * str.append(raffle(name));
 * str.append('\n');
 * }
 * }
 *
 * @author
 */
public interface RaiseStrategy {
    /**
     * 在最开始的时候被调用，默认是当集资金额大于限定下限时就开始抽奖,作为数据的传入
     *
     * @param raiseMoney 集资进而
     * @param minLimit   限定金额下限
     * @return 是否要抽奖
     */
    default boolean isRaiseMoney(double raiseMoney, double minLimit) {
        return raiseMoney >= minLimit;
    }

    /**
     * 传入抽卡的名字
     *
     * @param name 此次被抽卡的名字
     */
    default boolean raiseName(String name) {
        return true;
    }

    /**
     * 抽奖进行几次
     * 在返回false之后，信息会发送出去
     *
     * @return 如果返回true就会一直抽奖，直到false停下
     */
    boolean nextRaffle();
}
