package top.yzlin.CQRoot;

/**
 * 这个方法是消息返回的时候，获取额外插件功能的接口，一般是配合KeyWord使用
 * 每个插件只会创建一次实例，之后将一直调用同一个实例的solutionText()方法
 * 实现这个接口的类，构造方法必须为public且无参
 */
public interface MsgPlugins {
    /**
     * 获取参数列表
     * 如果插件是按照[function:Plugins:Param:<a1,a2,a3...>]或者[function:Plugins:Param:<a1,a2,a3...>:xxxxxxx]的格式传入，
     * 那么用这个方法可以获取参数列表
     * 大概就是，自动将"param:<a1,a2,a3...>:xxxxxx"里面的参数自动提取出来
     * @param text 这个参数一般是solutionText()的传入值
     * @return 返回参数列表，当没有param项或者param项没有写在开头时，返回空数组，不是null
     */
    static String[] getParam(String text){

        if(text.indexOf("param:<")!=0){
            return new String[]{};
        }
        int i=text.indexOf('>');
        if(i<0){
            return new String[]{};
        }
        text=text.substring(7,i);
        return text.split(",");
    }

    /**
     * 获取信息字段，一般是用作除去参数段，如果信息不规范，那么返回字符串本身
     * @param text 这个参数一般是solutionText(text)的传入值
     * @return Param:<a1,a2,a3...>:xxxxxxx返回xxxxxxx
     *         Param:<a1,a2,a3...>xxxxxxx返回Param:<a1,a2,a3...>xxxxxxx
     *         xxxxxx返回xxxxxx
     */
    static String getMsg(String text){
        int i=text.indexOf(">:");
        if(text.indexOf("param:<")!=0 || i<0){
            return text;
        }else{
            return text.substring(i+2);
        }
    }

    /**
     * 在信息发送之前，如果信息里面包含拓展插件字段，那么会将字段里面的信息作为参数传输过来
     * @param text 扩展插件的信息，比如"[function:Plugins:xxxxxxx]",那么传输过来的就是xxxxxxxxxx
     * @return 返回的信息会替换掉原本信息里面的字段，
     *         比如,如果[function:Plugins:xxxxxxx]返回aaa,则"xxxxx[function:Plugins:xxxxxxx]xxx"就会变成"xxxxxaaaxxx"
     */
    String solutionText(String text);
}
