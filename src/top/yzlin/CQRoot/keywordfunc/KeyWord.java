package top.yzlin.CQRoot.keywordfunc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.cqinfo.PersonMsgInfo;
import top.yzlin.CQRoot.msginterface.reply.GroupMsgReply;
import top.yzlin.CQRoot.msginterface.reply.PersonMsgReply;
import top.yzlin.tools.Tools;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 关键字回复的增强
 * @author 49968
 */
public class KeyWord {

    private Map<String, String> keyWord = new HashMap<>();
    private Set<String> adminSet = new HashSet<>();
    private ArrayList<KeyWordFunction> keyWordFunctionsList;

    private String GID;

    private File xmlFile;
    private Document xmlDoc;

    /**
     * 这个对象实例化之后，会载入文档
     * @param GID
     * @param mt
     */
    public KeyWord(String GID,CQRoot mt){
        this.GID=GID;
        mt.addMsgSolution(new GroupMsgReply() {
            @Override
            public boolean fromGroup(String from) {
                return GID.equals(from);
            }

            @Override
            public boolean checkMsg(String from) {
                return keyWord.containsKey(from);
            }

            @Override
            public String replyMsg(GroupMsgInfo a) {
                return keyWord.getOrDefault(a.getMsg(), "");
            }
        });

        mt.addMsgSolution(new PersonMsgReply() {

            @Override
            public boolean fromQQ(String from) {
                return adminSet.contains(from);
            }

            @Override
            public String replyMsg(PersonMsgInfo a) {
                for (KeyWordFunction function : keyWordFunctionsList) {
                    if (function.judgeText(a.getMsg())) {
                        return function.dealWithText(a.getFromQQ(), a.getMsg());
                    }
                }
                return null;
            }
        });

        keyWordFunctionsList= new ArrayList<>(Arrays.asList(initKeyWordFunction));

        //创建xml文档或载入文档
        xmlFile=new File("doc\\"+Tools.MD5(GID).substring(5,15)+".xml");
        if(!xmlFile.exists()){
            try {
                if(!new File("doc").exists()){
                    new File("doc").mkdir();
                }
                Tools.print("创建新文档");
                xmlFile.createNewFile();
                FileWriter os=new FileWriter(xmlFile);
                os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<keyWordDoc GID=\""+GID+"\">\n" +
                        "</keyWordDoc>");
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                Tools.print("文档创建失败");
            }
        }
        try {
            xmlDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            NodeList list = xmlDoc.getElementsByTagName("keyWord");
            for (int i =list.getLength()-1; i >=0 ; i--) {
                Element son = (Element) list.item(i);
                keyWord.put(son.getAttribute("key"),son.getTextContent());
            }
            Tools.print("关键字文档加载完成");
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加新的关键字
     * @param key 关键字
     * @param word 返回消息
     */
    public void addKeyWord(String key,String word){
        if(keyWord.containsKey(key)){
            Element per = (Element) selectSingleNode("/keyWordDoc/keyWord[@key='"+key+"']");
            per.setTextContent(word);
        }else{
            Element eKey = xmlDoc.createElement("keyWord");
            eKey.setAttribute("key", key);
            eKey.setTextContent(word);
            xmlDoc.getDocumentElement().appendChild(eKey);
        }
        saveDocument();
        keyWord.put(key,word);
    }

    /**
     * 删除关键字
     * @param key 关键字
     */
    public void removeKeyWord(String key){
        Element son = (Element) selectSingleNode("/keyWordDoc/keyWord[@key='"+key+"']");
        xmlDoc.getDocumentElement().removeChild(son);
        keyWord.remove(key);
        saveDocument();
    }

    /**
     * 是否存在关键字
     *
     * @param key
     * @return
     */
    public boolean containsKeyWord(String key) {
        return keyWord.containsKey(key);
    }

    /**
     * 添加管理员
     * @param QQID QQ号
     * @return 是否添加成功
     */
    public boolean addAdmin(String QQID){
        Tools.print("为"+GID+"添加了新的管理员"+QQID);
        return adminSet.add(QQID);
    }

    /**
     * 删除管理员
     * @param QQID QQ号
     * @return 是否添加成功
     */
    public boolean removeAdmin(String QQID){
        Tools.print("为"+GID+"删除了管理员"+QQID);
        return adminSet.remove(QQID);
    }

    /**
     * 默认功能，包括：
     * 添加关键字
     * 查看所有关键字
     * 删除管理员
     * 查询关键字
     * 测试
     */
    private KeyWordFunction[] initKeyWordFunction= new KeyWordFunction[]{
            //添加关键字
            new AddKeyWord(this),
            //删除关键字
            new RemoveKeyWord(this),
            //查看所有关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return "查看所有关键字".equals(msg);
                }
                @Override
                public String dealWithText(String fromQQ, String msg) {
                    int ks=keyWord.size();
                    if(ks==0){
                        return "没有任何关键字";
                    }
                    StringBuilder temp=new StringBuilder(ks*2+1);
                    temp.append("以下为所有关键字:");
                    keyWord.keySet().forEach(key->{
                        temp.append('\n');
                        temp.append(key);
                    });
                    Tools.print(fromQQ+"查询所有关键字");
                    return temp.toString();
                }
            },
            //添加管理员
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^添加管理员[:|：][0-9]{9,10}");
                }

                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg=msg.substring(6,msg.length());
                    if(adminSet.contains(msg)){
                        return "管理员"+msg+"已经存在";
                    }else{
                        addAdmin(msg);
                        Tools.print(fromQQ+"添加了管理员："+msg);
                        return "管理员"+msg+"已经添加";
                    }
                }
            },
            //删除管理员
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^删除管理员[:|：][0-9]{9,10}");
                }

                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg = msg.substring(6, msg.length());
                    if (adminSet.contains(msg)) {
                        removeAdmin(msg);
                        Tools.print(fromQQ + "删除了" + msg);
                        return "管理员" + msg + "已删除";
                    } else {
                        return "管理员" + msg + "不存在";
                    }
                }
            },
            //查询关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^查询关键字[:|：][\u4e00-\u9fa5|\\w]+");
                }

                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg = msg.substring(6, msg.length());
                    if (!keyWord.containsKey(msg)) {
                        return "不存在该关键字";
                    } else {
                        Tools.print(fromQQ + "查询关键字" + msg);
                        return "该关键字的回复消息：" + keyWord.get(msg);
                    }
                }
            }
    };



    /**
     * 查找节点
     * @param express xml文档读写的时候用的，可无视
     * @return 节点
     */
    private Node selectSingleNode(String express) {
        try {
            return (Node) XPathFactory.newInstance().newXPath().evaluate(express,xmlDoc.getDocumentElement() , XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存文档
     */
    private void saveDocument(){
        try {
            Transformer former= TransformerFactory.newInstance().newTransformer();
            former.transform(new DOMSource(xmlDoc), new StreamResult(xmlFile));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
