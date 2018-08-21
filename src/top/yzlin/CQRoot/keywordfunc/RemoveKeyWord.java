package top.yzlin.CQRoot.keywordfunc;

import top.yzlin.tools.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RemoveKeyWord implements KeyWordFunction {
    private KeyWord keyWord;
    private Pattern pattern;

    RemoveKeyWord(KeyWord keyWord) {
        this.keyWord = keyWord;
        pattern = Pattern.compile("^删除关键字[:|：](?<key>[\u4e00-\u9fa5|\\w]+)$",
                Pattern.DOTALL);
    }

    @Override
    public boolean judgeText(String msg) {
        return msg.matches("^删除关键字[:|：][\u4e00-\u9fa5|\\w]+");
    }

    @Override
    public String dealWithText(String fromQQ, String msg) {
        Matcher m = pattern.matcher(msg);
        if (m.find()) {
            String key = m.group("key");
            if (keyWord.containsKeyWord(key)) {
                keyWord.removeKeyWord(key);
                Tools.print("来自" + fromQQ + "删除了关键字" + key);
                return "关键字:" + msg + "已删除";
            } else {
                return "不存在关键字:" + msg;
            }
        } else {
            return null;
        }
    }
}
