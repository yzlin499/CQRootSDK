package top.yzlin.Raise;

import top.yzlin.tools.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreCardPoolRaffleRaise extends RaffleRaise {

    private List<RandomRaise> randomRaises = new ArrayList<>();

    public MoreCardPoolRaffleRaise(String GID) {
        super(GID);
    }

    public MoreCardPoolRaffleRaise(AbstractMonitoring am) {
        super(am);
    }

    public void addRandomRaise(RandomRaise... randomRaise) {
        for (RandomRaise raise : randomRaise) {
            minLimit = raise.getMinLimit() < minLimit ? raise.getMinLimit() : minLimit;
            randomRaises.add(raise);
        }
        randomRaises.sort((o1, o2) -> Double.compare(o2.getMinLimit(), o1.getMinLimit()));
    }

    @Override
    public String sendText(String name, double money) {
        if (money < minLimit) {
            return "";
        }
        StringBuilder str = new StringBuilder().append('\n').append(upText).append('\n');
        RafflePrize tr;
        StringBuilder log = new StringBuilder();
        RafflePrize min = RafflePrize.empty;
        Map<RafflePrize, Integer> raise = new HashMap<>();
        for (RandomRaise randomRaise : randomRaises) {
            double rMoney = randomRaise.getMinLimit();
            while (money >= rMoney) {
                money -= rMoney;
                tr = randomRaise.raffle();
                saveData(name, tr);
                if (tr.getProbability() < min.getProbability()) {
                    min = tr;
                }
                raise.put(tr, raise.containsKey(tr) ? raise.get(tr) + 1 : 1);
                log.append(tr.getPrize());
                log.append(' ');
            }
        }
        raise.forEach((k, v) -> str.append(k.getPrize()).append('×').append(v).append('\n'));
        if (listFunction != null) {
            str.append(listFunction.apply(min)).append('\n');
        }
        Tools.print(name + "抽到了" + log.toString());
        str.append(downText);
        return str.toString();
    }
}
