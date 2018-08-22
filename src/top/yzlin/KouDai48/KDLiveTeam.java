package top.yzlin.KouDai48;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author 殷泽凌
 */
public class KDLiveTeam implements Runnable {

    private String QQGID;
    private CQRoot QQMT;

    private long frequency;

    private HashSet<String> memberIDSet = new HashSet<>();
    private LinkedList<String> livingList = new LinkedList<>();

    private static SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss ");

    public KDLiveTeam(String Team, String QQGID, CQRoot QQMT) {
        this(Team, QQGID, QQMT, false);
    }

    public KDLiveTeam(Collection<String> list, String QQGID, CQRoot QQMT) {
        this.QQGID = QQGID;
        this.QQMT = QQMT;
        for (String t : list) {
            memberIDSet.add(LiveData.getLiveID(t));
        }
        frequency = 45 * 1000;
        Tools.print("的数据加载完成");
        new Thread(this).start();
    }

    public KDLiveTeam(String Team, String QQGID, CQRoot QQMT, boolean report) {
        this.QQGID = QQGID;
        this.QQMT = QQMT;
        loadData(Team);
        frequency = 45 * 1000;
        if (report) {
//			QQMT.addMsgReply(new GroupMsgReply(){
//				@Override
//				public boolean filter(GroupMsgInfo from){
//					return QQGID.equals(from.getFromGroup()) && from.getMsg().matches("查询[\u4e00-\u9fa5]{2,4}最新直播");
//				}
//				@Override
//				public String replyMsg(GroupMsgInfo a){
//					return msgSolve(a);
//				}
//			});
        }
        Tools.print(Team + "的数据加载完成");
        new Thread(this).start();
    }

