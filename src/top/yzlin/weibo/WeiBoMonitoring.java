package top.yzlin.weibo;

import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

import java.util.function.Predicate;

public class WeiBoMonitoring implements Runnable {
    private WeiBo weiBo;
    private String GID;
    private CQRoot cqRoot;
    private String name;
    private WeiBoInfo lastInfo;
    private Predicate<WeiBoInfo> predicate = i -> ((i.getDate().getTime() - lastInfo.getDate().getTime()) > (1000 * 150)) && (!i.equals(lastInfo));


    public WeiBoMonitoring(WeiBo weiBo, String GID, CQRoot cqRoot) {
        this.weiBo = weiBo;
        this.GID = GID;
        this.cqRoot = cqRoot;
        name = weiBo.getName();
        new Thread(this).start();
    }


    @Override
    public void run() {
        lastInfo = weiBo.getWeiBoData()[0];
        WeiBoInfo[] weiBoInfos;
        while (true) {
            Tools.sleep((1000 * 60 * 5 - ((System.currentTimeMillis() - 1000 * 60) % (1000 * 60 * 5))));
            weiBoInfos = weiBo.getWeiBoData(predicate);
            if (weiBoInfos != null && weiBoInfos.length > 0) {
                for (WeiBoInfo data : weiBoInfos) {
                    sendMsg(data);
                }
                lastInfo = weiBoInfos[0];
            }
        }
    }

    private void sendMsg(WeiBoInfo weiBoInfo) {
        Tools.print(name + "发了微博");
        cqRoot.sendGroupMsg(GID, sendText(weiBoInfo));
    }

    protected String sendText(WeiBoInfo weiBoInfo) {
        if (weiBoInfo.isRepost()) {
            return name + "转发了一条微博:\n" +
                    weiBoInfo.getText() + '\n' +
                    "原文:" + weiBoInfo.getRepostText() + '\n' +
                    "链接:" + Tools.getTinyURL(weiBoInfo.getUrl());
        } else {
            return name + "发了一条微博:\n" +
                    weiBoInfo.getText() + '\n' +
                    "链接:" + Tools.getTinyURL(weiBoInfo.getUrl());
        }
    }
}
