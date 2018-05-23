package top.yzlin.CQRoot;

import net.sf.json.JSONObject;

import top.yzlin.CQRoot.cqinfo.AbstractInfo;
import top.yzlin.CQRoot.cqinfo.DiscussMsgInfo;
import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.cqinfo.PersonMsgInfo;
import top.yzlin.CQRoot.msginterface.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import top.yzlin.tools.Tools;

import static java.lang.String.valueOf;

/**
 * MsgTools是通过酷Q机器人的LEMOC插件，与Q群机器人进行通信的类
 * <br><br>
 * 有关接收信息方法的重写与开发,请访问:<a href="https://cqp.cc/t/29722">https://cqp.cc/t/29722</a><br><br>
 * 有关CQ码,请访问:<a href="https://d.cqp.me/Pro/CQ%E7%A0%81">https://d.cqp.me/Pro/CQ%E7%A0%81</a>
 *
 * @author yzlin
 *
 */
public class CQRoot {

	// <editor-fold desc="各种状态码">
        // <editor-fold desc="接收到的状态码">
	/**接收群信息*/
	public final static int GET_GROUP_MSG = 2;
	/**接收讨论组信息*/
	public final static int GET_DISCUSS_MSG = 4;
	/**接收私聊(个人)信息*/
	public final static int GET_PERSON_MSG=21;
	/**管理员变动*/
    public final static int GET_GROUP_ADMIN_CHANGE=101;
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
    public final static int GET_GROUP_DELETE_MEMBER = 120;
    /**发送群消息*/
	public final static int SEND_GROUP_MSG = 101;
	/**发送讨论组消息*/
	public final static int SEND_DISCUSS_MSG = 103;
	/**发送私聊(个人)消息*/
	public final static int SEND_PERSON_MSG = 106;
	/**发送赞*/
	public final static int SEND_PRAISE = 110;
	/**全群禁言*/
	public final static int GROUP_BANNED = 123;
	/**匿名成员禁言*/
	public final static int ANONYMOUS_MEMBER_BANNED = 124;
	/**群匿名设置*/
	public final static int SET_GROUP_ANONYMOUS = 125;
	/**群成员名片设置*/
	public final static int SET_MEMBER_CARD = 126;
	/**群成员专属头衔*/
	public final static int SET_MEMBER_SPECIAL_TITLE = 128;

	/**json串方式返回群成员信息*/
	public final static int GET_MEMBER_INFOMATION = 20303;
	/**json串方式返回陌生人信息*/
	public final static int GET_STRANGER_INFOMATION = 25304;

	// </editor-fold>

    // <editor-fold desc="事件注册容器">
    //老方法，事件处理
	private ArrayList<MsgSolution> methodHashMapList =new ArrayList<>();
	//群信息回复事件处理
    private ArrayList<GroupMsgReply> groupMsgReplyList=new ArrayList<>();
    //讨论组回复事件处理
    private ArrayList<DiscussMsgReply> discussMsgReplyList=new ArrayList<>();
    //个人回复事件处理
    private ArrayList<PersonMsgReply> personMsgReplyList=new ArrayList<>();
    //群消息普通处理
    private ArrayList<GroupMsgSolution> groupMsgSolutionList=new ArrayList<>();
    //讨论组普通处理
    private ArrayList<DiscussMsgSolution> discussMsgSolutionList=new ArrayList<>();
    //个人回普通处理
    private ArrayList<PersonMsgSolution> personMsgSolutionList=new ArrayList<>();
    //通用信息处理
    private ArrayList<NewMsgSolution> newMsgSolutionList=new ArrayList<>();
    // </editor-fold>

    private WebSocketClient client;
    //插件容器
	private HashMap<String,MsgPlugins> plugins=new HashMap<>();
	//群成员增加回复话语
	private HashMap<String,String> newMember=new HashMap<>();

	/**
	 * 实例化一个链接的客户端，本地
	 * @param port webSocket的端口
	 */
	public CQRoot(int port){
		this(Integer.toString(port));
	}

