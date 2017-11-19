package anypick;

import anypick.html.SelectorAndRegex;
import anypick.json.JsonRuleConnector;
import okhttp3.*;
import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import server.ServiceAnypick;
import sql.SqlAnyPick;

import java.io.IOException;
import java.util.Date;
import java.util.List;

//单例模式
public class Browser {

    private Website websiteNow;
    private volatile static Browser instance;
    private int websiteCount=0;
    private int categoryCount=0;
    private Website[] websites;
    SqlAnyPick sqlAnyPick;
    //将默认的构造函数私有化，防止其他类手动new
    private Browser(){
        WebsiteInit.init();
        websites=WebsiteInit.websitesInit;
        sqlAnyPick=new SqlAnyPick();
    }

    public static Browser getInstance(){
        if(instance==null){
            synchronized (Browser.class){
                if(instance==null)
                    instance=new Browser();
            }
        }
        return instance;
    }

    public Website getWebsiteNow() {
        return websiteNow;
    }

    public void sendRequest(){
        websiteNow=websites[websiteCount];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (websiteNow.getCategory()==null){
                        if (categoryCount<0){
                            ServiceAnypick.isReady=true;
                            websiteCount++;
                            categoryCount=0;
                        }else {
                            categoryCount=-5;
                        }
                    }else {
                        if (categoryCount==websiteNow.getCategory().length/2){
                            ServiceAnypick.isReady=true;
                            websiteCount++;
                            categoryCount=0;
                        }else {
                            websiteNow.setIndexUrl(websiteNow.getCategory()[categoryCount*2+1]);
                        }
                    }
                    if (websiteCount==websites.length){
                        websiteCount=0;
                    }
                    String url=websiteNow.getIndexUrl();
                    OkHttpClient client = new OkHttpClient();
                    //System.out.println("Request url "+url);
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            categoryCount++;
                            System.out.println("onFailure");
                            e.printStackTrace();
                            EventBus.getDefault().post("nextWebsite");
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            categoryCount++;
                            if (!websiteNow.isJsonIndex()){
                                //解析HTML
                                Document doc= Jsoup.parse(response.body().string());
                                analysis(doc);
                            }else {
                                //解析JSON
                                String s=response.body().string();
                                //JSONObject jsonObject=JSON.parseObject(response.body().string());
                                analysisJSON(s);
                            }

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void analysis(Document doc){
        Elements list = doc.select(websiteNow.getItemSelector());
        int sizeThisPage=list.size();

        if (sizeThisPage==0){
            return;
        }
        String indexurl=websiteNow.getIndexUrl();
        String link=SelectorAndRegex.getItemData(doc,websiteNow,"Link",0);
        String latestupdate=String.valueOf(new Date().getTime());
        if (sqlAnyPick.refreshWebsite(indexurl,link,latestupdate)){
            EventBus.getDefault().post("nextWebsite");
        }
    }

    private void analysisJSON(String jsonData){
        List<Object> links = JsonRuleConnector.getCompleteLinks(jsonData);
        if (links.size()==0){
            return;
        }
        String indexurl=websiteNow.getIndexUrl();
        String link=links.get(0).toString();
        String latestupdate=String.valueOf(new Date().getTime());
            if (sqlAnyPick.refreshWebsite(indexurl,link,latestupdate)){
                EventBus.getDefault().post("nextWebsite");
        }
    }
}
