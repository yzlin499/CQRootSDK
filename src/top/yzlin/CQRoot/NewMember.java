package top.yzlin.CQRoot;

import java.util.HashMap;
import java.util.HashSet;

import static java.lang.String.valueOf;

public class NewMember {
    private CQRoot mt;
    private String GID;
    private HashSet<String> adminSet=new HashSet<>();



    public NewMember(CQRoot mt,String GID){
        this.mt=mt;
        this.GID=GID;
        mt.addMsgSolution(this::changeNewMemberWord);
    }

    public void addAdmin(String admin) {
        adminSet.add(admin);
    }

    private void changeNewMemberWord(HashMap<String,String> map){
        String fromQQ=map.get("fromQQ");
        if(valueOf(CQRoot.GET_PERSON_MSG).equals(map.get("act")) && adminSet.contains(fromQQ)){
            String msg=map.get("msg");
            if(msg.indexOf("设置群新成员消息:")==0||msg.indexOf("设置群新成员消息：")==0){
                mt.groupMemberIncrease(GID,msg.substring(9));
            }
        }
    }

}
