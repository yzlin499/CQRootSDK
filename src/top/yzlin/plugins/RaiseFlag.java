package top.yzlin.plugins;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.CQRoot.MsgPlugins;
import top.yzlin.tools.Tools;

import java.util.HashMap;

public class RaiseFlag implements MsgPlugins {
    private HashMap<String, Flag> flagMap = new HashMap<>();

    abstract private class Flag {
        protected String projectID;
        protected String param;
        private String goal;
        protected int beginRaise;

        abstract int getAlready();

        public void setGoal(String goal) {
            this.goal = goal;
        }

        String getFlag(String text) {
            int already = getAlready();
            text = text.replace("$already_", String.valueOf(already));
            text = text.replace("$goal_", goal);
            boolean temp = already > Integer.parseInt(goal);
            text = text.replace("$status_", temp ? "已完成" : "未完成");
            return text;
        }
    }


    private class PeopleFlag extends Flag {
        private static final String url = "https://zhongchou.modian.com/realtime/get_simple_product";

        private PeopleFlag(String projectID, int beginRaise) {
            this.projectID = projectID;
            this.beginRaise = beginRaise;
            param = "jsonpcallback=jQuery1111012148591129811948_1519884444602&ids=" + projectID + "&if_all=1&_=1519884444603";
        }

        @Override
        protected int getAlready() {
            JSONObject data = JSONObject.parseObject(Tools.sendPost(url, param));
            return data.getIntValue("backer_count");
        }
    }

    private class MoneyFlag extends Flag {
        private static final String url = "https://wds.modian.com/api/project/detail";

        private MoneyFlag(String projectID, int beginRaise) throws Exception {
            this.projectID = projectID;
            this.beginRaise = beginRaise;
            param = "pro_id=" + projectID + "&sign=" + Tools.MD5("pro_id=" + projectID + "&p=das41aq6").substring(5, 21);
        }

        @Override
        protected int getAlready() {
            JSONObject data = JSONObject.parseObject(Tools.sendPost(url, param));
            int alreadyRaise = data.getJSONArray("data").getJSONObject(0).getIntValue("already_raised");
            return alreadyRaise - beginRaise;
        }
    }


    /**
     * @param param 0.标识flag
     *              1.项目ID
     *              2.flag类型
     *              3.目标
     *              4.开始数目
     * @return 是否初始化成功
     */
    private boolean init(String[] param) {
        if (param.length < 5) {
            return false;
        }
        try {
            Flag temp;
            if ("moneyFlag".equals(param[2])) {
                temp = new MoneyFlag(param[1], Integer.parseInt(param[4]));
            } else if ("peopleFlag".equals(param[2])) {
                temp = new PeopleFlag(param[1], Integer.parseInt(param[4]));
            } else {
                return false;
            }
            temp.setGoal(param[3]);
            flagMap.put(param[0], temp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String solutionText(String text) {
        String param[] = MsgPlugins.getParam(text);
        if (!flagMap.containsKey(param[0])) {
            if (!init(param)) {
                return "插件初始化失败";
            }
        }
        return flagMap.get(param[0]).getFlag(MsgPlugins.getMsg(text));
    }
}
