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
    public static ArrayList<Boolean> subjectsBools;
    public static Map<String, String> userPrefrences;

    public static int searchTutorID;

    public static void initApplication() {
        routes = new HashMap<String, String>();
        userPrefrences = new HashMap<String, String>();
        subjects = new ArrayList<String>();
        subjectsBools = new ArrayList<Boolean>();

        routes.put("mainActivity", "/server/index");
        routes.put("Edit", "/server/addSubjects"); //
        routes.put("EditLoc", "/server/addtutorlocation"); //

        routes.put("Search", "/server/search");
        routes.put("Toggle", "/server/tutor");
        routes.put("TutorInfo", "/server/getTutor"); //GET
        routes.put("TutorRatings", "/server/getTutinfo"); //GET
        routes.put("StudentInfo", "/server/getStudent"); //GET
        routes.put("Rate", "/server/ratings"); //tutID, stuID, ratings, reviews, POST
        routes.put("getRatings", "/server/getRatings"); //
        routes.put("Favorite", "/server/addfavorites"); //studentID, tutorID, POST
        routes.put("getFavorite", "/server/getfavorites"); //studentID, GET

        userPrefrences.put("searchCode", "47906");
        userPrefrences.put("searchSubject", "Mathematics");
        userPrefrences.put("searchSubject2", "None");
        userPrefrences.put("searchSubject3", "None");
        userPrefrences.put("searchRating", "0");

        subjects.add("Mathematics");
        subjects.add("English");
        subjects.add("Physics");
        subjects.add("Chemistry");
        subjects.add("Biology");
        subjects.add("Business");
        subjects.add("Computer Science");
        subjects.add("Economics");

        subjectsBools.add(true);
        subjectsBools.add(false);
        subjectsBools.add(false);
        subjectsBools.add(false);
        subjectsBools.add(false);
        subjectsBools.add(false);
        subjectsBools.add(false);
        subjectsBools.add(false);

        searchTutorID = 0;
    }

    public static void resetApplication() {
        URL = "";
        context = null;
        user = null;
    }
}
