package cs307.com.pranav.getguru;

import android.content.Context;

import java.util.Dictionary;

/**
 * Created by DiGiT_WiZARD on 3/27/15.
 */
public final class ApplicationManager {
    public static String URL;
    public static Context context;
    public static User user;

    public static Dictionary<String, String> routes;
    public static Dictionary<String, String> subjects;
    public static Dictionary<String, String> userPrefrences;

    public static void initApplication() {
        routes.put("mainActivity", "");
        routes.put("Edit", "");
        routes.put("Search", "");
        routes.put("Rate", "");

        userPrefrences.put("searchRadius", "10");
        userPrefrences.put("searchSubject", "Mathematics");
        userPrefrences.put("searchRating", "1");


    }

    public static void resetApplication() {
        URL = "";
        context = null;
        user = null;
    }
}
