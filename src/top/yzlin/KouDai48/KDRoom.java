package top.yzlin.KouDai48;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import top.yzlin.netInterface.SetConnection;
import top.yzlin.tools.Tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static top.yzlin.tools.Tools.sendPost;

public class KDRoom {
    /*创建文档*/
    static {
        File tf = new File("doc\\KDRoomConfiguration");
        if (!tf.exists()) {
            tf.mkdirs();
        }
    }

    private String roomID;
    private String account;
    private String userID;
    private Set<KDRoomType> msgTypeSet = EnumSet.allOf(KDRoomType.class);
    private String token;
    private SetConnection conn = connection -> {
        connection.setRequestProperty("token", token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("version", "5.0.1");
        connection.setRequestProperty("build", "52127");
        connection.setRequestProperty("os", "android");
    };

    /**
     * 实例化一个对象
     *
     * @param account    口袋48账号
     * @param memberName 成员名字
     */
    public KDRoom(String account, String memberName) {
        this.account = account;
        if ((roomID = getRoomID(memberName)) == null) {
            Tools.print("房间获取失败，程序直接结束");
            return;
        }

        File cfg = new File("doc\\KDRoomConfiguration\\" + account + ".cfg");
        if (!cfg.exists()) {
            System.out.println("没有创建配置文件，接下来来创建文件");
            System.out.println("请输入密码，按回车确定：");
            while (!createDocument(account, new Scanner(System.in).next())) {
                Tools.print("创建失败,重新输入");
            }
        } else {
            try {
                Properties p = new Properties();
                p.load(new FileReader(cfg));
                Tools.print("token加载完成");
                token = p.getProperty("token");
                userID = p.getProperty("userID");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setFunctionOn(KDRoomType kdRoomType) {
        msgTypeSet.add(kdRoomType);
    }

    public void setFunctionDown(KDRoomType kdRoomType) {
        msgTypeSet.remove(kdRoomType);
    }

    /**
     * 创建文档,一般是被构造方法调用
     *
     * @param account  账号
     * @param password 密码
     * @return 是否创建成功
     */
    private boolean createDocument(String account, String password) {
        if ((token = getToken(account, password)) == null) {
            Tools.print("创建失败");
            return false;
        } else {
            Tools.print("创建成功");
            return true;
        }
    }

    /**
     * 返回最新的消息,如果获取失败返回null
     *
     * @param time 上一次收集的时间
     * @return RoomInfo数组
     */
    public KDRoomInfo[] getRoomMsg(long time) {
        JSONObject result = JSONObject.parseObject(Tools.sendPost(
                "https://pjuju.48.cn/imsystem/api/im/v1/member/room/message/mainpage",
                "{\"roomId\":\"" + roomID + "\",\"chatType\":\"0\",\"lastTime\":\"0\",\"limit\":\"10\"}",
                conn));
        if (result.getIntValue("status") == 401) {
            Tools.print(result.getString("message"));
            token = getToken();
            return getRoomMsg(time);
        } else if (result.getIntValue("status") != 200) {
            Tools.print(result.getString("message"));
            return new KDRoomInfo[0];
        }
        return result.getJSONObject("content").getJSONArray("data").toJavaList(JSONObject.class)
                .stream()
                .filter(data -> data.getLong("msgTime") > time)
                .map(data -> {
                    try {
                        JSONObject extInfo = JSONObject.parseObject(data.getString("extInfo"));
                        KDRoomInfo temp = new KDRoomInfo();
                        KDRoomType type = KDRoomType.parse(extInfo.getString("messageObject"));
                        temp.setMsgType(type);
                        if (type == null) {
                            Tools.print("未知数据:" + extInfo);
                            return null;
                        } else if (!msgTypeSet.contains(type)) {
                            return null;
                        }
                        temp.setMsgTime(data.getLong("msgTime"));
                        temp.setSender(extInfo.getString("senderName"));
                        switch (type) {
                            case TEXT:
                                temp.setMsg(extInfo.getString("text"));
                                break;
                            case FAIPAI_TEXT:
                                temp.setText(extInfo.getString("faipaiContent"));
                                temp.setMsg(extInfo.getString("messageText"));
                                break;
                            case LIVE:
                            case DIANTAI:
                                temp.setMsg("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" +
                                        extInfo.getString("referenceObjectId"));
                                temp.setText(extInfo.getString("referenceContent"));
                                break;
                            case AUDIO:
                            case IMAGE:
                                try {
                                    temp.setMsg(JSONObject.parseObject(
                                            data.getString("bodys"))
                                            .getString("url"));
                                } catch (JSONException e) {
                                    temp.setMsg(JSONObject.parseObject(
                                            data.getString("bodys").substring(1))
                                            .getString("url"));
                                }
                                break;
                            case IDOLFLIP:
                                temp.setMsg(extInfo.getString("idolFlipContent"));
                                temp.setText(extInfo.getString("idolFlipTitle"));
                                break;
                            default:
                                temp.setMsg("未知信息类型并且此处代码不可达");
                        }
                        return temp;
                    } catch (JSONException e) {
                        Tools.print(data);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .toArray(KDRoomInfo[]::new);
    }

    /**
     * 获取成员的房间ID
     *
     * @param memberName 成员姓名
     * @return 返回成员ID
     */
    private String getRoomID(String memberName) {
        JSONObject result = JSONObject.parseObject(Tools.sendPost(
                "https://pjuju.48.cn/imsystem/api/im/v1/search",
                "{\"roomName\":\"" + memberName + "\"}",
                conn
        ));
        if (!"200".equals(result.getString("status"))) {
            Tools.print("获取房间ID失败，炸了，散了吧");
            return null;
        }
        JSONArray aData = result.getJSONObject("content").getJSONArray("data");
        if (aData.size() <= 0) {
            Tools.print("搜不到这个人啊");
            return null;
        }
        for (int i = aData.size() - 1; i >= 0; i--) {
            result = aData.getJSONObject(i);
            if (0 == result.getIntValue("roomType")) {
                Tools.print("成员房间名：" + result.getString("roomName") + ":[" + result.getString("roomId") + ']');
                return result.getString("roomId");
            }
        }
        Tools.print("查到消息但是查不到成员");
        return null;
    }

    /**
     * 刷新token,并且将token写进配置
     *
     * @return 返回最新的token, 获取失败则返回null
     */
    private String getToken() {
        Properties p = new Properties();
        try {
            p.load(new FileReader("doc\\KDRoomConfiguration\\" + account + ".cfg"));
            return getToken(p.getProperty("account"), p.getProperty("password"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 刷新token,并且将token写进配置
     *
     * @return 返回最新的token, 获取失败则返回null
     */
    private String getToken(String account, String password) {
        Tools.print("获取新token");
        JSONObject result = JSONObject.parseObject(Tools.sendPost(
                "https://puser.48.cn/usersystem/api/user/v1/login/phone",
                "{\"latitude\":0,\"longitude\":0,\"password\":\"" + password + "\",\"account\":\"" + account + "\"}",
                conn -> {
                    conn.setRequestProperty("IMEI", "861962030" + (new Random().nextInt(899999) + 100000));
                    conn.setRequestProperty("Content-Type", "application/json");
                }
        ));
        if (result == null) {
            return null;
        } else {
            if (result.getIntValue("status") != 200) {
                Tools.print("token获取失败：" + result.getString("message"));
                return null;
            } else {
                Properties p = new Properties();
                p.setProperty("account", account);
                p.setProperty("password", password);
                String tok = result.getJSONObject("content").getString("token");
                p.setProperty("token", tok);
                try {
                    p.store(new FileWriter("doc\\KDRoomConfiguration\\" + account + ".cfg"), "账号配置文件");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return tok;
            }
        }
    }

    public void autoSignIn() {
        new Thread(() -> {
            Tools.sleep(Tools.todayRemainTime() + (1000 * 60 * 5));
            while (true) {
                Tools.print(sendPost("https://puser.48.cn/usersystem/api/user/v1/check/in", "{}", conn));
                Tools.sleep(500);
                int[] id = JSONObject.parseObject(Tools.sendPost("https://pdynamic.48.cn/dynamicsystem/api/dynamic/v1/list/friends",
                        "{\"lastTime\":0,\"userId\":" + userID + ",\"memberId\":[1,2,3,5,8,9,10,11,12,14,17,18,19,20,21,22,25,27,28,32,33,35,36,37,38,39,40,43,45,46,47,63,67,1544,2470,2508,5562,5564,5566,5567,5574,5973,6429,6431,6432,6734,6735,6737,6738,6739,6740,6741,6742,6743,6744,6745,6746,6747,9073,49005,49006,49007,63549,63554,63555,63558,63559,63561,63566,63567,63572,63576,63577,63579,63580,286973,286977,286978,286979,286980,286982,327558,327560,327561,327562,327566,327571,327573,327580,327584,327587,327591,327592,327597,327682,327683,399631,399652,399654,399669,399672,407101,407103,407104,407106,407108,407110,407112,407114,407118,407119,407121,407124,407126,407127,407130,407168,410175,410177,411040,417311,417315,417316,417317,417318,417320,417321,417324,417325,417326,417328,417329,417330,417331,417332,417333,417335,417336,419966,444081,459991,459994,459996,459997,460000,460004,460005,460933,480668,480670,480678,480679,490333,526172,528336,530440,530443,530450,530584,593820,594002,594003,594005],\"limit\":10}",
                        conn)).getJSONObject("content").getJSONObject("praise")
                        .keySet().stream().mapToInt(Integer::parseInt).sorted().toArray();
                int c = 0;
                for (int i = id.length - 1; i >= 0 && c < 5; i--) {
                    Tools.sleep(500);
                    JSONObject jo = JSON.parseObject(Tools.sendPost("https://pdynamic.48.cn/dynamicsystem/api/dynamic/v1/praise",
                            "{\"resId\":\"" + id[i] + "\"}", conn));
                    c += jo.getJSONObject("content").getIntValue("exp");
                    Tools.print(id[i] + jo.toString());
                }

                c = 0;
                Tools.sleep(1000);
                String[] liveID = JSONObject.parseObject(Tools.sendPost(
                        "https://plive.48.cn/livesystem/api/live/v1/memberLivePage",
                        "{\"lastTime\":\"0\",\"groupId\":\"0\",\"type\":\"0\",\"memberId\":\"0\",\"giftUpdTime\":\"1498211389003\",\"limit\":\"10\"}",
                        conn)).getJSONObject("content").getJSONArray("reviewList").toJavaList(JSONObject.class)
                        .stream().map(s -> s.getString("liveId")).toArray(String[]::new);

                for (int i = 0; i < liveID.length && c < 2; i++) {
                    Tools.sleep(500);
                    JSONObject jo = JSONObject.parseObject(Tools.sendPost("https://plive.48.cn/livesystem/api/live/v1/memberLiveShare",
                            "{\"userId\":" + userID + ",\"resId\":\"" + liveID[i] + "\"}",
                            conn));
                    c += jo.getJSONObject("content").getIntValue("exp");
                    Tools.print(liveID[i] + jo.toString());
                }
                Tools.sleep((1000 * 60 * 60 * 24) - 6000);
            }
        }).start();
    }
}
