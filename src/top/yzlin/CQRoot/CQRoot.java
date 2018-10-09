package top.yzlin.CQRoot;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import top.yzlin.CQRoot.cqinfo.AbstractInfo;
import top.yzlin.CQRoot.msginterface.EventSolution;
import top.yzlin.CQRoot.msginterface.reply.*;
import top.yzlin.tools.Tools;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MsgTools是通过酷Q机器人的LEMOC插件，与Q群机器人进行通信的类
 * <br><br>
 * 有关接收信息方法的重写与开发,请访问:<a href="https://cqp.cc/t/29722">https://cqp.cc/t/29722</a><br><br>
 * 有关CQ码,请访问:<a href="https://d.cqp.me/Pro/CQ%E7%A0%81">https://d.cqp.me/Pro/CQ%E7%A0%81</a>
 *
 * @author yzlin
 */
public class CQRoot {

    // <editor-fold desc="各种状态码">
    // <editor-fold desc="接收到的状态码">
    /**
     * 接收群信息
     */
    public final static int GET_GROUP_MSG = 2;
    /**
     * 接收讨论组信息
     */
    public final static int GET_DISCUSS_MSG = 4;
    /**
     * 接收私聊(个人)信息
     */
    public final static int GET_PERSON_MSG = 21;
    /**
     * 管理员变动
     */
    public final static int GET_GROUP_ADMIN_CHANGE = 101;
    /**群成员增加*/
    public final static int GET_GROUP_MEMBER_INCREASE = 103;
    /**群成员减少*/
    public final static int GET_GROUP_MEMBER_DECREASE = 102;
    /**好友增加*/
    public final static int GET_FRIEND_INCREASE = 201;
    /**好友增加请求*/
    public final static int GET_FRIEND_REQUEST = 301;
    /**群增加请求*/
    public final static int GET_GROUP_REQUEST = 302;
    // </editor-fold>
    /**删除群成员*/
    public final static int GROUP_DELETE_MEMBER = 120;
    /**
     * 发送群消息
     */
    public final static int SEND_GROUP_MSG = 101;
    /**
     * 发送讨论组消息
     */
    public final static int SEND_DISCUSS_MSG = 103;
    /**
     * 发送私聊(个人)消息
     */
    public final static int SEND_PERSON_MSG = 106;
    /**
     * 发送赞
     */
    public final static int SEND_PRAISE = 110;
    /**
     * 全群禁言
     */
    public final static int GROUP_BANNED = 123;
    /**
     * 匿名成员禁言
     */
    public final static int ANONYMOUS_MEMBER_BANNED = 124;
    /**
     * 群匿名设置
     */
    public final static int SET_GROUP_ANONYMOUS = 125;
    /**
     * 群成员名片设置
     */
    public final static int SET_MEMBER_CARD = 126;
    /**
     * 群成员专属头衔
     */
    public final static int SET_MEMBER_SPECIAL_TITLE = 128;

    /**
     * json串方式返回群成员信息
     */
    public final static int GET_MEMBER_INFOMATION = 20303;
    /**
     * json串方式返回陌生人信息
     */
    public final static int GET_STRANGER_INFOMATION = 25304;

    // </editor-fold>

    private WebSocketClient client;
    private TypeFactory typeFactory = TypeFactory.getInstance();
    private Map<Integer,List<EventSolution>> eventListMap=new HashMap<>();

    /**
     * 实例化一个链接的客户端，本地
     * @param port webSocket的端口
     */
    public CQRoot(int port) {
        this(Integer.toString(port));
    }

    /**
     * 实例化一个链接的客户端，本地
     *
     * @param port webSocket的端口
     */
    public CQRoot(String port) {
        this("ws://localhost", port);
    }

