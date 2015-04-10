package cs307.com.pranav.getguru;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class SubmitRating extends ActionBarActivity implements View.OnClickListener{

    EditText review;
    RatingBar rating;
    Button submit;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_rating);

        review = (EditText) findViewById(R.id.editTextreview);
        rating = (RatingBar) findViewById(R.id.ratingBar);

        submit = (Button) findViewById(R.id.buttonsubmitrating);
        submit.setOnClickListener(this);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("Rate");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit_rating, menu);
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
        if (v.getId() == R.id.buttonsubmitrating) {
            new NetworkTask().execute();
        }
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            return makePostRequest();
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(getApplicationContext(), "Rating submitted",
                        Toast.LENGTH_LONG).show();
        }

        protected Object makePostRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            JSONObject holder = new JSONObject(); //tutID, stuID, ratings, reviews, POST

            try {
                holder.put("stuID", ApplicationManager.user.ID);
                holder.put("tutID", ApplicationManager.searchTutorID);
                holder.put("ratings", rating.getRating());
                holder.put("reviews", review.getText().toString());

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
    }

}
