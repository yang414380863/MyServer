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
        websites=WebsiteInit.getWebsiteList();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                websiteNow=websites[websiteCount];
                try{
                    if (websiteNow.getCategory()==null){
                        if (categoryCount<0){
                            nextWebsite();
                        }else {
                            categoryCount=-10;
                        }
                    }else {
                        if (categoryCount==websiteNow.getCategory().length/2){
                            nextWebsite();
                            websiteNow.setIndexUrl(websiteNow.getCategory()[categoryCount*2+1]);
                        }else {
                            websiteNow.setIndexUrl(websiteNow.getCategory()[categoryCount*2+1]);
                        }
                    }

                    String url=websiteNow.getIndexUrl();
                    System.out.println("website No: "+websiteCount);
                    System.out.println("category No: "+categoryCount);
                    System.out.println("Request url "+url);
                    categoryCount++;
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("onFailure");
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
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
                    System.out.println("Error");
                    EventBus.getDefault().post("nextWebsite");
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

    private void nextWebsite(){
        websiteCount++;
        if (websiteCount==websites.length){
            websiteCount=0;
        }
        websiteNow=websites[websiteCount];
        categoryCount=0;
    }
}
