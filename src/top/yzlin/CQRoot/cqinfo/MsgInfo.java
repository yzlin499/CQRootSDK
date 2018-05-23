package top.yzlin.CQRoot.cqinfo;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import top.yzlin.tools.Tools;

public class MsgInfo extends AbstractInfo{
    private String msg;
    private String font;
    private String nick="";
    private String sex="";
    private String age="";

    protected MsgInfo(JSONObject text) {
        super(text);
        try {
            this.msg = text.getString("msg");
            this.font = text.getString("font");
            this.sex = text.containsKey("sex")?text.getString("sex"):"";
            this.age = text.containsKey("age")?text.getString("age"):"";
            this.nick = text.containsKey("nick")?text.getString("nick"):"";
        }catch(JSONException ex){
            Tools.print("出现了其他无法识别的无法识别");
        }
    }

    final public String getMsg() {
        return msg;
    }

    final public String getFont() {
        return font;
    }

    final public String getNick() {
        return nick;
    }

    final public String getSex() {
        return sex;
    }

    final public String getAge() {
        return age;
    }


}
