package top.yzlin.weibo;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import top.yzlin.tools.Tools;

import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;

public class WeiBo {

    private String param;
    private String name;

    public WeiBo(String name) {
        JSONObject jo = getUIDAndVerifier(name);
        if (jo != null) {
            param = "isTitle=0&noborder=0&uid=" +
                    jo.getString("weibo_uid") + "&verifier=" +
                    jo.getString("weibo_verifier") + "&isFans=0&isWeibo=1";
        }
        this.name = name;
    }

    public WeiBo(String name, String uid, String verifier) {
        this.name = name;
        param = "isTitle=0&noborder=0&uid=" + uid + "&verifier=" + verifier + "&isFans=0&isWeibo=1";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeiBoInfo[] getWeiBoData() {
        return getWeiBoData(w -> true);
    }

    public WeiBoInfo[] getWeiBoData(Predicate<WeiBoInfo> predicate) {
        return Jsoup.parse(Tools.sendGet("http://widget.weibo.com/weiboshow/index.php", param))
                .getElementById("weibo_list")
                .getElementsByClass("weiboShow_mainFeed_list")
                .stream()
                .map(e -> {
                    WeiBoInfo weiBoInfo = new WeiBoInfo();
                    weiBoInfo.setUrl(e.attr("gosrc"));
                    weiBoInfo.setText(e.getElementsByClass("weiboShow_mainFeed_listContent_txt").get(0).text());
                    String time[] = e.getElementsByClass("weiboShow_mainFeed_listContent_actionTime").get(0).child(0).text().split("[^\\d]+");
                    Calendar calendar = Calendar.getInstance();
                    switch (time.length) {
                        case 1:
                            calendar.add(Calendar.MINUTE, -Integer.parseInt(time[0]));
                            break;
                        case 3:
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[1]));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(time[2]));
                            break;
                        case 4:
                            calendar.set(Calendar.MONTH, Integer.parseInt(time[0]) - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time[1]));
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[2]));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(time[3]));
                            break;
                        default:
                            Tools.print("出现不能预测的代码:" + e.getElementsByClass("weiboShow_mainFeed_listContent_actionTime").get(0));
                    }
                    weiBoInfo.setDate(calendar.getTime());
                    Elements elements = e.getElementsByClass("relay_user_words");
                    if (elements.size() > 0) {
                        weiBoInfo.setRepost(true);
                        weiBoInfo.setRepostText(elements.get(0).text().substring(1));
                    } else {
                        weiBoInfo.setRepost(false);
                    }
                    return weiBoInfo;
                })
                .filter(predicate)
                .toArray(WeiBoInfo[]::new);
    }

    public static JSONObject getUIDAndVerifier(String name) {
        String memberData = Tools.sendGet("http://h5.snh48.com/resource/jsonp/members.php", "gid=00&callback=get_members_success");
        List<JSONObject> jo = JSONObject.parseObject(
                memberData.substring(memberData.indexOf('(') + 1, memberData.lastIndexOf(')'))
        ).getJSONArray("rows").toJavaList(JSONObject.class);
        for (JSONObject j : jo) {
            if (name.equals(j.getString("sname"))) {
                return j;
            }
        }
        Tools.print("查无此人:" + name);
        return null;
    }
}
