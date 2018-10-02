package top.yzlin.Raise;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.msginterface.reply.GroupMsgReply;
import top.yzlin.tools.Tools;
import top.yzlin.tools.XMLTools;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 */
public class RaffleRaise {
    private String QQGID;
    protected double minLimit = 100000000;
    private String admin;

    private HashMap<String, HashMap<String, Integer>> rafflePrizeMap = new HashMap<>();//数据快速读取

    private RandomRaise randomRaise = new RandomRaise();

    private RaiseStrategy raiseStrategy = null;
    protected Function<RafflePrize, String> listFunction = null;

    private String prizeKind = "奖品";
    protected String upText = "", downText = "";

    private File xmlFile;
    private Document xmlDoc;

    public RaffleRaise(String QQGID) {
        xmlFile = new File("doc\\raffleRaise\\" + Tools.MD5(QQGID).substring(5, 20) + ".xml");
        if (!xmlFile.exists()) {
            try {
                File temp = new File("doc\\raffleRaise");
                if (!temp.exists()) {
                    temp.mkdir();
                }
                Tools.print("创建新文档");
                FileWriter os = new FileWriter(xmlFile);
                os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<RaffleRecordDoc GID=\"" + QQGID + "\">\n" +
                        "</RaffleRecordDoc>");
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                Tools.print("文档创建失败");
            }
        }
        try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            NodeList peopleList = xmlDoc.getElementsByTagName("user");

            for (int i = peopleList.getLength() - 1; i >= 0; i--) {
                Element son = (Element) peopleList.item(i);
                HashMap<String, Integer> tempMap = new HashMap<>();
                rafflePrizeMap.put(son.getAttribute("nickName"), tempMap);
                NodeList prizeList = son.getElementsByTagName("rafflePrize");
                for (int j = prizeList.getLength() - 1; j >= 0; j--) {
                    son = (Element) prizeList.item(j);
                    tempMap.put(son.getAttribute("prize"), Integer.parseInt(son.getAttribute("num")));
                }
            }
            Tools.print("抽奖文档加载完成");
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关于抽奖
     *
     * @param am 集资项目
     */
    public RaffleRaise(AbstractMonitoring am) {
        QQGID = am.getQQGID();
        CQRoot QQMT = am.getQQMT();
        am.setRaiseEvent(raiseData -> sendText(raiseData.getNickName(), raiseData.getRaiseMoney()));

        QQMT.addMsgSolution(new GroupMsgReply() {
            @Override
            public boolean checkMsg(String from) {
                return from.matches("查询" + prizeKind + "获得情况[:|：][\\S]+");
            }

            @Override
            public boolean fromGroup(String from) {
                return QQGID.equals(from);
            }

            @Override
            public String replyMsg(GroupMsgInfo a) {
                return keyWordEvent(a.getMsg().substring(7 + prizeKind.length()));
            }
        });

        //创建xml文档或载入文档
        xmlFile = new File("doc\\raffleRaise\\" + Tools.MD5(QQGID).substring(5, 20) + ".xml");
        if (!xmlFile.exists()) {
            try {
                File temp = new File("doc\\raffleRaise");
                if (!temp.exists()) {
                    temp.mkdir();
                }
                Tools.print("创建新文档");
                FileWriter os = new FileWriter(xmlFile);
                os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<RaffleRecordDoc GID=\"" + QQGID + "\">\n" +
                        "</RaffleRecordDoc>");
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                Tools.print("文档创建失败");
            }
        }
        try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            NodeList peopleList = xmlDoc.getElementsByTagName("user");
            for (int i = peopleList.getLength() - 1; i >= 0; i--) {
                Element son = (Element) peopleList.item(i);
                HashMap<String, Integer> tempMap = new HashMap<>();
                rafflePrizeMap.put(son.getAttribute("nickName"), tempMap);
                NodeList prizeList = son.getElementsByTagName("rafflePrize");
                for (int j = prizeList.getLength() - 1; j >= 0; j--) {
                    son = (Element) prizeList.item(j);
                    tempMap.put(son.getAttribute("prize"), Integer.parseInt(son.getAttribute("num")));
                }
            }
            Tools.print("抽奖文档加载完成");
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置发送信息的头与尾部
     *
     * @param upText
     */
    public void setText(String upText, String downText) {
        this.upText = (upText == null ? "" : upText);
        this.downText = (downText == null ? "" : downText);
    }

    /**
     * 设置多少钱一棒起步
     *
     * @param minLimit 价格
     */
    public void setMinLimit(double minLimit) {
        this.minLimit = minLimit - 0.01;
    }

    /**
     * 奖品种类，用于回复信息
     *
     * @param prizeKind
     */
    public void setPrizeKind(String prizeKind) {
        this.prizeKind = prizeKind;
    }

