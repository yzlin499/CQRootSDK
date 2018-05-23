package top.yzlin.CQRoot.keywordfunc;

/**
 * 添加keyWord事件
 */
interface KeyWordFunction {
    /**
     * 判断发送过来的字符串是否符合条件，
     * 在KeyWord中，如果不符合就会跳过，如果符合判定就会执行dealWithText
     * @param msg 从QQ获取到的信息
     * @return 返回true的话，就会将msg进入dealWithText处理
     */
    boolean judgeText(String msg);

    /**
     * 处理QQ发送的信息并且将其返回
     * @param msg 从QQ获取到的信息
     * @param fromQQ
     * @return 处理之后的信息，用来回复给
     */
    String dealWithText(String fromQQ,String msg);
}
