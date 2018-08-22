package top.yzlin.KouDai48;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.lang.String.valueOf;


public class KDLive implements Runnable {
    private String QQGID;
    private CQRoot QQMT;
    private long frequency;
    private String memberID;
    private String memberName;
    private static SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss ");

    private String[] data;//因为心态爆炸之后写的垃圾变量，用来获取成员直播的信息

    public KDLive(String memberName, String QQGID, CQRoot QQMT) {
        this.memberName = memberName;
        this.QQGID = QQGID;
        this.QQMT = QQMT;

        memberID = LiveData.getLiveID(memberName);
        frequency = 60 * 1000;

//		QQMT.addMsgSolution(this::msgSolution);
        new Thread(this).start();
    }

    /**
     * 主要线程
     * 功能是检测成员是否直播
     * 成员直播之后，这个线程会暂停长达一定时间3小时不再监控
     */
    public void run() {
        while (true) {
            while (!isLiving()) {
                Tools.sleep(frequency);
            }
            QQMT.sendGroupMsg(QQGID, sendOpenLiveText(memberName, data[0], data[1], data[2], data[3]));
            Tools.print("小偶像开了直播，接下来进入3小时线程休眠");

            LiveData.setFrequency(1000 * 60 * 30);
            Tools.sleep(1000 * 60 * 60 * 3);
            LiveData.setFrequency(45000);
        }
    }

    /**
     * 设置监听频率，单位是毫秒
     *
     * @param frequency 监听频率，单位是毫秒
     */
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    /**
     * 获得直播数据源，该数据未解析，用来获取成员最新直播用
     *
     * @return 直播数据源
     */
    private String getLiveData() {
        return Tools.sendPost(
                "https://plive.48.cn/livesystem/api/live/v1/memberLivePage",
                "{\"lastTime\":\"0\",\"groupId\":\"0\",\"type\":\"0\",\"memberId\":\"" + memberID + "\",\"giftUpdTime\":\"1498211389003\",\"limit\":\"1\"}",
                conn -> {
                    conn.setRequestProperty("version", "5.0.1");
                    conn.setRequestProperty("os", "android");
                    conn.setRequestProperty("Content-Type", "application/json");
                }
        );
    }

    /**
     * 判断成员是否在直播
     *
     * @return 直播true, 不直播false
     */
    private boolean isLiving() {
        String data = LiveData.getData();

        //检测数据有效性，并且检测是否有人开启直播
        if (data == null) {
            return false;
        }
        JSONObject jData = JSONObject.parseObject(data);
        //检测有效性
        if (!"200".equals(jData.getString("status"))) {
            return false;
        }

        //切割出正在直播的
        jData = jData.getJSONObject("content");
        data = jData.getString("liveList");

        if (data.contains("\"memberId\":" + memberID)) {
            JSONArray jAData = jData.getJSONArray("liveList");
            for (Object temp : jAData) {
                jData = (JSONObject) temp;
                if (memberID.equals(jData.getString("memberId"))) {
                    this.data = new String[]{
                            jData.getString("subTitle"),
                            df.format(new Date(jData.getLong("startTime"))),
                            Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + jData.getString("liveId")),
                            ("1".equals(jData.getString("liveType")) ? "直播" : "电台")
                    };
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理从群里接收的信息的方法
     * 这个是回复查询小偶像最新直播关键字的方法
     *
     * @param Msg 该参数不用设置，这个方法一般是注册监听使用
     */
    private void msgSolution(HashMap<String, String> Msg) {
        if (valueOf(CQRoot.GET_GROUP_MSG).equals(Msg.get("act")) && QQGID.equals(Msg.get("fromGroup")) && "查询小偶像最新直播".equals(Msg.get("msg"))) {
            String temp = getLiveData();
            if (null == temp) {
                QQMT.sendGroupMsg(QQGID, "获取失败，机器人感觉好像炸了，请联系管理");
                return;
            }

            JSONObject data = JSONObject.parseObject(temp);
            if ("200".equals(data.getString("status"))) {

                data = data.getJSONObject("content")
                        .getJSONArray("reviewList")
                        .getJSONObject(0);

                QQMT.sendGroupMsg(QQGID, sendReplyMsg(
                        memberName,
                        data.getString("subTitle"),
                        df.format(new Date(data.getLong("startTime"))),
                        Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + data.getString("liveId")),
                        data.getString("streamPath"),
                        (1 == data.getIntValue("liveType") ? "直播" : "电台")
                ));
            } else {
                Tools.print("查询失败");
                QQMT.sendGroupMsg(QQGID, "查询失败");
            }
        }
    }


    /**
     * 重写这个方法来定义开启直播信息的样式
     *
     * @param memberName 成员名字
     * @param title      标题
     * @param openTime   开播时间
     * @param url        直播链接
     * @param liveType   直播类型
     * @return 返回值为提示消息
     */
    protected String sendOpenLiveText(String memberName, String title, String openTime, String url, String liveType) {
        String temp;
        temp = "小偶像开" + liveType + "了\n";
        temp += title + "\n";
        temp += "直播链接:" + url;
        Tools.print(QQGID + "群" + memberName + "开直播");
        return temp;
    }

    /**
     * 重写这个方法来定义最后一次直播信息的样式
     *
     * @param memberName 成员名字
     * @param title      标题
     * @param openTime   开播时间
     * @param url        直播链接（已缩短）
     * @param streamPath 视频下载地址（未缩短）
     * @param liveType   直播类型
     * @return 返回值为提示消息
     */
    protected String sendReplyMsg(String memberName, String title, String openTime, String url, String streamPath, String liveType) {
        String temp;
        temp = memberName + "最后一次直播时间为:" + openTime + "\n";
        temp += "直播地址为:" + url + "\n";
        temp += "直播下载地址为:" + streamPath;
        Tools.print(QQGID + "群有人查询最新直播");
        return temp;
    }
}