    /**
     * 实例化一个链接的客户端，本地
     * @param port webSocket的端口
     */
    public CQRoot(String port){
        this("ws://localhost",port);
    }
	/**
	 * 实例化一个链接的客户端
     * @param wsPath 按照某个地址来创建
	 * @param port webSocket的端口
	 */
	public CQRoot(String wsPath,String port){
        //配置服务器
        try {
            client = new WebSocketClient(new URI(wsPath+':'+port),new Draft_17()) {
                public void onOpen(ServerHandshake arg0) {
                    Tools.print("成功链接webSocket,端口:"+port);
                }
                public void onMessage(String arg) {
					CQRoot.this.onMessage(arg);
                }
                public void onError(Exception arg0) {
                    arg0.printStackTrace();
                    Tools.log(arg0.getMessage());
                    Tools.print("CQRoot出错了，炸了炸了");
                }
                public void onClose(int arg0, String arg1, boolean arg2) {
                    Tools.print("关闭连接，断开");
                }
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

        //群成员欢迎事件
        addMsgSolution((HashMap<String,String> Msg)-> {
            if(valueOf(GET_GROUP_MEMBER_INCREASE).equals(Msg.get("act"))){
                String QQGID=Msg.get("fromGroup");
                if(newMember.containsKey(QQGID)){
                    sendGroupMsg(QQGID,newMember.get(QQGID));
                    Tools.print(QQGID+"群成员增加");
                }
            }
        });
	}

	// <editor-fold desc="基础发送功能实现">

	/**
	 * 发送个人信息
	 * @param QQID QQ号
	 * @param msg 发送信息
	 */
	public void sendPersonMsg(String QQID, String msg){
		JSONObject text=new JSONObject();
		text.put("act",SEND_PERSON_MSG);
		text.put("QQID",QQID);
		text.put("msg",pluginsMsg(msg));
		client.send(text.toString());
	}

	/**
	 * 发送Q群信息
	 * @param GID Q群号码
	 * @param msg 发送信息
	 */
	public void sendGroupMsg(String GID,String msg){
		JSONObject text=new JSONObject();
		text.put("act",SEND_GROUP_MSG);
		text.put("groupid",GID);
		text.put("msg",pluginsMsg(msg));
		client.send(text.toString());
	}

	/**
	 * 发送讨论组信息
	 * @param DID 讨论组号码
	 * @param msg 发送信息
	 */
	public void sendDiscussMsg(String DID, String msg){
		JSONObject text=new JSONObject();
		text.put("act",SEND_DISCUSS_MSG);
		text.put("discussid",DID);
		text.put("msg",pluginsMsg(msg));
		client.send(text.toString());
	}
	// </editor-fold>

	// <editor-fold desc="接收事件处理">

    /**
     * 接收信息的处理方法
     * @param msg
     */
	private void onMessage(String msg){

		JSONObject jArg=JSONObject.fromObject(msg);
        newMsgSolution(jArg);

        AbstractInfo ait=AbstractInfo.getInfo(jArg);
        newMsgSolutionList.forEach(temp->{
            temp.msgSolution(ait);
        });

        //字符串转化为HashMap再进行处理
		//老式事件处理方法
		HashMap <String,String> tempMap=new HashMap<>();
		for (Object key:jArg.keySet()) {
			tempMap.put((String)key,jArg.get(key).toString());
		}
		methodHashMapList.forEach(i->{
			i.msgSolution((HashMap<String,String>)tempMap.clone());
		});
	}

    /**
     * 信息分类回复处理
     * @param jo
     */
    private void newMsgSolution(JSONObject jo){
	    switch(jo.getInt("act")) {
            case CQRoot.GET_GROUP_MSG:
                GroupMsgInfo tempInfoG=new GroupMsgInfo(jo);
                groupMsgReplyList.forEach(temp->{
                    if(temp.filter(tempInfoG)){
                        sendGroupMsg(tempInfoG.getFromGroup(),temp.replyMsg(tempInfoG));
                    }
                });
                groupMsgSolutionList.forEach(temp->{
                    temp.msgSolution(tempInfoG);
                });
                break;
            case CQRoot.GET_DISCUSS_MSG:
                DiscussMsgInfo tempInfoD=new DiscussMsgInfo(jo);
                discussMsgReplyList.forEach(temp->{
                    if(temp.filter(tempInfoD)) {
                        sendDiscussMsg(tempInfoD.getFromDiscuss(), temp.replyMsg(tempInfoD));
                    }
                });
                discussMsgSolutionList.forEach(temp->{
                    temp.msgSolution(tempInfoD);
                });

                break;
            case CQRoot.GET_PERSON_MSG:
                PersonMsgInfo tempInfoP=new PersonMsgInfo(jo);
                personMsgReplyList.forEach(temp->{
                    if(temp.filter(tempInfoP)) {
                        sendPersonMsg(tempInfoP.getFromQQ(), temp.replyMsg(tempInfoP));
                    }
                });
                personMsgSolutionList.forEach(temp->{
                    temp.msgSolution(tempInfoP);
                });
                break;
        }
    }

    // <editor-fold desc="处理方法的添加与删除">

    /**
     * 老方法，不推荐使用
     * @param msm 注册事件
     * @return 是否注册成功
     */
	public boolean addMsgSolution(MsgSolution msm){
        return methodHashMapList.add(msm);
	}

    /**
     * 添加个人事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgSolution(PersonMsgSolution msm){
        return personMsgSolutionList.add(msm);
    }

    /**
     * 添加讨论组事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgSolution(DiscussMsgSolution msm){
        return discussMsgSolutionList.add(msm);
    }

    /**
     * 添加群事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgSolution(GroupMsgSolution msm){
        return groupMsgSolutionList.add(msm);
    }

    /**
     * 添加个人回复事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgReply(PersonMsgReply msm){
        return personMsgReplyList.add(msm);
    }

    /**
     * 添加讨论组回复事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgReply(DiscussMsgReply msm){
        return discussMsgReplyList.add(msm);
    }

    /**
     * 添加群回复事件处理
     * @param msm 注册事件
     * @return 是否注册成功
     */
    public boolean addMsgReply(GroupMsgReply msm){
        return groupMsgReplyList.add(msm);
    }

    /**
     * 老方法，不推荐使用
     * @param msm
     * @return
     */
	public boolean removeMsgSolution(MsgSolution msm){
		return methodHashMapList.remove(msm);
	}

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgSolution(PersonMsgSolution msm){
        return personMsgSolutionList.remove(msm);
    }

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgSolution(DiscussMsgSolution msm){
        return discussMsgSolutionList.remove(msm);
    }

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgSolution(GroupMsgSolution msm){
        return groupMsgSolutionList.remove(msm);
    }

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgReply(PersonMsgReply msm){
        return personMsgReplyList.remove(msm);
    }

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgReply(DiscussMsgReply msm){
        return discussMsgReplyList.remove(msm);
    }

    /**
     * 删除事件
     * @param msm 删除事件
     * @return 是否删除成功
     */
    public boolean removeMsgReply(GroupMsgReply msm){
        return groupMsgReplyList.remove(msm);
    }
    // </editor-fold>
	// </editor-fold>

	// <editor-fold desc="其他基础功能">

	/**
	 * 重写这个方法，在close方法调用之前运行
	 */
	protected void destruct(){}

	/**
	 * 关闭链接与线程
	 */
	public void close(){
		destruct();
		client.close();
	}
	// </editor-fold>

	// <editor-fold desc="其他功能实现">

	/**
	 * 获取图片的CQ码<br>
	 * 图片存放在酷Q目录的data\image\下<br><br>
	 * 举例：<b>getImgCQCode("1.jpg");</b>（发送data\image\1.jpg）<br><br>
	 * 发送图片信息的时候的方法为:<b>sendxxxxxMsg(getImgCQCode("xxx"));</b>
	 * 会员才能发送图片
	 * @param imgFile 图片的名称
	 * @return 图片的CQ码
	 */
	public String getImgCQCode(String imgFile){
		return "[CQ:image,file="+imgFile+"]";
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
	public String getAudioCQCode(String audioFile,boolean magic){
		return "[CQ:record,file="+audioFile+(magic?",magic=true":"")+"]";
	}





	/**
	 * 为了提高代码利用率被迫写出来的
	 * @param msgAct 反正就是转换中间的那个关键字
	 * @return 关键字
	 */
	private String fromStr(int msgAct){
		switch (msgAct){
			case GET_PERSON_MSG:
				return "fromQQ";
			case GET_GROUP_MSG:
				return "fromGroup";
			case GET_DISCUSS_MSG:
				return "fromDiscuss";
			default:
				return "fromGroup";
		}
	}

	/**
	 * 关键字回复
	 * @param ID 群号码
	 * @param text 关键字
	 * @param returnText 返回信息
	 * @param msgAct 设置信息类型，默认为群ID处理
	 *               GET_PERSON_MSG，GET_GROUP_MSG，与GET_DISSCUSS_MSG
	 */
	public void keyWordSolution(String ID,String text,String returnText,int msgAct){
		if(null == ID || null == text){
			return;
		}
		String fromID=fromStr(msgAct);

		addMsgSolution((MsgSolution) Msg -> {
            if(valueOf(msgAct).equals(Msg.get("act")) && ID.equals(Msg.get(fromID)) && Msg.get("msg").equals(text)){
                switch (msgAct){
                    case GET_PERSON_MSG:
                        sendPersonMsg(ID,returnText);
                        Tools.print(ID+"好友出现关键字:"+text);
                        break;
                    case GET_GROUP_MSG:
                        sendGroupMsg(ID,returnText);
                        Tools.print(ID+"群出现关键字:"+text);
                        break;
                    case GET_DISCUSS_MSG:
                        sendDiscussMsg(ID,returnText);
                        Tools.print(ID+"讨论组出现关键字:"+text);
                        break;
                    default:
                        sendGroupMsg(ID,returnText);
                        Tools.print(ID+"群出现关键字:"+text);
                }

            }
        });
	}

	/**
	 * 关键字回复
	 * @param ID 群号码
	 * @param map 键为关键字，值为返回字
	 * @param msgAct 设置信息类型，默认为群ID处理
	 */
	public void keyWordSolution(String ID, Map<String,String> map, int msgAct){

		if(null == ID || null == map){
			return;
		}
		String fromID=fromStr(msgAct);

		addMsgSolution((MsgSolution) Msg -> {
			if(map.size()==0){
				return;
			}
			String text=Msg.get("msg");
			if(valueOf(msgAct).equals(Msg.get("act")) && ID.equals(Msg.get(fromID)) && map.containsKey(text)){
				switch (msgAct){
					case GET_PERSON_MSG:
						sendPersonMsg(ID,map.get(text));
						Tools.print(ID+"好友出现关键字:"+text);
						break;
					case GET_GROUP_MSG:
						sendGroupMsg(ID,map.get(text));
						Tools.print(ID+"群出现关键字:"+text);
						break;
					case GET_DISCUSS_MSG:
						sendDiscussMsg(ID,map.get(text));
						Tools.print(ID+"讨论组出现关键字:"+text);
						break;
					default:
						sendGroupMsg(ID,map.get(text));
						Tools.print(ID+"群出现关键字:"+text);
				}

			}
		});
	}


	/**
	 * 群成员增加返回信息
	 * @param QQGID QQ群号码
	 * @param returnText 返回信息
	 */
	public void groupMemberIncrease(String QQGID,String returnText){
		if(QQGID==null||returnText==null){
			return;
		}
		newMember.put(QQGID,returnText);
	}
	// </editor-fold>


    /**
     * 从外部来定义插件，手动加载插件
     * @param plugin 插件的对象
     */
    public void addPlugins(MsgPlugins plugin){
        plugins.put(plugin.getClass().getName(),plugin);
    }

	/**
	 * 插件的解析，在信息发送之前会处理一次
	 * @param msg
	 * @return
	 */
	private String pluginsMsg(String msg){
		msg=msg.replace("&#91;","[");
		msg=msg.replace("&#93;","]");
//        Pattern func=Pattern.compile("\\[function\\:[A-Za-z0-9_]+\\:[\\S\\s]+\\]");

		if(!msg.matches("[\\S\\s]*\\[function\\:[A-Za-z0-9_]+\\:[\\S\\s]+\\][\\S\\s]*")){
			return msg;
		}

		MsgPlugins mp;


		int l=msg.indexOf("[function:")+10;
		int m=msg.indexOf(':',l);
		String temp="top.yzlin.plugins."+msg.substring(l,m);

		mp=plugins.get(temp);
		//通过反射来获取插件的实例
		if(mp==null){
			try {
				Class cTemp=Class.forName(temp);
				Class intFace[]=cTemp.getInterfaces();
				boolean bTemp=true;
				for(int i=intFace.length-1;i>=0;i--){
					if("top.yzlin.CQRoot.MsgPlugins".equals(intFace[i].getName())){
						bTemp=false;
						break;
					}
				}
				if(bTemp){
					Tools.print(temp+"插件没有实现接口");
					return "有该插件但是该插件没有实现相应接口";
				}
				mp=(MsgPlugins)cTemp.newInstance();
				plugins.put(temp,mp);
			} catch (ClassNotFoundException e) {
				Tools.print("找不到"+temp+"插件");
				return "找不到插件";
			} catch (IllegalAccessException e) {
				Tools.print("创建"+temp+"插件实例时发生错误1");
				return "在创建插件实例时发生错误1";
			} catch (InstantiationException e) {
				Tools.print("创建"+temp+"插件实例时发生错误2");
				return "在创建插件实例时发生错误2";
			}
		}

		//从插件获取的字符串替换掉信息里的代码
		int n=msg.indexOf(']',m);
		temp=msg.substring(m+1,n);
		return pluginsMsg(msg.substring(0,l-10)+mp.solutionText(temp)+msg.substring(n+1));
	}



}