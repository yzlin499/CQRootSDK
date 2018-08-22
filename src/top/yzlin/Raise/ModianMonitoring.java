package top.yzlin.Raise;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.stream.Stream;

public class ModianMonitoring extends AbstractMonitoring {
    //优化代码的变量
    private String infoParam;//储存项目详情的参数
    private String ordersParam;//储存集资列表的参数
    private boolean threadIsRun = true;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private class RaiseData {
        private String nickName = "";
        private String raiseMoney = "";
        private long raiseTime;

        private RaiseData(String nickName, String raiseMoney, long raiseTime) {
            this.nickName = nickName;
            this.raiseMoney = raiseMoney;
            this.raiseTime = raiseTime;
        }

        private long getRaiseTime() {
            return raiseTime;
        }

        @Override
        public boolean equals(Object o) {
            if (RaiseData.class.equals(o.getClass())) {
                RaiseData raiseData = (RaiseData) o;
                return nickName.equals(raiseData.nickName) && raiseMoney.equals(raiseData.raiseMoney) && raiseTime == raiseData.raiseTime;
            } else {
                return false;
            }
        }
    }//储存信息类

    /**
     * 这是一个摩点的集资的对象
     *
     * @param pro_id 集资项目的ID
     * @param QQGID  Q群的号码
     * @param QQMT   socket的链接
     */
    public ModianMonitoring(String pro_id, String QQGID, CQRoot QQMT) {
        super(QQGID, QQMT);
        infoParam = "pro_id=" + pro_id + "&sign=" + Tools.MD5("pro_id=" + pro_id + "&p=das41aq6").substring(5, 21);
        ordersParam = "page=1&pro_id=" + pro_id + "&sign=" + Tools.MD5("page=1&pro_id=" + pro_id + "&p=das41aq6").substring(5, 21);
        try {
            JSONObject data = JSONObject.parseObject(Tools.sendPost("https://wds.modian.com/api/project/detail", infoParam));
            if (!"0".equals(data.getString("status"))) {
                Tools.print(data.getString("message"));
                throw new IOException();
            }
            data = data.getJSONArray("data").getJSONObject(0);
            setTitle(data.getString("pro_name"));
            setGoalMoney(data.getString("goal"));
            setEndTime(data.getString("end_time"));
            setMoneyUrl("https://zhongchou.modian.com/item/" + pro_id + ".html");
        } catch (IOException e) {
            e.printStackTrace();
            Tools.print("集资初始化失败，炸了，重启吧");
            return;
        }
        new Thread(this).start();
    }

    public static void main(String args[]) {
        String pro_id = "28243";
        String ordersParam = "page=1&pro_id=" + pro_id + "&sign=" + Tools.MD5("page=1&pro_id=" + pro_id + "&p=das41aq6").substring(5, 21);
        String data = Tools.sendPost("https://wds.modian.com/api/project/orders", ordersParam);
        System.out.println(JSONObject.parseObject(data));
    }

    //主要线程
    @Override
    public void run() {
        Tools.print("线程启动");
        Tools.sleep(1500);
        String nowMoney;
        RaiseData[] data;
        RaiseData lastRaise = getOrdersData(new RaiseData(null, null, 0))[0];
        while (threadIsRun) {
            nowMoney = getNowMoney();
            data = getOrdersData(lastRaise);
            for (RaiseData temp : data) {
                sendMsg(temp.nickName, temp.raiseMoney, nowMoney);
            }
            lastRaise = data.length > 0 ? data[0] : lastRaise;
            Tools.sleep(getFrequency());
        }
        Tools.print("重启线程");
        threadIsRun = true;
        run();
    }

    /**
     * 获得当前集资进度
     *
     * @return 当前集资
     */
    private String getNowMoney() {
        try {
            JSONObject data = JSONObject.parseObject(Tools.sendPost("https://wds.modian.com/api/project/detail", infoParam));
            if (!"0".equals(data.getString("status"))) {
                Tools.print("项目最新进度获取失败");
                Tools.print(data.getString("message"));
                Tools.sleep(10000);
                return getNowMoney();
            }
            return data.getJSONArray("data").getJSONObject(0).getString("already_raised");
        } catch (JSONException e) {
            Tools.print("项目最新进度获取失败");
            Tools.print(e.getMessage());
            Tools.sleep(10000);
            return getNowMoney();
        }
    }

    /**
     * 获取新的集资信息
     *
     * @return JSONArray对象
     */
    private RaiseData[] getOrdersData(RaiseData raiseData) {
        try {
            String data = Tools.sendPost("https://wds.modian.com/api/project/orders", ordersParam);
            JSONObject t = JSONObject.parseObject(data);
            if (!"0".equals(t.getString("status"))) {
                Tools.print(t.getString("message"));
                throw new IOException("非200");
            }
            JSONArray tja = t.getJSONArray("data");
            if (tja.size() == 0) {
                Tools.print("数据获取为空，等待100秒之后递归");
                Tools.sleep(100000);
                return getOrdersData(raiseData);
            }
            JSONObject[] jsonObject = new JSONObject[tja.size()];
            tja.toArray(jsonObject);
            return Stream.of(jsonObject)
                    .map(h -> {
                        String nickName = h.getString("nickname");
                        String backerMoney = h.getString("backer_money");
                        String payTime = h.getString("pay_time");
                        try {
                            return new RaiseData(nickName, backerMoney, df.parse(payTime).getTime());
                        } catch (ParseException e) {
                            return null;
                        }
                    }).filter(h -> {
                        if (h == null) {
                            return false;
                        } else {
                            return h.raiseTime > raiseData.raiseTime;
                        }
                    }).sorted(Comparator.comparing(RaiseData::getRaiseTime).reversed())
                    .toArray(RaiseData[]::new);
        } catch (IOException e) {
            Tools.print("读取集资列表时炸了老哥,30秒之后重新加载数据");
            Tools.sleep(30000);
            return getOrdersData(raiseData);
        }
    }

    public void autoRestart(long time) {
        new Thread(() -> {
            while (true) {
                Tools.sleep(time);
                threadIsRun = false;
            }
        }).start();
    }
}