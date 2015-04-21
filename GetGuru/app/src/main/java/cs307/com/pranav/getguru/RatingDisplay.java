package cs307.com.pranav.getguru;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RatingDisplay extends ActionBarActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> groups;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_display);

        listView = (ListView) findViewById(R.id.listViewRatingsDisplay);
        groups = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups);
        listView.setAdapter(adapter);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("TutorRatings");

        new NetworkTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating_display, menu);
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

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            //makePostRequest();
            makeGetRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            adapter.notifyDataSetChanged();
        }

        protected void makeGetRequest() {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(ApplicationManager.user.email, ApplicationManager.user.password));
            httpClient.setCredentialsProvider(credProvider);
            HttpGet httpGet = new HttpGet(addParametersToUrl(URL));

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
                JSONObject json = new JSONObject(responseString);
                JSONArray iter = json.getJSONArray("return");
                Log.d("iter:", iter.toString());

                for (int i = 0; i < iter.length(); i++) {
                    JSONObject tutObj = (JSONObject) iter.get(i);
                    Log.d("Object in loop:", tutObj.toString());

                    String groupStr = "Rating Received: ";
                    groupStr += Integer.toString(tutObj.getInt("rating"));
                    groupStr += "\n\n";
                    groupStr += "Review Received: ";
                    groupStr += tutObj.getString("review");
                    groups.add(groupStr);
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
