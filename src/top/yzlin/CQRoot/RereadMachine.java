package top.yzlin.CQRoot;

import top.yzlin.CQRoot.cqinfo.GroupMsgInfo;
import top.yzlin.CQRoot.msginterface.GroupMsgSolution;

import java.util.Objects;

public class RereadMachine {
    private int count = 1;
    private int threshold = 4;
    private String lastRecord = "";
    private CQRoot cqRoot;
    private String gid;

    public RereadMachine(CQRoot cqRoot, String gid) {
        this.gid = gid;
        this.cqRoot = cqRoot;
        cqRoot.addMsgSolution((GroupMsgSolution) this::reread);
    }

    private void reread(GroupMsgInfo Msg) {
        if (Objects.equals(gid, Msg.getFromGroup())) {
            String newMsg = Msg.getMsg();
            if (Objects.equals(lastRecord, newMsg)) {
                count++;
                if (count == threshold) {
                    cqRoot.sendGroupMsg(gid, lastRecord);
                    count++;
                }
            } else {
                lastRecord = newMsg;
                count = 1;
            }
        }
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
