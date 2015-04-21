package cs307.com.pranav.getguru;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;


public class SearchOptions extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    MaterialEditText pin;
    NumberPicker ratingPicker;

    CheckBox s1, s2, s3, s4, s5, s6, s7, s8;

    ButtonRectangle save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_options);

        save = (ButtonRectangle) findViewById(R.id.button2Save);
        ratingPicker = (NumberPicker) findViewById(R.id.numberPickerRating);
        pin = (MaterialEditText) findViewById(R.id.editTextZIPCODE);

        s1 = (CheckBox) findViewById(R.id.checkBoxmath);
        s2 = (CheckBox) findViewById(R.id.checkBoxEnglish);
        s3 = (CheckBox) findViewById(R.id.checkBoxPhy);
        s4 = (CheckBox) findViewById(R.id.checkBoxChem);
        s5 = (CheckBox) findViewById(R.id.checkBoxBio);
        s6 = (CheckBox) findViewById(R.id.checkBoxBuss);
        s7 = (CheckBox) findViewById(R.id.checkBoxCS);
        s8 = (CheckBox) findViewById(R.id.checkBoxEcon);

        s1.setChecked(ApplicationManager.subjectsBools.get(0));
        s2.setChecked(ApplicationManager.subjectsBools.get(1));
        s3.setChecked(ApplicationManager.subjectsBools.get(2));
        s4.setChecked(ApplicationManager.subjectsBools.get(3));
        s5.setChecked(ApplicationManager.subjectsBools.get(4));
        s6.setChecked(ApplicationManager.subjectsBools.get(5));
        s7.setChecked(ApplicationManager.subjectsBools.get(6));
        s8.setChecked(ApplicationManager.subjectsBools.get(7));

        ratingPicker.setMinValue(0);
        ratingPicker.setMaxValue(5);
        ratingPicker.setOnClickListener(this);

        save.setOnClickListener(this);
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
            if (pin.getText().toString().equals("")) {

            }
            else {
                ApplicationManager.userPrefrences.put("searchCode", pin.getText().toString());
            }

            ApplicationManager.userPrefrences.put("searchRating", Integer.toString(ratingPicker.getValue()));

            ApplicationManager.subjectsBools.set(0, s1.isChecked());
            ApplicationManager.subjectsBools.set(1, s2.isChecked());
            ApplicationManager.subjectsBools.set(2, s3.isChecked());
            ApplicationManager.subjectsBools.set(3, s4.isChecked());
            ApplicationManager.subjectsBools.set(4, s5.isChecked());
            ApplicationManager.subjectsBools.set(5, s6.isChecked());
            ApplicationManager.subjectsBools.set(6, s7.isChecked());
            ApplicationManager.subjectsBools.set(7, s8.isChecked());

            Toast.makeText(getApplicationContext(), "Your search preferences have been saved",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Parent:", parent.toString());
        Log.d("View:", view.toString());
        Log.d("Position:", Integer.toString(position));
        Log.d("ID:", Long.toString(id));


        Log.d("Changes", ApplicationManager.userPrefrences.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
