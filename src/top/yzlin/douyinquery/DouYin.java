package top.yzlin.douyinquery;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.tools.Tools;

import java.util.function.Predicate;

/**
 * 该软件的使用方法
 * 由这个类来进行获取某个时间点之后的所有成员的最新抖音
 * 然后
 */
public class DouYin {
    private static final ConfigLoading configLoading = ConfigLoading.getInstance();
    private static GetSignFunction getSignFunction = new OldSignFunction();
    private String userID;//成员的ID
    private String memberName;//成员的姓名
    private String memberDytk;

    /**
     * 成员名字，按照名字来进行查找成员ID
     *
     * @param memberName 成员名字
     */
    public DouYin(String memberName) {
        this.memberName = memberName;
        userID = configLoading.getMemberUserID(memberName);
        memberDytk = configLoading.getMemberDytk(memberName);
    }

    /**
     * 某个时间之后的成员抖音发布情况
     *
     * @return 抖音信息实例
     */
    public DouYinInfo[] getData() {
        return getData(j -> true);
    }

    /**
     * 按照id进行过滤，大于这个ID的才能返回
     *
     * @param proID ID
     * @return 抖音
     */
    public DouYinInfo[] getData(long proID) {
        return getData(j -> j.getLong("aweme_id") > proID);
    }

    /**
     * 自己过滤，对源json进行过滤
     *
     * @param predicate 过滤规则
     * @return 抖音
     */
    public DouYinInfo[] getData(Predicate<JSONObject> predicate) {
        String str = Tools.sendGet("https://www.amemv.com/aweme/v1/aweme/post/",
                "user_id=" + userID + "&count=21&max_cursor=0&aid=1128&_signature=" + getSign(userID) + "&dytk=" + memberDytk,
                conn -> {
                    conn.setRequestProperty("accept", "application/json");
                    conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
                    conn.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                });
        return JSONObject.parseObject(str).getJSONArray("aweme_list").toJavaList(JSONObject.class)
                .stream()
                .filter(predicate)
                .map(j -> {
                    DouYinInfo douYinInfo = new DouYinInfo();
                    douYinInfo.setDouyinID(j.getLong("aweme_id"));
                    douYinInfo.setMemberName(memberName);
                    douYinInfo.setTitle(j.getJSONObject("share_info").getString("share_desc"));
                    j = j.getJSONObject("video");
                    douYinInfo.setCoverUrl(j.getJSONObject("cover").getJSONArray("url_list").getString(0));
                    douYinInfo.setVideoUrl(j.getJSONObject("play_addr").getJSONArray("url_list").getString(0));
                    return douYinInfo;
                }).toArray(DouYinInfo[]::new);
    }

    /**
     * 封装一下算法
     *
     * @param userID 成员的userID
     * @return 签名
     */
    private static String getSign(String userID) {
        return getSignFunction.getSign(userID);
    }
}