    /**
     * 设置抽奖策略
     *
     * @param raiseStrategy
     */
    public void setRaiseStrategy(RaiseStrategy raiseStrategy) {
        this.raiseStrategy = raiseStrategy;
    }

    /**
     * 设置最好的卡牌的额外处理
     * 一般是用来做图片发送
     *
     * @param listFunction
     */
    public void setListFunction(Function<RafflePrize, String> listFunction) {
        this.listFunction = listFunction;
    }


    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    /**
     * 集资接收，从集资监控机器人触发
     *
     * @param name  谁集资了
     * @param money 集资金额
     * @return
     */
    protected String sendText(String name, double money) {
        StringBuilder str = new StringBuilder();
        if (raiseStrategy != null && raiseStrategy.isRaiseMoney(money, minLimit) && raiseStrategy.raiseName(name)) {
            str.append('\n').append(upText).append('\n');
            RafflePrize tr;
            StringBuilder log = new StringBuilder();
            RafflePrize min = RafflePrize.empty;
            Map<RafflePrize, Integer> raise = new HashMap<>();
            while (raiseStrategy.nextRaffle()) {
                tr = raffle();
                saveData(name, tr);

                if (tr.getProbability() < min.getProbability()) {
                    min = tr;
                }
                raise.put(tr, raise.containsKey(tr) ? raise.get(tr) + 1 : 1);

                log.append(tr.getPrize());
                log.append(' ');
            }

            raise.forEach((k, v) -> str.append(k.getPrize()).append('×').append(v).append('\n'));

            if (listFunction != null) {
                str.append(listFunction.apply(min)).append('\n');
            }
            Tools.print(name + "抽到了" + log.toString());
            str.append(downText);
        }
        return str.toString();
    }

    public void setRandomRaise(RandomRaise randomRaise) {
        this.randomRaise = randomRaise;
    }

    public int getProbabilitySum() {
        return randomRaise.getProbabilitySum();
    }

    public Random getRand() {
        return randomRaise.getRand();
    }

    public ArrayList<RafflePrize> getRafflePrizeList() {
        return randomRaise.getRafflePrizeList();
    }

    public void setRafflePrizeList(ArrayList<RafflePrize> rafflePrizeList) {
        randomRaise.setRafflePrizeList(rafflePrizeList);
    }

    public void addRafflePrize(RafflePrize... prize) {
        randomRaise.addRafflePrize(prize);
    }

    public RafflePrize raffle() {
        return randomRaise.raffle();
    }

    /**
     * 存档
     * @param name
     * @param rafflePrize
     */
    protected void saveData(String name, RafflePrize rafflePrize) {
        if (!rafflePrizeMap.containsKey(name)) {
            rafflePrizeMap.put(name, new HashMap<>());
        }
        HashMap<String, Integer> tempMap = rafflePrizeMap.get(name);
        String prize = rafflePrize.getPrize();
        int c = (tempMap.containsKey(prize) ? tempMap.get(prize) + 1 : 1);

        tempMap.put(prize, c);

        //写入xml文档
        Node tn = XMLTools.selectNode("/RaffleRecordDoc/user[@nickName='" + name + "']", xmlDoc);
        if (tn == null) {
            Element eKey = xmlDoc.createElement("user");
            eKey.setAttribute("nickName", name);
            Element eKey2 = xmlDoc.createElement("rafflePrize");
            eKey2.setAttribute("prize", prize);
            eKey2.setAttribute("num", String.valueOf(c));
            eKey.appendChild(eKey2);
            xmlDoc.getDocumentElement().appendChild(eKey);
        } else {
            Element tn2 = (Element) XMLTools.selectNode("/RaffleRecordDoc/user[@nickName='" + name + "']/rafflePrize[@prize='" + prize + "']", xmlDoc);
            if (tn2 == null) {
                Element eKey = xmlDoc.createElement("rafflePrize");
                eKey.setAttribute("prize", prize);
                eKey.setAttribute("num", String.valueOf(c));
                tn.appendChild(eKey);
            } else {
                tn2.setAttribute("num", String.valueOf(c));
            }
        }
        XMLTools.saveDocument(xmlDoc, xmlFile);
    }

    /**
     * 关键字回复
     *
     * @param name
     */
    private String keyWordEvent(String name) {
        if (rafflePrizeMap.containsKey(name)) {
            HashMap<String, Integer> tempMap = rafflePrizeMap.get(name);
            StringBuilder strb = new StringBuilder(prizeKind + "获得情况:");
            tempMap.forEach((k, v) -> strb.append('\n').append(k).append(":").append(v.intValue()));
            return strb.toString();
        } else {
            return "没有关于这个人的集资记录";
        }
    }
}