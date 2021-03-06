package anypick;

import com.google.gson.Gson;


/**
 * Created by YanGGGGG on 2017/4/26.
 */

public class JsonUtils {
    public static String ObjectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static Website JsonToWebsite(String string){
        Gson gson = new Gson();
        return gson.fromJson(string.trim(),Website.class);
    }
}
