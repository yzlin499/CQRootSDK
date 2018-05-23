package top.yzlin.CQRoot.keywordfunc;

import top.yzlin.tools.Tools;

class AddKeyWord implements KeyWordFunction {
    KeyWord keyWord;
    AddKeyWord(KeyWord keyWord){
        this.keyWord=keyWord;
    }

    /**
     * 判断信息类型
     * @param msg
     * @return
     */
    private int swithcs(String msg){
        if(msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：]部分匹配[:|：][\\S\\s]+$")){
            return 2;
        }else if(msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：]有效时间[:|：][0-9]+[:|：][\\S\\s]+$")){
            return 3;
        }else{
            return 1;
        }
    }

    private String addComicKeyWord(String fromQQ,String msg){
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
        keyWord.addKeyWord(key,msg);
        Tools.print("来自"+fromQQ+"添加了关键字"+key);
        return "关键字:"+key+"已添加";
    }

    @Override
    public boolean judgeText(String msg) {
        return msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：][\\S\\s]+$");
    }

    @Override
    public String dealWithText(String fromQQ, String msg) {
        return null;
    }
}
