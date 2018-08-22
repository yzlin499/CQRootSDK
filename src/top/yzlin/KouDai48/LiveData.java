package top.yzlin.KouDai48;

import top.yzlin.tools.Tools;

import java.util.HashMap;


/**
 * 这个方法是用来获取口袋直播的数据，为了能同时检测多个成员，又防止数据重复获取引起的效率低下的类
 * 同时会加载所有成员的直播房间号
 * 作为优化类，封了
 * 第一次使用之后,线程就会一直开启
 *
 * @author 49968
 */
public class LiveData implements Runnable {
    private static String data = null;
    private static boolean openLive = true;
    private static boolean openMember = true;
    private static long frequency = 45000;
    private static HashMap<String, String> memberHashMap = new HashMap<>();


    private LiveData() {
        Tools.print("开始监控数据");
    }

    public void run() {
        while (true) {
            data = Tools.sendPost(
                    "https://plive.48.cn/livesystem/api/live/v1/memberLivePage",
                    "{\"lastTime\":\"0\",\"groupId\":\"0\",\"type\":\"0\",\"memberId\":\"63\",\"giftUpdTime\":\"1498211389003\",\"limit\":\"1\"}",
                    conn -> {
                        conn.setRequestProperty("version", "5.0.1");
                        conn.setRequestProperty("os", "android");
                        conn.setRequestProperty("Content-Type", "application/json");
                    },
                    Throwable::printStackTrace
            );
            Tools.sleep(frequency);
        }
    }

    static String getData() {
        if (openLive) {
            openLive = false;
            new Thread(new LiveData()).start();
        }
        while (data == null) {
            Tools.sleep(100);
        }
        return data;
    }

    /**
     * 设置监听频率，单位是毫秒
     *
     * @param frequency 监听频率，单位是毫秒
     */
    public static void setFrequency(long frequency) {
        LiveData.frequency = frequency;
    }

    /**
     * 获取成员直播ID
     *
     * @param memberName 成员名字
     * @return 成员直播房间ID
     */
    public static String getLiveID(String memberName) {
        if (openMember) {
            openMember = false;
            loadData();
        }
        return memberHashMap.get(memberName);
    }

