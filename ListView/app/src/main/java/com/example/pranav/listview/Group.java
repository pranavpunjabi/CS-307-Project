package com.example.pranav.listview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav on 4/10/2015.
 */
public class Group {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public Group(String string) {
        this.string = string;
    }

}