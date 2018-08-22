package top.yzlin.KouDai48;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

public enum KDRoomType {
    TEXT("text", "消息"),
    FAIPAI_TEXT("faipaiText", "翻牌"),
    LIVE("live", "直播"),
    DIANTAI("diantai", "电台"),
    IMAGE("image", "图片"),
    AUDIO("audio", "音频"),
    IDOLFLIP("idolFlip", "付费翻牌");

    private String name;
    private String cnName;
    private static Map<String, KDRoomType> nameMap =
            EnumSet.allOf(KDRoomType.class).stream().collect(Collectors.toMap(k -> k.name, v -> v));

    KDRoomType(String name, String cnName) {
        this.name = name;
        this.cnName = cnName;
    }

    public static KDRoomType parse(String type) {
        return nameMap.get(type);
    }

    public String getName() {
        return name;
    }

    public String getCnName() {
        return cnName;
    }

    @Override
    public String toString() {
        return name;
    }

}
