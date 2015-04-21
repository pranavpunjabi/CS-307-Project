package cs307.com.pranav.getguru;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * Created by DiGiT_WiZARD on 3/4/15.
 */

//SHOW LOCATION
//VIEW RATINGS
//POLISH UI

public class TutorProfileFragment extends Fragment implements OnClickListener {

    ButtonRectangle toggle, edit, vyr;
    View masterView;

    ListView tutorSubjects;
    TextView name, email, avgRating;

    String URL;


    ArrayAdapter<String> subAdapter;
    ArrayList<String> subResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);
        masterView = inflater.inflate(R.layout.profile_tutor, container, false);
        masterView.setBackgroundColor(Color.WHITE);
        toggle = (ButtonRectangle) masterView.findViewById(R.id.toggle2);
        edit = (ButtonRectangle) masterView.findViewById(R.id.editInfo2);
        vyr = (ButtonRectangle) masterView.findViewById(R.id.buttonvyr);
        tutorSubjects = (ListView) masterView.findViewById(R.id.subjectListtutorprofile);

        name = (TextView) masterView.findViewById(R.id.nameTutor);
        email = (TextView) masterView.findViewById(R.id.emailTutor);
        avgRating = (TextView) masterView.findViewById(R.id.ratingTutor);

        subResults = new ArrayList<String>();
        subAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, subResults);
        tutorSubjects.setAdapter(subAdapter);

        toggle.setOnClickListener(this);
        edit.setOnClickListener(this);
        vyr.setOnClickListener(this);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("TutorInfo");

        ApplicationManager.searchTutorID = ApplicationManager.user.ID;

        new NetworkTask().execute();

        return masterView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editInfo2:
                Intent i = new Intent(this.getActivity(), Edit.class);
                startActivity(i);
                break;
            case R.id.toggle2:
                Intent j = new Intent(this.getActivity(), StudentTabHost.class);
                startActivity(j);
                break;
            case R.id.buttonvyr:
                Intent k = new Intent(this.getActivity(), RatingDisplay.class);
                startActivity(k);
                break;
        }
    }

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();


        params.add(new BasicNameValuePair("id", Integer.toString(ApplicationManager.user.ID)));


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