    /**
     * 主要线程
     */
    public void run() {
        JSONArray data;
        while (true) {
            data = getLivingData();
            if (data == null) {
                Tools.sleep(60000);
                continue;
            }
            String memberID;
            JSONObject jt;
            for (String a : livingList) {
                boolean temp = true;
                for (Object ot : data) {
                    jt = (JSONObject) ot;
                    memberID = jt.getString("memberId");
                    if (a.equals(memberID)) {
                        temp = false;
                        break;
                    }
                }
                if (temp) {
                    livingList.remove(a);
                }
            }

            for (Object temp : data) {
                jt = (JSONObject) temp;
                memberID = jt.getString("memberId");
                if (memberIDSet.contains(memberID) && (!livingList.contains(memberID))) {
                    QQMT.sendGroupMsg(QQGID, sendOpenLiveText(
                            jt.getString("title").replaceAll("的直播间|的电台", ""),
                            jt.getString("subTitle"),
                            df.format(new Date(jt.getLong("startTime"))),
                            Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + jt.getString("liveId")),
                            (1 == jt.getIntValue("liveType") ? "直播" : "电台")
                    ));
                    livingList.add(memberID);
                }
            }
            Tools.sleep(frequency);
        }
    }

    /**
     * 处理从群里接收的信息的方法
     *
     * @param msg 该参数不用设置，这个方法一般是注册监听使用
     */
    private String msgSolve(GroupMsgInfo msg) {
        String memberName = msg.getMsg().replaceAll("查询|最新直播", "");
        String memberID = LiveData.getLiveID(memberName);
        if (!memberIDSet.contains(memberID)) {
            return "该成员未录入或者非法输入";
        } else {
            JSONObject data = JSONObject.parseObject(Tools.sendPost(
                    "https://plive.48.cn/livesystem/api/live/v1/memberLivePage",
                    "{\"lastTime\":\"0\",\"groupId\":\"0\",\"type\":\"0\",\"memberId\":\"" + memberID + "\",\"giftUpdTime\":\"1498211389003\",\"limit\":\"1\"}",
                    conn -> {
                        conn.setRequestProperty("version", "5.0.1");
                        conn.setRequestProperty("os", "android");

                        conn.setRequestProperty("Content-Type", "application/json");
                    }
            ));
            if (!"200".equals(data.getString("status"))) {
                Tools.print("直播获取失败，非200,KDLiveTeam错误");
                return "获取失败，机器人感觉好像炸了，请联系管理";
            }
            data = data.getJSONObject("content").getJSONArray("reviewList").getJSONObject(0);
            return sendReplyMsg(
                    memberName,
                    data.getString("subTitle"),
                    df.format(new Date(data.getLong("startTime"))),
                    Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + data.getString("liveId")),
                    data.getString("streamPath"),
                    (1 == data.getIntValue("liveType") ? "直播" : "电台"));
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
     * 返回当前直播成员的数据
     *
     * @return 会返回一个JSONArray数组
     */
    private JSONArray getLivingData() {
        JSONObject data = JSONObject.parseObject(LiveData.getData());
        if (!"200".equals(data.getString("status"))) {
            return null;
        }
        return data.getJSONObject("content").getJSONArray("liveList");
    }


    /**
     * 重写这个方法来定义开启直播信息的样式
     *
     * @param memberName 成员名字
     * @param title      标题
     * @param openTime   开播时间
     * @param url        直播链接（已缩短）
     * @param liveType   直播类型
     * @return 返回值为提示消息
     */
    protected String sendOpenLiveText(String memberName, String title, String openTime, String url, String liveType) {
        String temp;
        temp = memberName + "开" + liveType + "了\n";
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
        temp = memberName + "最后一次" + liveType + "时间为:" + openTime + "\n";
        temp += "直播地址为:" + url + "\n";
        temp += "直播下载地址为:" + streamPath;
        Tools.print(QQGID + "群有人查询" + memberName + "最新直播");
        return temp;
    }

    /**
     * 用过成员ID来获取成员名字，效率贼他妈不行，一般用于特殊情况
     * 懒得完成,你打我啊
     * 无论怎么搞都会返回null
     *
     * @param memberName 成员房间ID
     * @return 成员名字
     */
    private String getMemberName(String memberName) {
        return null;
    }


    /**
     * 加载某个队伍的直播数据
     *
     * @param Team 队伍名称<br>
     *             支持大写，小写，不支持大小写混写<br>
     *             支持某些队伍别称<br>
     *             恩黑开发者,没有恩队数据<br>
     *             <font size="12" color="red">有本事微博挂我<font/>
     * @author 殷泽凌
     */
    private void loadData(String Team) {
        Team = Team.replaceAll("Team|team|TEAM|\\s+", "").toUpperCase();
        String temp[];
        switch (Team) {
            case "SII":
            case "S":
            case "艾斯队":
            case "社会队":
            case "艾斯兔":
                temp = new String[]{
                        "孙芮", "陈思", "戴萌", "蒋芸", "莫寒", "陈观慧", "沈之琳", "袁雨桢", "张语格", "孔肖吟", "李宇琪",
                        "钱蓓婷", "邱欣怡", "温晶婕", "吴哲晗", "徐晨辰", "徐子轩", "许佳琪", "袁丹妮", "成珏", "吕一",
                        "潘燕琦", "赵晔", "冯晓菲", "邵雪聪", "刘增艳", "徐伊人",

                        "赵韩倩"
                };
                break;
            case "HII":
            case "hii":
            case "h":
                temp = new String[]{
                        "郭倩芸", "郝婉晴", "姜涵", "李清扬", "林楠", "刘炅然", "刘佩鑫", "沈梦瑶", "孙珍妮", "王柏硕",
                        "王露皎", "王奕", "文文", "吴燕文", "谢妮", "熊沁娴", "徐伊人", "杨惠婷", "袁航", "袁一琦", "张昕",
                        "徐晗", "许杨玉琢"
                };
                break;
            case "X":
            case "x":
            case "P":
            case "p":
            case "皮":
                temp = new String[]{
                        "陈琳", "冯晓菲", "李晶", "李钊", "林忆宁", "祁静", "邵雪聪", "宋昕冉", "孙歆文", "孙亚萍", "汪佳翎",
                        "汪束", "王晓佳", "谢天依", "杨冰怡", "杨韫玉", "姚祎纯", "张丹三", "张嘉予", "吕一", "李佳恩",
                        "陈韫凌", "潘瑛琪", "徐诗琪"
                };
                break;
            case "test":
                temp = new String[]{
                        "周佳怡", "秦玺", "吴羽霏"
                };
                break;
            default:
                Tools.print("无效队伍");
                temp = new String[0];
        }
        for (String t : temp) {
            memberIDSet.add(LiveData.getLiveID(t));
        }
    }
}

