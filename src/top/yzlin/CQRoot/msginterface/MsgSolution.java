package top.yzlin.CQRoot.msginterface;

import java.util.HashMap;

/**
 * 信息处理接口，重写这个方法并在MsgTools使用
 * @author 49968
 *
 */
@FunctionalInterface
public interface MsgSolution {
    void msgSolution(HashMap<String,String> Msg);
}
