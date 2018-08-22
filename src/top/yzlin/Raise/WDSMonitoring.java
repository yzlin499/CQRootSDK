package top.yzlin.Raise;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

/**
 * 因为微打赏的
 * 官方没有白名单
 * 接口
 */
public class WDSMonitoring extends AbstractMonitoring {
    private String post_id;

    private String infoURL;//储存项目详情的参数
    private String ordersParam;//储存集资列表的参数

    private class RaiseData {
        private String nickName;
        private String raiseMoney;

        private RaiseData(String nickName, String raiseMoney) {
            this.nickName = nickName;
            this.raiseMoney = raiseMoney;
        }
    }//储存信息类

    /**
     * 这是一个微打赏的集资的线程
     *
     * @param pro_id 集资项目的ID
     * @param QQGID  Q群的号码
     * @param QQMT   socket的链接
     */
    public WDSMonitoring(String pro_id, String QQGID, CQRoot QQMT) {
        super(QQGID, QQMT);
        infoURL = "https://wds.modian.com/show_weidashang_pro/" + pro_id;
        Document doc = Jsoup.parse(Tools.sendGet(infoURL, ""));
        Element rows = doc.child(0).child(1).child(0);
        setTitle(rows.getElementsByClass("title").get(0).text());
        setGoalMoney(rows.getElementsByClass("mon target")
                .get(0)
                .child(1)
                .text()
                .replaceAll("[^(\\d|.)]", ""));
        setMoneyUrl("https://wds.modian.com/show_weidashang_pro/" + pro_id);
        setEndTime(rows.getElementsByClass("right").get(0).child(0).text());
        post_id = rows.getElementsByAttribute("moxi_id").get(0).attr("moxi_id");
        ordersParam = "page=1&post_id=" + post_id + "&pro_id=" + pro_id;
        new Thread(this).start();
    }

//	public static void main(String args[]){
//        String infoURL="https://wds.modian.com/show_weidashang_pro/"+11028;
//        Document doc = Jsoup.parse(Tools.sendGet(infoURL, ""));
//        Element rows = doc.child(0).child(1).child(0);
//        System.out.println(rows.getElementsByClass("right").get(0).child(0)
//               .text()
//        );
//        String post_id=rows.getElementsByAttribute("moxi_id").get(0).attr("moxi_id");
//        String ordersParam ="page=1&post_id="+post_id+"&pro_id="+11028;
//    }


    //主要线程
    @Override
    public void run() {
        double nowMoney = getNowMoney();
        double oldMoney = nowMoney;
        double temp;
        while (true) {
            if (nowMoney > oldMoney) {
                temp = nowMoney - oldMoney;
                Tools.sleep(5000);
                for (RaiseData data : getData()) {
                    sendMsg(data.nickName, data.raiseMoney, String.valueOf(nowMoney));
                    temp -= Double.parseDouble(data.raiseMoney);
                    if (temp <= 0.01) {
                        break;
                    }
                }
                oldMoney = nowMoney;
            }
            Tools.sleep(getFrequency());
            nowMoney = getNowMoney();
        }
    }

    //获得当前集资进度
    private double getNowMoney() {
        return Double.parseDouble(Jsoup.parse(Tools.sendGet(infoURL, ""))
                .getElementsByClass("mon current")
                .get(0)
                .child(1)
                .text()
                .replaceAll("[^(\\d|.)]", ""));
    }

    //返回当前最新列表
    private RaiseData[] getData() {
        return Jsoup.parse(
                JSONObject.parseObject(Tools.sendPost("https://wds.modian.com/ajax/comment_list", ordersParam))
                        .getJSONObject("data")
                        .getString("html")
        ).getElementsByTag("li").stream()
                .filter(h -> !h.getElementsByClass("right")
                        .get(0)
                        .getElementsByTag("span")
                        .get(0)
                        .text()
                        .contains("分钟"))
                .map(h -> new RaiseData(
                        h.getElementsByClass("nick")
                                .text(),
                        h.getElementsByClass("nick_sup")
                                .text()
                                .replaceAll("支持了|元", "")))
                .toArray(RaiseData[]::new);
    }
}