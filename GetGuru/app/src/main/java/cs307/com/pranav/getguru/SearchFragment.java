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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gc.materialdesign.views.ButtonRectangle;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
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

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    View masterView;
    String URL;
    ButtonRectangle searchOptions, sendC, test;
    ListView searchHolder;

    private String provider;

    float lat;
    float lng;
    private LocationManager locationManager;
    Location location;

    ArrayAdapter<String> searchAdapter;
    ArrayList<String> searchResults;
    ArrayList<Integer> tutorIDs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.search_student, container, false);
        searchOptions = (ButtonRectangle) masterView.findViewById(R.id.buttonsrchpts);
        searchOptions.setOnClickListener(this);
        sendC = (ButtonRectangle) masterView.findViewById(R.id.buttonsendc);
        sendC.setOnClickListener(this);
        test = (ButtonRectangle) masterView.findViewById(R.id.buttontest);
        test.setOnClickListener(this);

        searchHolder = (ListView) masterView.findViewById(R.id.listViewSearch);

        tutorIDs = new ArrayList<Integer>();
        searchResults = new ArrayList<String>();

        searchAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, searchResults);

        searchHolder.setAdapter(searchAdapter);
        searchHolder.setOnItemClickListener(this);


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
        URL += ApplicationManager.routes.get("Search");

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

        if (v.getId() == R.id.buttontest) {
            searchAdapter.add("Another result");
        }
    }

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

        params.add(new BasicNameValuePair("requestType", "locSearch"));
        params.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
        params.add(new BasicNameValuePair("longitude", String.valueOf(lng)));
        params.add(new BasicNameValuePair("zip", ApplicationManager.userPrefrences.get("searchCode")));
        params.add(new BasicNameValuePair("rating", ApplicationManager.userPrefrences.get("searchRating")));
        for (int j = 0; j < ApplicationManager.subjects.size(); j++) {
            if (ApplicationManager.subjectsBools.get(j)) {
                params.add(new BasicNameValuePair("subject", ApplicationManager.subjects.get(j)));
            }
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;
        return url;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Parent", parent.toString());
        Log.d("Position", Integer.toString(position));

        ApplicationManager.searchTutorID = tutorIDs.get(position);
        Intent i = new Intent(this.getActivity(), TutorDisplay.class);
        startActivity(i);
    }


    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            makeGetRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            searchAdapter.notifyDataSetChanged();
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


                JSONArray iter = json.getJSONArray("return");
                Log.d("iter:", iter.toString());

                searchResults.clear();
                tutorIDs.clear();

                for (int i = 0; i < iter.length(); i++) {
                    JSONObject tutObj = (JSONObject) iter.get(i);
                    Log.d("Object in loop:", tutObj.toString());
                    String name = tutObj.getString("firstName");
                    name += " ";
                    name += tutObj.getString("lastName");
                    Log.d("Name found:", name);
                    searchResults.add(name);
                    tutorIDs.add(tutObj.getInt("id"));
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
