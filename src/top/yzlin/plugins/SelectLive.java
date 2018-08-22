package top.yzlin.plugins;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.CQRoot.MsgPlugins;
import top.yzlin.KouDai48.LiveData;
import top.yzlin.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectLive implements MsgPlugins {
    private String memberName;
    private String msg;
    private static SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss ");

    /**
     * 初始化
     *
     * @param text
     * @return
     */
    private boolean init(String text) {
        String temp[] = MsgPlugins.getParam(text);
        if (temp.length == 0) {
            return false;
        } else {
            memberName = temp[0];
            msg = MsgPlugins.getMsg(text);
            return true;
        }
    }

    @Override
    public String solutionText(String text) {
        if (!init(text)) {
            return "查询插件无法初始化";
        }

        //获取直播数据,源json数据
        JSONObject data = JSONObject.parseObject(Tools.sendPost(
                "https://plive.48.cn/livesystem/api/live/v1/memberLivePage",
                "{\"lastTime\":\"0\",\"groupId\":\"0\",\"type\":\"0\",\"memberId\":\"" + LiveData.getLiveID(memberName) + "\",\"giftUpdTime\":\"1498211389003\",\"limit\":\"1\"}",
                conn -> {
                    conn.setRequestProperty("version", "5.0.1");
                    conn.setRequestProperty("os", "android");
                    conn.setRequestProperty("Content-Type", "application/json");
                }
        ));
        if ("200".equals(data.getString("status"))) {

            data = data.getJSONObject("content")
                    .getJSONArray("reviewList")
                    .getJSONObject(0);

            Tools.print("有人通过插件查询了" + memberName + "最新直播");
            return sendReplyMsg(
                    memberName,
                    data.getString("subTitle"),
                    df.format(new Date(data.getLong("startTime"))),
                    Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + data.getString("liveId")),
                    data.getString("streamPath"),
                    (1 == data.getIntValue("liveType") ? "直播" : "电台")
            );
        } else {
            System.out.println(data);
            Tools.print("查询失败");
            return "查询失败";
        }
    }

    /**
     * 这个方法来替换最后一次直播信息的样式
     *
     * @param memberName 成员名字
     * @param title      标题
     * @param openTime   开播时间
     * @param url        直播链接（已缩短）
     * @param streamPath 视频下载地址（未缩短）
     * @param liveType   直播类型
     * @return 返回值为提示消息
     */
    private String sendReplyMsg(String memberName, String title, String openTime, String url, String streamPath, String liveType) {
        msg = msg.replace("$name_", memberName);
        msg = msg.replace("$title_", title);
        msg = msg.replace("$time_", openTime);
        msg = msg.replace("$url_", url);
        msg = msg.replace("$file_", streamPath);
        msg = msg.replace("$type_", liveType);
        return msg;
    }
}
