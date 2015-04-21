package cs307.com.pranav.getguru;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */
public class FavoritesFragment extends Fragment implements View.OnClickListener {

    View masterView;
    String URL;
    ListView favoriteHolder;

    ArrayAdapter<String> favoriteAdapter;
    ArrayList<String> favorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        masterView = inflater.inflate(R.layout.favorites_student, container, false);
        masterView.setBackgroundColor(Color.WHITE);
        favoriteHolder = (ListView) masterView.findViewById(R.id.listViewFav);


        favorites = new ArrayList<String>();

        favoriteAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, favorites);

        favoriteHolder.setAdapter(favoriteAdapter);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("getFavorite");

        new NetworkTask().execute();

        return masterView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();


        params.add(new BasicNameValuePair("studentID", Integer.toString(ApplicationManager.user.ID)));


        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;

        return url;
    }


    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            //makePostRequest();
            return makeGetRequest();
        }

        @Override
        protected void onPostExecute(Object o) {
            favoriteAdapter.notifyDataSetChanged();
        }

        protected Object makePostRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            JSONObject holder = new JSONObject();

            try {
                holder.put("radius", ApplicationManager.userPrefrences.get("searchRadius"));
                holder.put("subject", ApplicationManager.userPrefrences.get("searchSubject"));
                holder.put("rating", ApplicationManager.userPrefrences.get("searchRating"));

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
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(ApplicationManager.user.email, ApplicationManager.user.password));
            httpClient.setCredentialsProvider(credProvider);
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
                Log.d("Http Get Response:", json.getString("return"));


                JSONArray iter = json.getJSONArray("return");
                Log.d("iter:", iter.toString());

                favorites.clear();

                for (int i = 0; i < iter.length(); i++) {
                    JSONObject tutObj = (JSONObject) iter.get(i);
                    Log.d("Object in loop:", tutObj.toString());
                    String name = tutObj.getString("firstName");
                    name += " ";
                    name += tutObj.getString("lastName");
                    Log.d("Name found:", name);
                    favorites.add(name);
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }

            return json;
        }
    }
}
