package anypick;


/**
 * Created by YanGGGGG on 2017/6/6.
 */

public class WebsiteInit {
    private static Website[] websiteList;
    private static String[] websiteNameList;
    private static int length;

    public static String[] getWebsiteNameList(){
        websiteNameList =FileUtil.getList();
        length=websiteNameList.length;
        return websiteNameList;
    }

    public static Website[] getWebsiteList(){
        getWebsiteNameList();
        websiteList =new Website[length];
        for (int i = 0; i< length; i++){
            websiteList[i]= JsonUtils.JsonToWebsite(FileUtil.readFile(websiteNameList[i]));
        }
        return websiteList;

    }
}
