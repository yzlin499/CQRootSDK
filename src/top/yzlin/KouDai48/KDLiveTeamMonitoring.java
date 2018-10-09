package top.yzlin.KouDai48;

import top.yzlin.CQRoot.CQRoot;
import top.yzlin.tools.Tools;

import java.util.*;

/**
 * @author 殷泽凌
 */
public class KDLiveTeamMonitoring implements Runnable {
    private static final KDData KD_DATA = KDData.getInstance();
    private static final KDLive KD_LIVE = new KDLive();

    private CQRoot cqRoot;
    private String gid;
    private long frequency = 1000 * 60;
    private Map<Integer, String> teamMap;

    private Set<Integer> pushedSet = new HashSet<>();

    public KDLiveTeamMonitoring(CQRoot cqRoot, String gid, String team) {
        this(cqRoot, gid, KD_DATA.getTeamIDMap(team));
    }

    public KDLiveTeamMonitoring(CQRoot cqRoot, String gid, Map<Integer, String> teamMap) {
        init(cqRoot, gid, teamMap);
    }

    public KDLiveTeamMonitoring(CQRoot cqRoot, String gid, List<String> teamList) {
        Map<Integer, String> map = new HashMap<>();

        teamList.forEach(i -> map.put(KD_DATA.getMemberId(i), i));
        init(cqRoot, gid, teamMap);
    }

    private void init(CQRoot cqRoot, String gid, Map<Integer, String> teamMap) {
        this.cqRoot = cqRoot;
        this.gid = gid;
        this.teamMap = teamMap;
        Tools.print(teamMap.values().toString());
        new Thread(this).start();
    }

    /**
     * 设置监听频率，单位是毫秒
     * @param frequency 监听频率，单位是毫秒
     */
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    @Override
    public void run() {
        while (true) {
            KDLiveInfo[] kdLiveInfos = KD_LIVE.getLivingData(i -> teamMap.containsKey(i.getMemberId()));
            int count = 0;
            for (KDLiveInfo kdLiveInfo : kdLiveInfos) {
                if (!pushedSet.contains(kdLiveInfo.getMemberId())) {
                    pushedSet.add(kdLiveInfo.getMemberId());
                    sendMsg(kdLiveInfo);
                } else {
                    count++;
                }
            }
            if (count != pushedSet.size()) {
                pushedSet.clear();
                for (KDLiveInfo kdLiveInfo : kdLiveInfos) {
                    pushedSet.add(kdLiveInfo.getMemberId());
                }
            }
            Tools.sleep(frequency);
        }
    }


    private void sendMsg(KDLiveInfo kdLiveInfo) {
        String name = teamMap.get(kdLiveInfo.getMemberId());
        Tools.print(gid + "群" + name + "开直播");
        cqRoot.sendGroupMsg(gid, sendText(kdLiveInfo, name));
    }

    protected String sendText(KDLiveInfo kdLiveInfo, String memberName) {
        String temp = memberName + "开" + kdLiveInfo.getLiveType() + "了\n";
        temp += kdLiveInfo.getSubTitle() + "\n";
        temp += "直播链接:" + Tools.getTinyURL("https://h5.48.cn/2017appshare/memberLiveShare/index.html?id=" + kdLiveInfo.getLiveId());
        return temp;
    }
}

