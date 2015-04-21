package cs307.com.pranav.getguru;

import java.util.ArrayList;

/**
 * Created by DiGiT_WiZARD on 3/31/15.
 */
public class User {

    //Primary data
    String firstName;
    String lastName;
    String email;
    String password;
    int ID;

    //Secondary data
    String major;
    String classification;

    boolean isTutor = false;
    ArrayList<String> Subjects, Favorites;


    public User(String fn, String ln, String em, int id, String pw) {
        this.firstName = fn;
        this.lastName = ln;
        this.email = em;
        this.ID = id;
        this.password = pw;
    }

    public void populateUser() {

    }
}
