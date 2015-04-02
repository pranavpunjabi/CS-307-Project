package cs307.com.pranav.getguru;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
public class SearchFragment extends Fragment implements View.OnClickListener {

    //

    View masterView;
    String URL;
    Button searchOptions, sendC;
    ListView searchHolder;

    private String provider;

    float lat;
    float lng;
    private LocationManager locationManager;
    Location location;

    ArrayAdapter<String> searchAdapter;
    ArrayList<String> searchResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        masterView = inflater.inflate(R.layout.search_student, container, false);
        searchOptions = (Button) masterView.findViewById(R.id.buttonsrchpts);
        searchOptions.setOnClickListener(this);
        sendC = (Button) masterView.findViewById(R.id.buttonsendc);
        sendC.setOnClickListener(this);

        searchHolder = (ListView) masterView.findViewById(R.id.listViewSearch);

        searchResults = new ArrayList<String>();
        searchResults.add("");
        searchAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, searchResults);

        searchHolder.setAdapter(searchAdapter);



        locationManager = (LocationManager) ApplicationManager.context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
        else {

        }

        URL = ApplicationManager.URL;
        URL.concat(ApplicationManager.routes.get("Search"));

        return masterView;

    }

    public void onLocationChanged(Location location) {
        lat = (float) (location.getLatitude());
        lng = (float) (location.getLongitude());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonsrchpts) {
            Intent i = new Intent(this.getActivity(), SearchOptions.class);
            startActivity(i);
        }

        if (v.getId() == R.id.buttonsendc) {
            new NetworkTask().execute();
        }
    }

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();


        params.add(new BasicNameValuePair("requestType", "locSearch"));
        params.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
        params.add(new BasicNameValuePair("longitude", String.valueOf(lng)));


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

        }

        protected Object makePostRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            JSONObject holder = new JSONObject();

            try {
                holder.put("latitude", lat);
                holder.put("longitude", lng);

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

        protected void makeGetRequest() {
            HttpClient httpClient = new DefaultHttpClient();
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
                Log.d("Http Get Response:", json.getString("return"));

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
