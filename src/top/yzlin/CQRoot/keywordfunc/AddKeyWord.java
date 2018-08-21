package top.yzlin.CQRoot.keywordfunc;

import top.yzlin.tools.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AddKeyWord implements KeyWordFunction {
    private KeyWord keyWord;
    private Pattern pattern;

    AddKeyWord(KeyWord keyWord) {
        this.keyWord = keyWord;
        pattern = Pattern.compile("^添加关键字[:|：](?<key>[\u4e00-\u9fa5|\\w]+)[:|：](?<word>[\\S\\s]+)$",
                Pattern.DOTALL);
    }

    @Override
    public boolean judgeText(String msg) {
        return msg.matches("^添加关键字[:|：][\u4e00-\u9fa5|\\w]+[:|：][\\S\\s]+$");
    }

    @Override
    public String dealWithText(String fromQQ, String msg) {
        Matcher m = pattern.matcher(msg);
        if (m.find()) {
            String key = m.group("key");
            String word = m.group("word");
            keyWord.addKeyWord(key, word);
            Tools.print("来自" + fromQQ + "添加了关键字" + key);
            return "关键字:" + key + "已添加";
        } else {
            return null;
        }
    }
}
