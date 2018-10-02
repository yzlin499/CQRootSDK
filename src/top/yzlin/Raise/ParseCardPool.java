package top.yzlin.Raise;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import top.yzlin.tools.Tools;

import java.io.File;
import java.util.List;

public class ParseCardPool {
    private static final ParseCardPool INSTANCE = new ParseCardPool();

    private ParseCardPool() {
    }

    public static ParseCardPool getInstance() {
        return INSTANCE;
    }

    private SAXReader reader = new SAXReader();

    public RandomRaise[] parseRandomForXml(File file) {
        try {
            Element rootElement = reader.read(file).getRootElement();
            List<Element> elementList = rootElement.elements("cardPool");
            RandomRaise[] randomRaises = new RandomRaise[elementList.size()];
            for (int i = 0; i < randomRaises.length; i++) {
                randomRaises[i] = new RandomRaise();
                Element cardPool = elementList.get(i);
                randomRaises[i].setMinLimit(Double.parseDouble(cardPool.attributeValue("money")));
                randomRaises[i].addRafflePrize(cardPool.elements("card").stream()
                        .map(item -> {
                            RafflePrize prize = new RafflePrize(item.attributeValue("name"),
                                    Integer.parseInt(item.attributeValue("probability")));
                            prize.setPicturePath(item.attributeValue("picturePath"));
                            return prize;
                        }).toArray(RafflePrize[]::new));
            }
            return randomRaises;
        } catch (DocumentException e) {
            e.printStackTrace();
            Tools.print("崩了啊");
            return new RandomRaise[0];
        }
    }
}
