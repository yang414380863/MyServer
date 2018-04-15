package anypick;


/**
 * Created by YanGGGGG on 2017/6/6.
 */

public class WebsiteInit {
    private static Website[] websiteList;
    private static String[] websiteFileList;
    private static int length;

    public static String[] getWebsiteFileList(){
        websiteFileList =FileUtil.getList();
        length= websiteFileList.length;
        return websiteFileList;
    }

    public static Website[] getWebsiteList(){
        getWebsiteFileList();
        websiteList =new Website[length];
        for (int i = 0; i< length; i++){
            websiteList[i]= JsonUtils.JsonToWebsite(FileUtil.readFile(websiteFileList[i]));
        }
        return websiteList;
    }
    public static String[] getWebsiteNameList(){
        getWebsiteFileList();
        String[] websiteNameList =new String[length];
        for (int i = 0; i< length; i++){
            websiteNameList[i]= JsonUtils.JsonToWebsite(FileUtil.readFile(websiteFileList[i])).getWebSiteName();
        }
        return websiteNameList;
    }
}
