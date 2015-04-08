package cs307.com.pranav.getguru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;


public class SearchOptions extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    Spinner subjects, ratings, loc;
    ArrayAdapter subAdapter, ratAdapter, locAdapter;
    ArrayList<String> subList, ratList, locList;

    Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_options);

        save = (Button) findViewById(R.id.button2Save);
        cancel = (Button) findViewById(R.id.buttonCancel);

        subjects = (Spinner) findViewById(R.id.spinner2Sub);
        ratings = (Spinner) findViewById(R.id.spinner3Rat);
        loc = (Spinner) findViewById(R.id.spinnerLoc);


        subList = new ArrayList<String>();
        ratList = new ArrayList<String>();
        locList = new ArrayList<String>();

        for (int i = 0; i < ApplicationManager.subjects.size(); i++) {
            subList.add(ApplicationManager.subjects.get(i));
        }
        ratList.add("None");
        ratList.add("1 and above");
        ratList.add("2 and above");
        ratList.add("3 and above");
        ratList.add("4 and above");
        ratList.add("5 and above");
        locList.add("10");
        locList.add("20");
        locList.add("30");
        locList.add("40");


        subAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, subList);
        ratAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, ratList);
        locAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, locList);


        subjects.setAdapter(subAdapter);
        ratings.setAdapter(ratAdapter);
        loc.setAdapter(locAdapter);

        subjects.setOnItemSelectedListener(this);
        ratings.setOnItemSelectedListener(this);
        loc.setOnItemSelectedListener(this);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2Save) {

        }
        if (v.getId() == R.id.buttonCancel) {
            Intent i = new Intent(this, StudentTabHost.class);
            startActivity(i);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Parent:", parent.toString());
        Log.d("View:", view.toString());
        Log.d("Position:", Integer.toString(position));
        Log.d("ID:", Long.toString(id));


        if (parent.getId() == R.id.spinner2Sub) {
            ApplicationManager.userPrefrences.put("searchSubject", ApplicationManager.subjects.get(position));
        }
        if (parent.getId() == R.id.spinner3Rat) {
            switch (position) {
                case 0:
                    ApplicationManager.userPrefrences.put("searchRating", "0");
                    break;
                case 1:
                    ApplicationManager.userPrefrences.put("searchRating", "1");
                    break;
                case 2:
                    ApplicationManager.userPrefrences.put("searchRating", "2");
                    break;
                case 3:
                    ApplicationManager.userPrefrences.put("searchRating", "3");
                    break;
                case 4:
                    ApplicationManager.userPrefrences.put("searchRating", "4");
                    break;
                case 5:
                    ApplicationManager.userPrefrences.put("searchRating", "5");
                    break;
            }
        }
        if (parent.getId() == R.id.spinnerLoc) {
            switch (position) {
                case 0:
                    ApplicationManager.userPrefrences.put("searchRadius", "10");
                    break;
                case 1:
                    ApplicationManager.userPrefrences.put("searchRadius", "20");
                    break;
                case 2:
                    ApplicationManager.userPrefrences.put("searchRadius", "30");
                    break;
                case 3:
                    ApplicationManager.userPrefrences.put("searchRadius", "40");
                    break;
            }
        }
        Log.d("Changes", ApplicationManager.userPrefrences.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