    // <editor-fold desc="加载成员LiveID表">
    private static void loadData() {
        memberHashMap.put("张语格", "1");
        memberHashMap.put("赵嘉敏", "2");
        memberHashMap.put("陈观慧", "3");
        memberHashMap.put("陈佳莹", "4");
        memberHashMap.put("袁雨桢", "5");
        memberHashMap.put("董艳芸", "6");
        memberHashMap.put("冯薪朵", "7");
        memberHashMap.put("孙芮", "8");
        memberHashMap.put("沈之琳", "9");
        memberHashMap.put("何晓玉", "10");
        memberHashMap.put("温晶婕", "11");
        memberHashMap.put("唐安琪", "12");
        memberHashMap.put("罗兰", "13");
        memberHashMap.put("徐子轩", "14");
        memberHashMap.put("孟玥", "15");
        memberHashMap.put("李艺彤", "16");
        memberHashMap.put("蒋芸", "17");
        memberHashMap.put("徐晨辰", "18");
        memberHashMap.put("孔肖吟", "19");
        memberHashMap.put("李宇琪", "20");
        memberHashMap.put("许佳琪", "21");
        memberHashMap.put("黄婷婷", "22");
        memberHashMap.put("林思意", "24");
        memberHashMap.put("万丽娜", "25");
        memberHashMap.put("龚诗淇", "26");
        memberHashMap.put("赵粤", "27");
        memberHashMap.put("李清扬", "28");
        memberHashMap.put("郝婉晴", "32");
        memberHashMap.put("易嘉爱", "33");
        memberHashMap.put("陆婷", "34");
        memberHashMap.put("莫寒", "35");
        memberHashMap.put("钱蓓婷", "36");
        memberHashMap.put("陈思", "37");
        memberHashMap.put("戴萌", "38");
        memberHashMap.put("吴哲晗", "39");
        memberHashMap.put("邱欣怡", "40");
        memberHashMap.put("徐伊人", "43");
        memberHashMap.put("谢妮", "45");
        memberHashMap.put("刘佩鑫", "46");
        memberHashMap.put("王柏硕", "47");
        memberHashMap.put("鞠婧祎", "48");
        memberHashMap.put("曾艳芬", "49");
        memberHashMap.put("袋王2", "53");
        memberHashMap.put("袋王", "63");
        memberHashMap.put("袁丹妮", "67");
        memberHashMap.put("陈问言", "68");
        memberHashMap.put("袋王 Gavin", "1338");
        memberHashMap.put("刘炅然", "1544");
        memberHashMap.put("徐晗", "2470");
        memberHashMap.put("张昕", "2508");
        memberHashMap.put("袋王4", "5526");
        memberHashMap.put("袋王5", "5527");
        memberHashMap.put("陈怡馨", "5560");
        memberHashMap.put("李豆豆", "5561");
        memberHashMap.put("林楠", "5562");
        memberHashMap.put("王璐", "5563");
        memberHashMap.put("吴燕文", "5564");
        memberHashMap.put("许杨玉琢", "5566");
        memberHashMap.put("杨惠婷", "5567");
        memberHashMap.put("张雨鑫", "5574");
        memberHashMap.put("SNH48", "5973");
        memberHashMap.put("赵晔", "6429");
        memberHashMap.put("陈琳", "6431");
        memberHashMap.put("冯晓菲", "6432");
        memberHashMap.put("李晶", "6734");
        memberHashMap.put("李钊", "6735");
        memberHashMap.put("孙静怡", "6736");
        memberHashMap.put("邵雪聪", "6737");
        memberHashMap.put("宋昕冉", "6738");
        memberHashMap.put("孙歆文", "6739");
        memberHashMap.put("汪佳翎", "6740");
        memberHashMap.put("汪束", "6741");
        memberHashMap.put("王晓佳", "6742");
        memberHashMap.put("谢天依", "6743");
        memberHashMap.put("杨冰怡", "6744");
        memberHashMap.put("闫明筠", "6745");
        memberHashMap.put("杨韫玉", "6746");
        memberHashMap.put("张丹三", "6747");
        memberHashMap.put("张韵雯", "6749");
        memberHashMap.put("影视君", "9073");
        memberHashMap.put("刘力玮", "48995");
        memberHashMap.put("申月姣", "48997");
        memberHashMap.put("徐佳丽", "48998");
        memberHashMap.put("刘诗蕾", "49000");
        memberHashMap.put("周怡", "49003");
        memberHashMap.put("沈梦瑶", "49005");
        memberHashMap.put("王露皎", "49006");
        memberHashMap.put("袁航", "49007");
        memberHashMap.put("陈珂", "63548");
        memberHashMap.put("陈美君", "63549");
        memberHashMap.put("陈音", "63550");
        memberHashMap.put("陈韫凌", "63551");
        memberHashMap.put("陈雨琪", "63552");
        memberHashMap.put("杜雨微", "63553");
        memberHashMap.put("段艺璇", "63554");
        memberHashMap.put("费沁源", "63555");
        memberHashMap.put("冯雪莹", "63556");
        memberHashMap.put("高源婧", "63557");
        memberHashMap.put("洪珮雲", "63558");
        memberHashMap.put("胡晓慧", "63559");
        memberHashMap.put("姜杉", "63560");
        memberHashMap.put("蒋舒婷", "63561");
        memberHashMap.put("林嘉佩", "63562");
        memberHashMap.put("刘梦雅", "63563");
        memberHashMap.put("李沁洁", "63564");
        memberHashMap.put("刘筱筱", "63565");
        memberHashMap.put("刘增艳", "63566");
        memberHashMap.put("潘瑛琪", "63567");
        memberHashMap.put("宋思娴", "63568");
        memberHashMap.put("时语婕", "63569");
        memberHashMap.put("宋雨珊", "63570");
        memberHashMap.put("田姝丽", "63571");
        memberHashMap.put("谢蕾蕾", "63572");
        memberHashMap.put("熊素君", "63573");
        memberHashMap.put("严佼君", "63574");
        memberHashMap.put("於佳怡", "63575");
        memberHashMap.put("阳青颖", "63576");
        memberHashMap.put("曾艾佳", "63577");
        memberHashMap.put("张菡筱", "63578");
        memberHashMap.put("邹佳佳", "63579");
        memberHashMap.put("张凯祺", "63580");
        memberHashMap.put("张文静", "63581");
        memberHashMap.put("张怡", "63582");
        memberHashMap.put("袋王活动1", "68795");
        memberHashMap.put("成珏", "286973");
        memberHashMap.put("钱艺", "286974");
        memberHashMap.put("邓艳秋菲", "286975");
        memberHashMap.put("黄彤扬", "286976");
        memberHashMap.put("孙珍妮", "286977");
        memberHashMap.put("王金铭", "286978");
        memberHashMap.put("张嘉予", "286979");
        memberHashMap.put("林忆宁", "286980");
        memberHashMap.put("吕梦莹", "286982");
        memberHashMap.put("李佳恩", "286983");
        memberHashMap.put("程文路", "286984");
        memberHashMap.put("胡怡莹", "327557");
        memberHashMap.put("罗寒月", "327558");
        memberHashMap.put("王馨悦", "327559");
        memberHashMap.put("张琼予", "327560");
        memberHashMap.put("周倩玉", "327561");
        memberHashMap.put("陈慧婧", "327562");
        memberHashMap.put("陈楠茜", "327563");
        memberHashMap.put("陈欣妤", "327564");
        memberHashMap.put("冯嘉希", "327565");
        memberHashMap.put("洪静雯", "327566");
        memberHashMap.put("刘力菲", "327567");
        memberHashMap.put("刘倩倩", "327568");
        memberHashMap.put("卢静", "327569");
        memberHashMap.put("孙馨", "327570");
        memberHashMap.put("唐莉佳", "327571");
        memberHashMap.put("冼燊楠", "327572");
        memberHashMap.put("肖文铃", "327573");
        memberHashMap.put("熊心瑶", "327574");
        memberHashMap.put("郑丹妮", "327575");
        memberHashMap.put("左嘉欣", "327576");
        memberHashMap.put("左婧媛", "327577");
        memberHashMap.put("林溪荷", "327578");
        memberHashMap.put("刘姝贤", "327579");
        memberHashMap.put("张梦慧", "327580");
        memberHashMap.put("青钰雯", "327581");
        memberHashMap.put("孙姗", "327582");
        memberHashMap.put("文妍", "327583");
        memberHashMap.put("胡博文", "327584");
        memberHashMap.put("夏越", "327585");
        memberHashMap.put("牛聪聪", "327586");
        memberHashMap.put("冯思佳", "327587");
        memberHashMap.put("陈姣荷", "327588");
        memberHashMap.put("罗雪丽", "327589");
        memberHashMap.put("毕梦媛", "327590");
        memberHashMap.put("李梓", "327591");
        memberHashMap.put("李媛媛", "327592");
        memberHashMap.put("李诗彦", "327593");
        memberHashMap.put("李想", "327594");
        memberHashMap.put("郑一凡", "327595");
        memberHashMap.put("马玉灵", "327596");
        memberHashMap.put("苏杉杉", "327597");
        memberHashMap.put("张笑盈", "327598");
        memberHashMap.put("林堃", "327599");
        memberHashMap.put("顼凘炀", "327600");
        memberHashMap.put("陈倩楠", "327601");
        memberHashMap.put("刘胜男", "327602");
        memberHashMap.put("易妍倩", "327603");
        memberHashMap.put("BEJ48", "327682");
        memberHashMap.put("GNZ48", "327683");
        memberHashMap.put("吕一", "399631");
        memberHashMap.put("潘燕琦", "399652");
        memberHashMap.put("赵韩倩", "399654");
        memberHashMap.put("徐真", "399657");
        memberHashMap.put("江真仪", "399662");
        memberHashMap.put("刘菊子", "399664");
        memberHashMap.put("张雅梦", "399665");
        memberHashMap.put("刘瀛", "399667");
        memberHashMap.put("许逸", "399668");
        memberHashMap.put("袁一琦", "399669");
        memberHashMap.put("祁静", "399672");
        memberHashMap.put("曾晓雯", "399673");
        memberHashMap.put("徐诗琪", "399674");
        memberHashMap.put("吴月黎", "407071");
        memberHashMap.put("陈逸菲", "407077");
        memberHashMap.put("陈雅钰", "407101");
        memberHashMap.put("房蕾", "407103");
        memberHashMap.put("葛司琪", "407104");
        memberHashMap.put("黄恩茹", "407106");
        memberHashMap.put("李泓瑶", "407108");
        memberHashMap.put("刘闲", "407110");
        memberHashMap.put("任心怡", "407112");
        memberHashMap.put("任玥霖", "407114");
        memberHashMap.put("单习文", "407118");
        memberHashMap.put("石羽莎", "407119");
        memberHashMap.put("孙语姗", "407121");
        memberHashMap.put("叶苗苗", "407124");
        memberHashMap.put("杨晔", "407126");
        memberHashMap.put("张怀瑾", "407127");
        memberHashMap.put("张韩紫陌", "407130");
        memberHashMap.put("李娜(一期生)", "407132");
        memberHashMap.put("杨一帆", "407135");
        memberHashMap.put("王雨煊", "407168");
        memberHashMap.put("黄黎蓉", "410175");
        memberHashMap.put("向芸", "410177");
        memberHashMap.put("戴欣佚", "410179");
        memberHashMap.put("李伊虹", "410180");
        memberHashMap.put("尼德兰的微笑", "411040");
        memberHashMap.put("陈桂君", "417311");
        memberHashMap.put("陈梓荧", "417315");
        memberHashMap.put("代玲", "417316");
        memberHashMap.put("杜秋霖", "417317");
        memberHashMap.put("刘嘉怡", "417318");
        memberHashMap.put("龙亦瑞", "417320");
        memberHashMap.put("农燕萍", "417321");
        memberHashMap.put("王翠菲", "417324");
        memberHashMap.put("王炯义", "417325");
        memberHashMap.put("王偲越", "417326");
        memberHashMap.put("王盈", "417328");
        memberHashMap.put("王秭歆", "417329");
        memberHashMap.put("杨可璐", "417330");
        memberHashMap.put("杨媛媛", "417331");
        memberHashMap.put("于珊珊", "417332");
        memberHashMap.put("张心雨", "417333");
        memberHashMap.put("赵欣雨", "417335");
        memberHashMap.put("赵翊民", "417336");
        memberHashMap.put("姚祎纯", "419966");
        memberHashMap.put("悠游甜心A", "443031");
        memberHashMap.put("悠游甜心B", "443032");
        memberHashMap.put("许婉玉", "444081");
        memberHashMap.put("李慧", "458335");
        memberHashMap.put("南翎璞", "458358");
        memberHashMap.put("徐静妍", "459988");
        memberHashMap.put("王诗蒙", "459989");
        memberHashMap.put("卢天惠", "459991");
        memberHashMap.put("刘娜", "459992");
        memberHashMap.put("刘娇", "459993");
        memberHashMap.put("秦玺", "459994");
        memberHashMap.put("赖梓惜", "459995");
        memberHashMap.put("关思雨", "459996");
        memberHashMap.put("朱燕", "459997");
        memberHashMap.put("韩家乐", "459999");
        memberHashMap.put("陈婧文", "460000");
        memberHashMap.put("付紫琪", "460002");
        memberHashMap.put("冯译莹", "460003");
        memberHashMap.put("杨允涵", "460004");
        memberHashMap.put("赵佳蕊", "460005");
        memberHashMap.put("孙敏", "460007");
        memberHashMap.put("SHY48", "460933");
        memberHashMap.put("董思佳", "480656");
        memberHashMap.put("高志娴", "480665");
        memberHashMap.put("寇承希", "480666");
        memberHashMap.put("刘静晗", "480667");
        memberHashMap.put("李晴", "480668");
        memberHashMap.put("李熙凝", "480670");
        memberHashMap.put("曲悦萌", "480671");
        memberHashMap.put("任雨情", "480672");
        memberHashMap.put("徐斐然", "480673");
        memberHashMap.put("杨肖", "480674");
        memberHashMap.put("张爱静", "480675");
        memberHashMap.put("郑洁丽", "480676");
        memberHashMap.put("张儒轶", "480678");
        memberHashMap.put("张云梦", "480679");
        memberHashMap.put("张幼柠", "480680");
        memberHashMap.put("澳洲行活动一", "485376");
        memberHashMap.put("澳洲行活动二", "485380");
        memberHashMap.put("澳洲行活动三", "485381");
        memberHashMap.put("7SENSES", "490333");
        memberHashMap.put("GNZ48-星梦剧院", "524597");
        memberHashMap.put("SNH48-星梦剧院", "526172");
        memberHashMap.put("胡丽芝", "528094");
        memberHashMap.put("刘崇恬", "528101");
        memberHashMap.put("李沐遥", "528106");
        memberHashMap.put("毛其羽", "528118");
        memberHashMap.put("孙晓艳", "528329");
        memberHashMap.put("郑依灵", "528330");
        memberHashMap.put("黄子璇", "528331");
        memberHashMap.put("李烨", "528332");
        memberHashMap.put("徐静", "528333");
        memberHashMap.put("赵笛儿", "528334");
        memberHashMap.put("金锣赛", "528335");
        memberHashMap.put("兰昊", "528336");
        memberHashMap.put("刘一菲", "528337");
        memberHashMap.put("乔钰珂", "528339");
        memberHashMap.put("郑心雨", "528340");
        memberHashMap.put("48WAN游戏平台", "529287");
        memberHashMap.put("陈奕君", "529991");
        memberHashMap.put("菅瑞静", "530378");
        memberHashMap.put("叶锦童", "530380");
        memberHashMap.put("臧聪", "530381");
        memberHashMap.put("郑诗琪", "530383");
        memberHashMap.put("方诗涵", "530384");
        memberHashMap.put("高崇", "530385");
        memberHashMap.put("龚梦婷", "530386");
        memberHashMap.put("逯芳竹", "530387");
        memberHashMap.put("王菲妍", "530388");
        memberHashMap.put("王睿琦", "530390");
        memberHashMap.put("张羽涵", "530392");
        memberHashMap.put("陈佳莹(GNZ48)", "530431");
        memberHashMap.put("陈俊宏", "530433");
        memberHashMap.put("陈乐添", "530434");
        memberHashMap.put("程一心", "530435");
        memberHashMap.put("方晓瑜", "530436");
        memberHashMap.put("高雪逸", "530439");
        memberHashMap.put("赖俊亦", "530440");
        memberHashMap.put("梁可", "530443");
        memberHashMap.put("刘小末", "530444");
        memberHashMap.put("唐诗怡", "530446");
        memberHashMap.put("谢艾琳", "530447");
        memberHashMap.put("张秋怡", "530450");
        memberHashMap.put("郑悦", "530451");
        memberHashMap.put("朱怡欣", "530452");
        memberHashMap.put("郭倩芸", "530584");
        memberHashMap.put("陶波尔", "533852");
        memberHashMap.put("文文", "534729");
        memberHashMap.put("沈小爱", "538697");
        memberHashMap.put("金莹玥", "538735");
        memberHashMap.put("赵梦婷", "540106");
        memberHashMap.put("林歆源", "541132");
        memberHashMap.put("CKG48", "565225");
        memberHashMap.put("贺苏堃", "592320");
        memberHashMap.put("许嘉怡", "592348");
        memberHashMap.put("孙亚萍", "593820");
        memberHashMap.put("葛佳慧", "593999");
        memberHashMap.put("姜涵", "594002");
        memberHashMap.put("王奕", "594003");
        memberHashMap.put("熊沁娴", "594005");
        memberHashMap.put("梁婉琳", "601302");
        memberHashMap.put("马凡", "606343");
        memberHashMap.put("毕瑞珊", "607507");
        memberHashMap.put("程子钰", "607510");
        memberHashMap.put("邓惠恩", "607511");
        memberHashMap.put("符冰冰", "607513");
        memberHashMap.put("黄楚茵", "607515");
        memberHashMap.put("何梦瑶", "607516");
        memberHashMap.put("罗可嘉", "607521");
        memberHashMap.put("林芝", "607523");
        memberHashMap.put("汪慕远", "607524");
        memberHashMap.put("吴羽霏", "607591");
        memberHashMap.put("叶晓梦", "607592");
        memberHashMap.put("余芷媛", "607594");
        memberHashMap.put("李彬玉", "608995");
        memberHashMap.put("司珀琳", "608997");
        memberHashMap.put("周佳怡", "609001");
        memberHashMap.put("赵天杨", "609002");
        memberHashMap.put("相望", "610040");
        memberHashMap.put("唐霖", "610042");
        memberHashMap.put("朱星宇", "613487");
        memberHashMap.put("柏欣妤", "614528");
        memberHashMap.put("李恩锐", "614725");
        memberHashMap.put("李姗姗", "614727");
        memberHashMap.put("雷宇霄", "614728");
        memberHashMap.put("李泽亚", "614729");
        memberHashMap.put("孟玥(new)", "614730");
        memberHashMap.put("毛译晗", "614731");
        memberHashMap.put("谯玉珍", "614733");
        memberHashMap.put("冉蔚", "614734");
        memberHashMap.put("田倩兰", "614735");
        memberHashMap.put("陶菀瑞", "614736");
        memberHashMap.put("伍寒琪", "614738");
        memberHashMap.put("王梦竹", "614739");
        memberHashMap.put("王娱博", "614740");
        memberHashMap.put("曾佳", "614741");
        memberHashMap.put("左欣", "614742");
        memberHashMap.put("周源", "614743");
        memberHashMap.put("艾芷亦", "614749");
        memberHashMap.put("邓倩", "614750");
        memberHashMap.put("樊曦月", "614752");
        memberHashMap.put("郝婧怡", "614753");
        memberHashMap.put("韩林芹", "614754");
        memberHashMap.put("黄琬璎", "614755");
        memberHashMap.put("林舒晴", "614756");
        memberHashMap.put("李瑜璇", "614757");
        memberHashMap.put("石勤", "614758");
        memberHashMap.put("田祯臻", "614760");
        memberHashMap.put("吴学雨", "614761");
        memberHashMap.put("夏文倩", "614762");
        memberHashMap.put("章宇阳", "614770");
        memberHashMap.put("郑阳莹", "614772");
        memberHashMap.put("赵泽慧", "614773");
        memberHashMap.put("吴晶晶", "614776");
        memberHashMap.put("金鑫", "617948");
        memberHashMap.put("程戈", "618319");
        memberHashMap.put("李娜(三期生)", "623828");
        memberHashMap.put("李宗颐", "623832");
        memberHashMap.put("何阳青青", "624121");
        memberHashMap.put("李晨曦", "624311");
        memberHashMap.put("舒湘", "624312");
        memberHashMap.put("王梦媛", "624313");
        memberHashMap.put("徐佳音", "624314");
        memberHashMap.put("肖文静", "624315");
        memberHashMap.put("张紫颖", "624318");
        memberHashMap.put("杨鑫", "652650");
        memberHashMap.put("周洁艺", "652652");
        memberHashMap.put("青春大本营", "654707");

        Tools.print("成员列表加载完成");
    }// </editor-fold>
}
