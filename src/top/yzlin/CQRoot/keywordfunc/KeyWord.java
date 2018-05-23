package top.yzlin.CQRoot.keywordfunc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import top.yzlin.CQRoot.CQRoot;
import top.yzlin.CQRoot.msginterface.MsgSolution;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

/**
 * 关键字回复的增强
 * @author 49968
 */
public class KeyWord {

    private TreeMap<String,String> keyWord=new TreeMap<>();
    private HashSet<String> adminSet=new HashSet<>();
    private ArrayList<KeyWordFunction> keyWordFunctionsList;

    private HashMap<String,MsgSolution> KeyWordMethodMap=new HashMap<>();

    private CQRoot mt;
    private String GID;

    private File xmlFile;
    private Document xmlDoc;

    /**
     * 这个对象实例化之后，会载入文档
     * @param GID
     * @param mt
     */
    public KeyWord(String GID,CQRoot mt){
        this.mt=mt;
        this.GID=GID;
        mt.addMsgSolution(this::adminSolution);
        mt.keyWordSolution(GID,keyWord,CQRoot.GET_GROUP_MSG);

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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
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
     * 添加功能关键字
     * @param key
     * @param word
     */
    public void addFunctionKeyWord(String key,String word){
        String keyToRegex=key.replaceAll("\\$[A-Za-z0-9]+_","[\\s]+");


        //添加事件
        mt.addMsgSolution((HashMap<String,String> Msg)->{
            if(valueOf(CQRoot.GET_GROUP_MSG).equals(Msg.get("act"))){

            }
        });
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
     * 接收到来自QQ的信息
     * @param map
     */
    private void adminSolution(HashMap<String,String> map){
        String fromQQ=map.get("fromQQ");
        if(valueOf(CQRoot.GET_PERSON_MSG).equals(map.get("act")) && adminSet.contains(fromQQ)){
            String msg=map.get("msg");
            keyWordFunctionsList.forEach(i->{
                if(i.judgeText(msg)){
                    mt.sendPersonMsg(fromQQ,i.dealWithText(fromQQ,msg));
                }
            });
        }
    }









    /**
     * 默认功能，包括：
     * 添加关键字
     * 添加临时关键字
     * 查看所有关键字
     * 删除管理员
     * 查询关键字
     * 测试
     */
    private KeyWordFunction[] initKeyWordFunction= new KeyWordFunction[]{
            //添加关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    if(msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：][\\S\\s]+$")) {
                        if(msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：]有效时间[:|：][0-9]+[:|：][\\S\\s]+$")){
                            return false;
                        }else{
                            return true;
                        }
                    }else{
                        return false;
                    }
                }
                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg=msg.substring(6);
                    int firstColon=msg.indexOf(":");
                    int temp=msg.indexOf("：");
                    if(firstColon==-1){
                        firstColon=temp;
                    }else if(temp!=-1){
                        firstColon=temp<firstColon?temp:firstColon;
                    }
                    String key=msg.substring(0,firstColon);
                    msg=msg.substring(firstColon+1);
                    addKeyWord(key,msg);
                    Tools.print("来自"+fromQQ+"添加了关键字"+key);
                    return "关键字:"+key+"已添加";
                }
            },
            //添加临时关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：]有效时间[:|：][0-9]+[:|：][\\S\\s]+$");
                }
                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg=msg.substring(6);
                    int firstColon=msg.indexOf(":");
                    int temp=msg.indexOf("：");
                    if(firstColon==-1){
                        firstColon=temp;
                    }else if(temp!=-1){
                        firstColon=temp<firstColon?temp:firstColon;
                    }
                    String key=msg.substring(0,firstColon);
                    msg=msg.substring(firstColon+6);
                    firstColon=msg.indexOf(":");
                    temp=msg.indexOf("：");
                    if(firstColon==-1){
                        firstColon=temp;
                    }else if(temp!=-1){
                        firstColon=temp<firstColon?temp:firstColon;
                    }
                    String time=msg.substring(0,firstColon);
                    msg=msg.substring(firstColon+1);

                    addKeyWord(key,msg);
                    new Thread(()->{
                        Tools.sleep(Long.parseLong(time)*1000);
                        if(keyWord.containsKey(key)) {
                            removeKeyWord(key);
                            Tools.print("来自"+fromQQ+"添加的关键字"+key+"过期");
                            mt.sendPersonMsg(fromQQ,"关键字"+key+"已过期");
                        }else{
                            Tools.print("来自"+fromQQ+"添加的关键字"+key+"在过期之前被删除");
                        }
                    }).start();
                    Tools.print("来自"+fromQQ+"添加了有效时为"+time+"的关键字"+key);
                    return "临时关键字:"+key+"已添加";
                }
            },
            //添加功能关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^添加功能关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：][\\S\\s]+$");
                }
                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg=msg.substring(6);
                    int firstColon=msg.indexOf(":");
                    int temp=msg.indexOf("：");
                    if(firstColon==-1){
                        firstColon=temp;
                    }else if(temp!=-1){
                        firstColon=temp<firstColon?temp:firstColon;
                    }
                    String key=msg.substring(0,firstColon);
                    HashSet<String> tempSet=new HashSet<>();


                    Matcher m=Pattern.compile("\\$[A-Za-z0-9]+_").matcher(key);
                    String test;
                    while(m.find()){
                        test=m.group();
                        if(tempSet.contains(test)){
                            return "关键字添加失败，出现了重复变量名";
                        }else{
                            tempSet.add(test);
                        }
                    }
                    if(!msg.matches("")){
                        return "返回信息中不含插件与插件参数";
                    }

                    msg=msg.substring(firstColon+1);
                    addFunctionKeyWord(key,msg);
                    Tools.print("来自"+fromQQ+"添加了功能关键字"+key);
                    return "添加成功";
                }
            },
            //删除关键字
            new KeyWordFunction() {
                @Override
                public boolean judgeText(String msg) {
                    return msg.matches("^删除关键字[:|：][\u4e00-\u9fa5|\\w]+");
                }
                @Override
                public String dealWithText(String fromQQ, String msg) {
                    msg=msg.substring(6,msg.length());
                    if(keyWord.containsKey(msg)){
                        removeKeyWord(msg);
                        Tools.print("来自"+fromQQ+"删除了关键字"+msg);
                        return "关键字:"+msg+"已删除";
                    }else{
                        return "不存在关键字:"+msg;
                    }
                }
            },
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
                msg=msg.substring(6,msg.length());
                if(msg.equals("499680328")){
                    Tools.print(fromQQ+"试图删除开发者");
                    return "删除开发者？你有病吧。你的操作已经被记录。";
                }else if(adminSet.contains(msg)){
                    removeAdmin(msg);
                    Tools.print(fromQQ+"删除了"+msg);
                    return "管理员"+msg+"已删除";
                }else{
                    return "管理员"+msg+"不存在";
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
                msg=msg.substring(6,msg.length());
                if(!keyWord.containsKey(msg)){
                    return "不存在该关键字";
                }else{
                    Tools.print(fromQQ+"查询关键字"+msg);
                    return "该关键字的回复消息："+keyWord.get(msg);
                }
            }
            },
            //测试存活
            new KeyWordFunction() {
            @Override
            public boolean judgeText(String msg) {
                return "测试".equals(msg);
            }
            @Override
            public String dealWithText(String fromQQ, String msg) {
                return "确认存活";
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
