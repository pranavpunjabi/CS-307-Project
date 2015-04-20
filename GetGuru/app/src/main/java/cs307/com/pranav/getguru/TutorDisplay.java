package cs307.com.pranav.getguru;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TutorDisplay extends ActionBarActivity implements View.OnClickListener {

    String URL;
    TextView name, email, avgRating;
    ListView subs;

    ButtonRectangle fav, rate, viewRat;

    ArrayAdapter<String> subAdapter;
    ArrayList<String> subResults;

    int pressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_display);

        name = (TextView) findViewById(R.id.textViewtutorname);
        email = (TextView) findViewById(R.id.textViewtutoremail);
        avgRating = (TextView) findViewById(R.id.textViewavgrating);

        fav = (ButtonRectangle) findViewById(R.id.buttonfav);
        rate = (ButtonRectangle) findViewById(R.id.buttonrate);
        viewRat = (ButtonRectangle) findViewById(R.id.buttonviewRatings);

        rate.setOnClickListener(this);
        fav.setOnClickListener(this);
        viewRat.setOnClickListener(this);

        subs = (ListView) findViewById(R.id.listViewtutorsubs);

        subResults = new ArrayList<String>();
        subAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, subResults);
        subs.setAdapter(subAdapter);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("TutorInfo");

        new NetworkTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutor_display, menu);
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

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();


        params.add(new BasicNameValuePair("id", Integer.toString(ApplicationManager.searchTutorID)));


        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;

        return url;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonfav) {
            pressed = 1;
            URL = ApplicationManager.URL;
            URL += ApplicationManager.routes.get("Favorite");
            new NetworkTask().execute();
        }

        if (v.getId() == R.id.buttonrate) {
            Intent i = new Intent(TutorDisplay.this, SubmitRating.class);
            startActivity(i);
        }

        if (v.getId() == R.id.buttonviewRatings) {
            Intent i = new Intent(TutorDisplay.this, RatingDisplay.class);
            startActivity(i);
        }
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            //makePostRequest();
            if (pressed == 1) {
                return makePostRequest();
            }
            else
                return makeGetRequest();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (pressed == 1) {
                Toast.makeText(getApplicationContext(), "Added to Favorites",
                        Toast.LENGTH_LONG).show();
            }
            else {
                JSONObject result = (JSONObject) o;
                String nmStr = null;
                String emStr = null;
                double avgrat = 0;

                try {
                    nmStr = result.getString("firstname");
                    nmStr += " ";
                    nmStr += result.get("lastname");
                    emStr = result.getString("email");
                    avgrat = result.getDouble("rating");
                    JSONArray recSubs = result.getJSONArray("subjects");
                    for (int i = 0; i < recSubs.length(); i++) {
                        subResults.add(recSubs.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                name.setText(nmStr);
                email.setText(emStr);
                avgRating.setText(Double.toString(avgrat));
                subAdapter.notifyDataSetChanged();

            }

        }

        protected Object makePostRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            JSONObject holder = new JSONObject();

            try {
                holder.put("studentID", ApplicationManager.user.ID);
                holder.put("tutorID", ApplicationManager.searchTutorID);

            } catch (JSONException e) {
                e.printStackTrace();
            }


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

        protected Object makeGetRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(addParametersToUrl(URL));

            JSONObject json = null;


            //making GET request.
            InputStream inputStream = null;
            String responseString = "";
            try {
                HttpResponse response = httpClient.execute(httpGet);
                // write response to log
                inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                responseString = sb.toString();
                Log.d("Http Get Response:", responseString);
                json = new JSONObject(responseString);


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }

            return json;
        }
    }
}
