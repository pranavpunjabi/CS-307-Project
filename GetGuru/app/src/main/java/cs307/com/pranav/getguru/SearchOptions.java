package cs307.com.pranav.getguru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;


public class SearchOptions extends ActionBarActivity implements View.OnClickListener {


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


        subList.add("");
        ratList.add("");
        locList.add("");


        subAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, subList);
        ratAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, ratList);
        locAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, locList);


        subjects.setAdapter(subAdapter);

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
}
