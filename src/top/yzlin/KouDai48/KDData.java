package top.yzlin.KouDai48;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.yzlin.tools.Tools;

import java.util.*;

public class KDData {
    private static final KDData INSTANCE = new KDData();

    public static KDData getInstance() {
        return INSTANCE;
    }

    private Element rootElement;

    private KDData() {
        try {
            rootElement = new SAXReader().read(Tools.getResources("top/yzlin/KouDai48/member-list.xml")).getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public int getMemberId(String name) {
        return getMemberId(null, name);
    }

    public int getMemberId(String team, String name) {
        Element e = Optional.ofNullable(team).map(this::getTeamElementByName).orElse(rootElement);
        List<Element> elements = e.elements("member");
        for (Element element : elements) {
            if (Objects.equals(name, element.attributeValue("name"))) {
                return Integer.parseInt(element.attributeValue("ID"));
            }
        }
        return -1;
    }

    public String getMemberName(int id) {
        return getMemberName(null, id);
    }

    public String getMemberName(String team, int id) {
        Element e = Optional.ofNullable(team)
                .map(this::getTeamElementByName)
                .orElse(rootElement)
                .elementByID(String.valueOf(id));
        return Optional.ofNullable(e).map(e1 -> e1.attributeValue("name")).orElse("");
    }

    private Element getTeamElementByName(String name) {
        for (Element team : rootElement.elements("team")) {
            if (Objects.equals(team.attributeValue("name"), name)) {
                return team;
            }
        }
        return rootElement;
    }

    public Map<String, Integer> getTeamNameMap(String team) {
        for (Element eTeam : rootElement.elements("team")) {
            if (Objects.equals(eTeam.attributeValue("name"), team)) {
                HashMap<String, Integer> map = new HashMap<>();
                for (Element member : eTeam.elements("member")) {
                    map.put(member.attributeValue("name"), Integer.valueOf(member.attributeValue("ID")));
                }
                return map;
            }
        }
        return Collections.emptyMap();
    }

    public Map<Integer, String> getTeamIDMap(String team) {
        for (Element eTeam : rootElement.elements("team")) {
            if (Objects.equals(eTeam.attributeValue("name"), team)) {
                HashMap<Integer, String> map = new HashMap<>();
                for (Element member : eTeam.elements("member")) {
                    map.put(Integer.valueOf(member.attributeValue("ID")), member.attributeValue("name"));
                }
                return map;
            }
        }
        return Collections.emptyMap();
    }

}
