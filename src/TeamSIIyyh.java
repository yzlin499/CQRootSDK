import top.yzlin.CQRoot.CQRoot;
import top.yzlin.KouDai48.KDLiveTeamMonitoring;
import top.yzlin.Raise.ModianMonitoring;

public class TeamSIIyyh {
    public static void main(String[] args) {
        String gid = "461296095";
        CQRoot cqRoot = new CQRoot(25307);
        ModianMonitoring modianMonitoring = new ModianMonitoring("32872", gid, cqRoot) {
            @Override
            protected String sendText(String name, double money, String nowMoney, String title, String goalMoney, String moneyUrl, String endTime) {
                return "感谢" + name + "爸爸支持￥" + money + "元!\n" +
                        "让我们一起为了艾斯兔一起,爪拉爪的走下去!\n" +
                        "大家加油！！！冲鸭～\n" +
                        "￥" + nowMoney + "/￥" + goalMoney + "\n" +
                        "还差:￥" + (Double.parseDouble(goalMoney) - Double.parseDouble(nowMoney)) + "\n" +
                        "完成:" + String.format("%.2f", (Double.parseDouble(nowMoney) / Double.parseDouble(goalMoney) * 100)) + "%\n" +
                        ModianMonitoring.progressBar((Double.parseDouble(nowMoney) / Double.parseDouble(goalMoney) * 100)) + "\n" +
                        "集资链接:" + moneyUrl;
            }
        };
        KDLiveTeamMonitoring kdLiveTeamMonitoring = new KDLiveTeamMonitoring(cqRoot, gid, "TEAM SII");
    }
}
