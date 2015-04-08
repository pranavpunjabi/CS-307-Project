package cs307.com.pranav.getguru;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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


public class StudentProfileFragment extends Fragment implements View.OnClickListener {


    View masterView;
    Button toggle, edit, logout;
    TextView name, major, clss, age;
    String URL;

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        masterView = inflater.inflate(R.layout.profile_student, container, false);
        toggle = (Button) masterView.findViewById(R.id.toggle);
        edit = (Button) masterView.findViewById(R.id.editInfo);
        logout = (Button) masterView.findViewById(R.id.buttonlogout);

        name = (TextView) masterView.findViewById(R.id.nameStudent);
        major = (TextView) masterView.findViewById(R.id.education);
        clss = (TextView) masterView.findViewById(R.id.classification);
        age = (TextView) masterView.findViewById(R.id.ageStudent);

        //name.setText(ApplicationManager.user.firstName + ApplicationManager.user.lastName);


        toggle.setOnClickListener(this);
        edit.setOnClickListener(this);
        logout.setOnClickListener(this);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("Toggle");

        //return super.onCreateView(inflater, container, savedInstanceState);

        return masterView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editInfo:
                Intent i = new Intent(this.getActivity(), Edit.class);
                startActivity(i);
                break;
            case R.id.toggle:
                if (!ApplicationManager.user.isTutor) {
                    new NetworkTask().execute();
                }
                Intent j = new Intent(this.getActivity(),  TutorTabHost.class);
                startActivity(j);
                break;
            case R.id.buttonlogout:
                prefs.edit().putString("UserFirstName", "");
                prefs.edit().putString("UserLastName", "");
                prefs.edit().putString("UserEmail", "");
                prefs.edit().putInt("UserID", -1);
                ApplicationManager.resetApplication();
                Intent k = new Intent(this.getActivity(),  MyActivity.class);
                startActivity(k);
                break;
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

        }

        protected Object makePostRequest() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);


            JSONArray list = new JSONArray();
            JSONObject holder = new JSONObject();

            try {

                holder.put("id", Integer.toString(ApplicationManager.user.ID));
                holder.put("location", "47906");

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

