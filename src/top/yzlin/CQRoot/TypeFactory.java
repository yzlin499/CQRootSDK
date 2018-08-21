package top.yzlin.CQRoot;

import top.yzlin.CQRoot.cqinfo.*;
import top.yzlin.CQRoot.msginterface.*;

import java.util.HashMap;

public class TypeFactory {
    private final static TypeFactory instance = new TypeFactory();

    private TypeFactory() {
        init();
    }

    public static TypeFactory getInstance() {
        return instance;
    }

    private HashMap<Integer, Class<? extends AbstractInfo>> beanMap = new HashMap<>();
    private HashMap<Class<? extends EventSolution>, Integer> eventMap = new HashMap<>();

    private void init() {
        beanMap.put(CQRoot.GET_GROUP_MSG, GroupMsgInfo.class);
        beanMap.put(CQRoot.GET_PERSON_MSG, PersonMsgInfo.class);
        beanMap.put(CQRoot.GET_DISCUSS_MSG, DiscussMsgInfo.class);
        beanMap.put(CQRoot.GET_GROUP_MEMBER_INCREASE, GroupMemberIncreaseEventInfo.class);
        beanMap.put(CQRoot.GET_GROUP_MEMBER_DECREASE, GroupMemberDecreaseEventInfo.class);
        beanMap.put(CQRoot.GET_GROUP_ADMIN_CHANGE, GroupAdminChangeEventInfo.class);
        beanMap.put(CQRoot.GET_GROUP_REQUEST, GroupMemberRequestEventInfo.class);
        beanMap.put(CQRoot.GET_FRIEND_INCREASE, FriendIncreaseEventInfo.class);
        beanMap.put(CQRoot.GET_FRIEND_REQUEST, FriendRequestEventInfo.class);

        eventMap.put(GroupMsgSolution.class, CQRoot.GET_GROUP_MSG);
        eventMap.put(PersonMsgSolution.class, CQRoot.GET_PERSON_MSG);
        eventMap.put(DiscussMsgSolution.class, CQRoot.GET_DISCUSS_MSG);
        eventMap.put(GroupMemberIncreaseSolution.class, CQRoot.GET_GROUP_MEMBER_INCREASE);
        eventMap.put(GroupMemberDecreaseSolution.class, CQRoot.GET_GROUP_MEMBER_DECREASE);
        eventMap.put(GroupAdminChangeSolution.class, CQRoot.GET_GROUP_ADMIN_CHANGE);
        eventMap.put(GroupMemberRequestSolution.class, CQRoot.GET_GROUP_REQUEST);
        eventMap.put(FriendIncreaseSolution.class, CQRoot.GET_FRIEND_INCREASE);
        eventMap.put(FriendRequestSolution.class, CQRoot.GET_FRIEND_REQUEST);

    }

    public Class<? extends AbstractInfo> getInfoClass(int act) {
        return beanMap.getOrDefault(act, AbstractInfo.class);
    }

    public Integer getEventClass(Class<?> act) {
        return eventMap.get(act);
    }
}
