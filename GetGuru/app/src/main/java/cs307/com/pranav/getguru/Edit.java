package cs307.com.pranav.getguru;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class Edit extends ActionBarActivity implements View.OnClickListener{

    MaterialEditText loc;
    ButtonRectangle sendSubjects;
    String URL;

    ArrayList<Boolean> tutored;

    CheckBox s1, s2, s3, s4, s5, s6, s7, s8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        loc = (MaterialEditText) findViewById(R.id.editTexttutZIPCODE);
        sendSubjects = (ButtonRectangle) findViewById(R.id.buttonsendsubs);

        s1 = (CheckBox) findViewById(R.id.checkBoxmathtut);
        s2 = (CheckBox) findViewById(R.id.checkBoxEnglishtut);
        s3 = (CheckBox) findViewById(R.id.checkBoxPhytut);
        s4 = (CheckBox) findViewById(R.id.checkBoxChemtut);
        s5 = (CheckBox) findViewById(R.id.checkBoxBiotut);
        s6 = (CheckBox) findViewById(R.id.checkBoxBusstut);
        s7 = (CheckBox) findViewById(R.id.checkBoxCStut);
        s8 = (CheckBox) findViewById(R.id.checkBoxEcontut);

        tutored = new ArrayList<Boolean>();
        for (int i = 0; i < 8; i++) {
            tutored.add(true);
        }

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("Edit");
        sendSubjects.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
        if (v.getId() == R.id.buttonsendsubs) {

            tutored.set(0, s1.isChecked());
            tutored.set(1, s2.isChecked());
            tutored.set(2, s3.isChecked());
            tutored.set(3, s4.isChecked());
            tutored.set(4, s5.isChecked());
            tutored.set(5, s6.isChecked());
            tutored.set(6, s7.isChecked());
            tutored.set(7, s8.isChecked());

            new NetworkTask().execute();
        }
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            makePostRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(getApplicationContext(), "Your information has been updated.",
                    Toast.LENGTH_LONG).show();
        }

        protected Object makePostRequest() {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(ApplicationManager.user.email, ApplicationManager.user.password));
            httpClient.setCredentialsProvider(credProvider);
            HttpPost httpPost = new HttpPost(URL);


            JSONArray list = new JSONArray();
            JSONObject holder = new JSONObject();

            try {
                for (int i = 0; i < tutored.size(); i++) {
                    if (tutored.get(i)) {
                        list.put(new JSONObject().put("subject", ApplicationManager.subjects.get(i)));
                    }
                }

                holder.put("id", Integer.toString(ApplicationManager.user.ID));
                holder.put("location", loc.getText().toString());
                holder.put("subjects", list);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("JSON created", holder.toString());

            //passes the results to a string builder/entity
            StringEntity se = null;
            try {
                se = new StringEntity(holder.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //sets the post request as the resulting string
            httpPost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            InputStream inputStream = null;
            String responseString = "";
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                responseString = sb.toString();
                Log.d("Http Post Response:", responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
