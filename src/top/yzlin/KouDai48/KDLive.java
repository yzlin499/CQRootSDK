package top.yzlin.KouDai48;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.yzlin.tools.Tools;

import java.util.function.Predicate;


public class KDLive {
    private static final String API_URL = "https://plive.48.cn/livesystem/api/live/v1/memberLivePage";
    private static final String LIVING_LIST = "liveList";
    private static final String REVIEW_LIST = "reviewList";

    private String param;


    public KDLive() {
        this(0, 63, 1);
    }

    public KDLive(String name) {
        this(KDData.getInstance().getMemberId(name));
    }

    public KDLive(int memberId) {
        this(0, memberId, 20);
    }

    public KDLive(long lastTime, int memberId, int limit) {
        param = new JSONObject()
                .fluentPut("lastTime", lastTime)
                .fluentPut("groupId", 0)
                .fluentPut("type", 0)
                .fluentPut("memberId", memberId)
                .fluentPut("giftUpdTime", "1498211389003")
                .fluentPut("limit", limit)
                .toJSONString();
    }

    public KDLiveInfo[] getLivingData() {
        return getSourceContent(LIVING_LIST).toJavaList(KDLiveInfo.class)
                .toArray(new KDLiveInfo[0]);
    }

    public KDLiveInfo[] getLivingData(Predicate<KDLiveInfo> predicate) {
        return getSourceContent(LIVING_LIST).toJavaList(KDLiveInfo.class)
                .stream()
                .filter(predicate)
                .toArray(KDLiveInfo[]::new);
    }

    public KDLiveInfo[] getReviewData() {
        return getSourceContent(REVIEW_LIST).toJavaList(KDLiveInfo.class)
                .toArray(new KDLiveInfo[0]);
    }

    public KDLiveInfo[] getReviewData(Predicate<KDLiveInfo> predicate) {
        return getSourceContent(REVIEW_LIST).toJavaList(KDLiveInfo.class)
                .stream()
                .filter(predicate)
                .toArray(KDLiveInfo[]::new);
    }


    private JSONArray getSourceContent(String listName) {
        JSONObject jo = getSourceData();
        if (jo.getIntValue("status") == 200) {
            return jo.getJSONObject("content").getJSONArray(listName);
        } else {
            Tools.print("口袋直播数据获取炸了，等待之后重新获取:" + jo.getString("message"));
            Tools.sleep(1000 * 30);
            return getSourceContent(listName);
        }
    }

    private JSONObject getSourceData() {
        return JSON.parseObject(Tools.sendPost(API_URL, param,
                conn -> {
                    conn.setRequestProperty("version", "5.0.1");
                    conn.setRequestProperty("os", "android");
                    conn.setRequestProperty("Content-Type", "application/json");
                }, Throwable::printStackTrace));
    }
}