    /**
     * 实例化一个链接的客户端
     * @param wsPath 按照某个地址来创建
     * @param port   webSocket的端口
     */
    public CQRoot(String wsPath, String port) {
        //配置服务器
        try {
            client = new WebSocketClient(new URI(wsPath + ':' + port), new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake arg0) {
                    Tools.print("成功链接webSocket,端口:" + port);
                }

                @Override
                public void onMessage(String arg) {
                    CQRoot.this.onMessage(JSONObject.parseObject(arg));
                }

                @Override
                public void onError(Exception arg0) {
                    arg0.printStackTrace();
                    Tools.log(arg0.getMessage());
                    Tools.print("CQRoot出错了，炸了炸了");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    Tools.print("关闭连接，断开");
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    try {
                        System.out.println(new String(bytes.array(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            };
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送个人信息
     *
     * @param QQID QQ号
     * @param msg  发送信息
     */
    public void sendPersonMsg(String QQID, String msg) {
        client.send(new JSONObject()
                .fluentPut("act", SEND_PERSON_MSG)
                .fluentPut("QQID", QQID)
                .fluentPut("msg", msg)
                .toString());
    }

    /**
     * 发送Q群信息
     *
     * @param GID Q群号码
     * @param msg 发送信息
     */
    public void sendGroupMsg(String GID, String msg) {
        client.send(new JSONObject()
                .fluentPut("act", SEND_GROUP_MSG)
                .fluentPut("groupid", GID)
                .fluentPut("msg", msg)
                .toString());
    }

    /**
     * 发送讨论组信息
     *
     * @param DID 讨论组号码
     * @param msg 发送信息
     */
    public void sendDiscussMsg(String DID, String msg) {
        client.send(new JSONObject()
                .fluentPut("act", SEND_DISCUSS_MSG)
                .fluentPut("discussid", DID)
                .fluentPut("msg", msg)
                .toString());
    }

    /**
     * 接收信息的处理方法
     *
     * @param msg
     */
    private void onMessage(JSONObject msg) {
        int act = msg.getIntValue("act");
        AbstractInfo info = msg.toJavaObject(typeFactory.getInfoClass(act));
        getEventList(act).forEach(item -> item.msgSolution(info));
    }

    private List<EventSolution> getEventList(Integer integer) {
        if (eventListMap.containsKey(integer)){
            return eventListMap.get(integer);
        }else{
            List<EventSolution> eventSolutionList = new ArrayList<>();
            eventListMap.put(integer, eventSolutionList);
            return eventSolutionList;
        }
    }

    /**
     * 添加群事件处理
     *
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgSolution(EventSolution msm) {
        if (msm instanceof ReplySolution) {
            int type = 0;
            if (msm instanceof GroupMsgReply) {
                type = GET_GROUP_MSG;
            } else if (msm instanceof PersonMsgReply) {
                type = GET_PERSON_MSG;
            } else if (msm instanceof DiscussMsgReply) {
                type = GET_DISCUSS_MSG;
            }
            return getEventList(type).add(
                    new ProxyReplySolution(this, (ReplySolution) msm, type));
        } else {
            for (Class clazz : msm.getClass().getInterfaces()){
                if (EventSolution.class.isAssignableFrom(clazz)) {
                    return getEventList(typeFactory.getEventClass(clazz)).add(msm);
                }
            }
        }
        return false;
    }

    /**
     * 删除事件
     *
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgSolution(EventSolution msm) {
        for (Class clazz : msm.getClass().getInterfaces()) {
            if (EventSolution.class.isAssignableFrom(clazz)) {
                return getEventList(typeFactory.getEventClass(clazz)).remove(msm);
            }
        }
        return false;
    }

    /**
     * 重写这个方法，在close方法调用之前运行
     */
    protected void destruct() {
    }

    /**
     * 关闭链接与线程
     */
    public void close() {
        destruct();
        client.close();
    }

    /**
     * 获取图片的CQ码<br>
     * 图片存放在酷Q目录的data\image\下<br><br>
     * 举例：<b>getImgCQCode("1.jpg");</b>（发送data\image\1.jpg）<br><br>
     * 发送图片信息的时候的方法为:<b>sendxxxxxMsg(getImgCQCode("xxx"));</b>
     * 会员才能发送图片
     *
     * @param imgFile 图片的名称
     * @return 图片的CQ码
     */
    public String getImgCQCode(String imgFile) {
        return "[CQ:image,file=" + imgFile + "]";
    }

    /**
     * 获取音频的CQ码<br>
     * 图片存放在酷Q目录的data\record\下<br><br>
     * 举例：<b>getImgCQCode("1");</b>（发送data\image\1.jpg）<br><br>
     * 发送图片信息的时候的方法为:<b>sendxxxxxMsg(getImgCQCode("xxx"));</b>
     * 会员才能发送音频
     * @param audioFile 音频的名称
     * @return 音频的CQ码
     */
    public String getAudioCQCode(String audioFile, boolean magic) {
        return "[CQ:record,file=" + audioFile + (magic ? ",magic=true" : "") + "]";
    }
}