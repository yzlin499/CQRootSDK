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
import java.time.Clock;
import java.util.*;
import java.util.function.Function;

/**
 * @param <PT>
 */
public class RaffleRaise<PT> {
    private String QQGID;
    private CQRoot QQMT;
    private double minLimit = 1000000;

    private ArrayList<RafflePrize<PT>> rafflePrizeList = new ArrayList<>();
    private HashMap<String, HashMap<String, Integer>> rafflePrizeMap = new HashMap<>();

    private int probabilitySum;//奖品集合的概率总和
    private Random rand = new Random(Clock.systemDefaultZone().millis());

    private RaiseStrategy raiseStrategy = null;
    private Function<RafflePrize<PT>, String> listFunction = null;

    private String prizeKind = "奖品";
    private String upText = "", downText = "";

    private File xmlFile;
    private Document xmlDoc;

    /**
     * 关于抽奖
     *
     * @param am 集资项目
     */
    public RaffleRaise(AbstractMonitoring am) {
        am.setRaiseEvent(new RaiseEvent() {
            @Override
            public void transferInfo(String GID, CQRoot cqRoot) {
                QQGID = GID;
                QQMT = cqRoot;
            }

            @Override
            public String eventTrigger(String name, String money) {
                return sendText(name, money);
            }
        });

        QQMT.addMsgSolution(new GroupMsgReply() {
            @Override
            public boolean fromGroup(String from) {
                return QQGID.equals(from);
            }

            @Override
            public String replyMsg(GroupMsgInfo a) {
                return keyWordEvent(a);
            }
        });

        //创建xml文档或载入文档
        xmlFile = new File("doc\\raffleRaise\\" + Tools.MD5(QQGID).substring(5, 20) + ".xml");
        if (!xmlFile.exists()) {
            try {
                if (!new File("doc\\raffleRaise").exists()) {
                    new File("doc\\raffleRaise").mkdir();
                }
                Tools.print("创建新文档");
                xmlFile.createNewFile();
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
    public void setListFunction(Function<RafflePrize<PT>, String> listFunction) {
        this.listFunction = listFunction;
    }

    /**
     * 添加奖品
     *
     * @param prize 奖品模型
     */
    public void addRafflePrize(RafflePrize<PT>... prize) {
        rafflePrizeList.addAll(Arrays.asList(prize));
        probabilitySum = rafflePrizeList.stream()
                .mapToInt(RafflePrize::getProbability)
                .sum();
    }

    /**
     * 集资接收，从集资监控机器人触发
     *
     * @param name  谁集资了
     * @param money 集资金额
     * @return
     */
    private String sendText(String name, String money) {
        StringBuilder str = new StringBuilder();
        double moneyTemp = Double.parseDouble(money);
        if (raiseStrategy != null && raiseStrategy.isRaiseMoney(moneyTemp, minLimit) && raiseStrategy.raiseName(name)) {
            str.append('\n');
            str.append(upText);
            str.append('\n');
            RafflePrize tr;
            StringBuilder log = new StringBuilder();
            RafflePrize min = new RafflePrize(null, 2147483647);
            Map<RafflePrize, Integer> raise = new HashMap<>();
            while (raiseStrategy.nextRaffle()) {
                tr = raffle(name);

                if (tr.getProbability() < min.getProbability()) {
                    min = tr;
                }
                if (!raise.containsKey(tr)) {
                    raise.put(tr, 1);
                } else {
                    raise.put(tr, raise.get(tr) + 1);
                }

                log.append(tr.getPrize().toString());
                log.append(' ');
            }
            for (RafflePrize temp : raise.keySet()) {
                str.append(temp.getPrize().toString());
                str.append('×');
                str.append(raise.get(temp));
                str.append('\n');
            }
            if (listFunction != null) {
                str.append(listFunction.apply(min));
                str.append('\n');
            }
            Tools.print(name + "抽到了" + log.toString());
            str.append(downText);
        }
        return str.toString();
    }

    /**
     * 抽卡
     *
     * @param name 名字
     * @return 抽到了哪张卡
     */
    private RafflePrize raffle(String name) {
        if (!rafflePrizeMap.containsKey(name)) {
            rafflePrizeMap.put(name, new HashMap<>());
        }
        int rand = this.rand.nextInt(probabilitySum) + 1;
        int tempCount = 0;

        for (RafflePrize tempPrize : rafflePrizeList) {
            //随机位置
            tempCount += tempPrize.getProbability();
            if (tempCount >= rand) {
                HashMap<String, Integer> tempMap = rafflePrizeMap.get(name);

                String prize = tempPrize.getPrize().toString();
                int c;
                if (tempMap.containsKey(prize)) {
                    c = tempMap.get(prize) + 1;
                } else {
                    c = 1;
                }


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


                return tempPrize;
            }
        }
        return null;
    }

    /**
     * 关键字回复
     *
     * @param msg
     */
    private String keyWordEvent(GroupMsgInfo msg) {
        String m = msg.getMsg();
        if (m.matches("查询" + prizeKind + "获得情况[:|：][\\S]+")) {
            m = m.substring(7 + prizeKind.length());
            if (rafflePrizeMap.containsKey(m)) {
                HashMap<String, Integer> tempMap = rafflePrizeMap.get(m);
                StringBuilder strb = new StringBuilder(prizeKind + "获得情况:");
                for (String tr : tempMap.keySet()) {
                    strb.append('\n');
                    strb.append(tr);
                    strb.append(":");
                    strb.append(tempMap.get(tr).intValue());
                }
                return strb.toString();
            } else {
                return "没有关于这个人的集资记录";
            }
        }
        return null;
    }
}