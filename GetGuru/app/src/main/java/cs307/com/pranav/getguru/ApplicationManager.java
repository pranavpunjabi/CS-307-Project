package cs307.com.pranav.getguru;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DiGiT_WiZARD on 3/27/15.
 */
public final class ApplicationManager {
    public static String URL;
    public static Context context;
    public static User user;

    public static Map<String, String> routes;
    public static ArrayList<String> subjects;
    public static Map<String, String> userPrefrences;

    public static void initApplication() {
        routes = new HashMap<String, String>();
        userPrefrences = new HashMap<String, String>();
        subjects = new ArrayList<String>();

        routes.put("mainActivity", "/server/index");
        routes.put("Edit", "/server/addSubjects"); //
        routes.put("Search", "/server/search");
        routes.put("Toggle", "/server/tutor");
        routes.put("TutorInfo", "/server/getTutor"); //GET
        routes.put("StudentInfo", "/server/getStudent"); //GET
        routes.put("Rate", "");
        routes.put("Favorite", "");

        userPrefrences.put("searchRadius", "10");
        userPrefrences.put("searchSubject", "Mathematics");
        userPrefrences.put("searchRating", "1");

        subjects.add("Mathematics");
        subjects.add("Physics");
        subjects.add("Chemistry");
        subjects.add("Biology");
        subjects.add("Computer Science");
        subjects.add("Business");
    }

    public static void resetApplication() {
        URL = "";
        context = null;
        user = null;
    }
}
