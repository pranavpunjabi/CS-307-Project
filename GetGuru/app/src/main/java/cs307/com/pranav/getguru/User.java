package cs307.com.pranav.getguru;

import java.util.ArrayList;

/**
 * Created by DiGiT_WiZARD on 3/31/15.
 */
public class User {

    String firstName;
    String lastName;
    String email;
    int ID;

    boolean isTutor = false;
    ArrayList<String> Subjects, Favorites;


    public User(String fn, String ln, String em, int id) {
        this.firstName = fn;
        this.lastName = ln;
        this.email = em;
        this.ID = id;
    }

    public void populateUser() {

    }
}