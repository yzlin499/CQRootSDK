package top.yzlin.douyinquery;

import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

public class DouYinMonitoring implements Runnable {
    private DouYin douYin;
    private CQRoot cqRoot;
    private String gid;
    private long lastDouYinID;

    public DouYinMonitoring(CQRoot cqRoot, String gid, String name) {
        Tools.print(name + "抖音监控启动");
        this.gid = gid;
        this.cqRoot = cqRoot;
        douYin = new DouYin(name);
        lastDouYinID = douYin.getData()[0].getDouyinID();
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            DouYinInfo[] douYinInfos = douYin.getData(lastDouYinID);
            if (douYinInfos.length > 0) {
                lastDouYinID = douYinInfos[0].getDouyinID();
                for (DouYinInfo d : douYinInfos) {
                    pushMsg(d);
                }
            }
            Tools.sleep(1000 * 60);
        }
    }

    private void pushMsg(DouYinInfo douYinInfo) {
        Tools.print(douYinInfo.getMemberName() + "发了一个抖音" +
                douYinInfo.getTitle());
        cqRoot.sendGroupMsg(gid, sendMsg(douYinInfo));
    }

    protected String sendMsg(DouYinInfo douYinInfo) {
        return douYinInfo.getMemberName() + "发了一个抖音\n" +
                douYinInfo.getTitle();
    }
}
