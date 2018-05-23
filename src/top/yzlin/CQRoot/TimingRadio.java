package top.yzlin.CQRoot;

import top.yzlin.tools.Tools;

public class TimingRadio extends Thread{
    private long sleepTime=100000;
    private CQRoot cqRoot;
    private String GID;
    private String radioText;

    public TimingRadio(CQRoot cqRoot,String GID){
        this.cqRoot=cqRoot;
        this.GID=GID;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setRadioText(String radioText){
        this.radioText=radioText;
    }

    @Override
    public void run() {
        if(radioText!=null){
            while(true){
                cqRoot.sendGroupMsg(GID,radioText);
                Tools.sleep(sleepTime);
            }
        }
    }
}
