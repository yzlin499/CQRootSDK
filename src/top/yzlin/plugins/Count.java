package top.yzlin.plugins;

import top.yzlin.CQRoot.MsgPlugins;

import java.util.HashMap;

public class Count implements MsgPlugins {
    private class Number {
        private int initNum;
        int add = 1;

        private Number() {
            initNum = 0;
        }

        private void cUP() {
            initNum += add;
        }

        private String getInitNum() {
            return String.valueOf(initNum);
        }
    }

    private HashMap<String, Number> countMap = new HashMap<>();

    @Override
    public String solutionText(String text) {
        String param[] = MsgPlugins.getParam(text);
        String variable = param[0];
        Number c;
        if (!countMap.containsKey(variable)) {
            c = new Number();
            try {
                switch (param.length) {
                    case 3:
                        c.add = Integer.parseInt(param[2]);
                    case 2:
                        c.initNum = Integer.parseInt(param[1]);
                    case 1:
                        countMap.put(variable, c);
                }
            } catch (NumberFormatException e) {
                countMap.put(variable, c);
            }
        } else {
            c = countMap.get(variable);
        }
        c.cUP();
        return c.getInitNum();
    }
}
