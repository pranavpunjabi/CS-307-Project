package cs307.com.pranav.getguru;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MyActivity extends Activity implements View.OnClickListener {


    ButtonRectangle signUpButton, signInButton, test;
    MaterialEditText signInEmail, signInPass, signUpName1, signUpName2, signUpEmail, signUpPass, signUpRepass;
    MaterialEditText testURL;

    protected String URL;

    private int buttonPressed = 0;
    private boolean success = false;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        signInEmail = (MaterialEditText) findViewById(R.id.sieditTextemail);
        signInPass = (MaterialEditText) findViewById(R.id.sieditTextpass);
        signUpName1 = (MaterialEditText) findViewById(R.id.suedittextfirstname);
        signUpName2 = (MaterialEditText) findViewById(R.id.suedittextlastname);
        signUpEmail = (MaterialEditText) findViewById(R.id.suedittextemail);
        signUpPass = (MaterialEditText) findViewById(R.id.suedittextpass);
        signUpRepass = (MaterialEditText) findViewById(R.id.suedittextrepass);

        testURL = (MaterialEditText) findViewById(R.id.testURL);

        signInButton = (ButtonRectangle) findViewById(R.id.sibutton);
        signUpButton = (ButtonRectangle) findViewById(R.id.subutton);
        test = (ButtonRectangle) findViewById(R.id.buttontest);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        test.setOnClickListener(this);

        ApplicationManager.initApplication();
        prefs = this.getSharedPreferences("GetGuruPrefs", Context.MODE_PRIVATE);
        ApplicationManager.URL = "http://9395340.ngrok.com";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean inputValidated(int buttonPressed) {
        boolean validated = false;
        switch (buttonPressed) {
            case 1:
                String siEmail = signInEmail.getText().toString();
                String siPass = signInPass.getText().toString();
                if (!siEmail.matches("") && !siPass.matches("")) {
                    if (Patterns.EMAIL_ADDRESS.matcher(siEmail).matches()) {
                        validated = true;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter a valid email",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "One or more required fields is empty",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                String suName1 = signUpName1.getText().toString();
                String suName2 = signUpName2.getText().toString();
                String suEmail = signUpEmail.getText().toString();
                String suPass = signUpPass.getText().toString();
                String suRepass = signUpRepass.getText().toString();
                if (!suName1.matches("") && !suEmail.matches("")
                        && !suPass.matches("") && !suRepass.matches("")) {
                    if (suPass.matches(suRepass)) {
                        if (Patterns.EMAIL_ADDRESS.matcher(suEmail).matches()) {
                            validated = true;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid email",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter matching passwords",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "One or more required fields is empty",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
        return validated;
    }

    @Override
    public void onClick(View v) {

        //ApplicationManager.URL = testURL.getText().toString();
        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("mainActivity");
        Log.d("string", URL);

        switch (v.getId()) {
            case R.id.sibutton:
                buttonPressed = 1;
                if (inputValidated(1))
                    new NetworkTask().execute();
                break;
            case R.id.subutton:
                buttonPressed = 2;
                if (inputValidated(2))
                    new NetworkTask().execute();
                break;
            case R.id.buttontest:
                ApplicationManager.user = new User("Bruce", "Wayne", "ceo@wayneIndustries.com", 1, "test");

                Intent j = new Intent(MyActivity.this, StudentTabHost.class);
                startActivity(j);
                break;
        }
    }

    protected String addParametersToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        String paramString = URLEncodedUtils.format(params, "utf-8");

        switch (buttonPressed) {
            case 0:
                break;
            case 1:
                params.add(new BasicNameValuePair("requestType", "Sign In"));
                params.add(new BasicNameValuePair("email", signInEmail.getText().toString()));
                params.add(new BasicNameValuePair("password", signInPass.getText().toString()));
                url += paramString;
                break;
            case 2:
                params.add(new BasicNameValuePair("requestType", "Sign Up"));
                params.add(new BasicNameValuePair("firstName", signUpName1.getText().toString()));
                params.add(new BasicNameValuePair("lastName", signUpName2.getText().toString()));
                params.add(new BasicNameValuePair("email", signUpEmail.getText().toString()));
                params.add(new BasicNameValuePair("password", signUpPass.getText().toString()));
                url += paramString;
                break;
        }

        return url;
    }

    private static JSONObject getJsonObjectFromMap(Map params) throws JSONException {

        //all the passed parameters from the post request
        //iterator used to loop through all the parameters
        //passed in the post request
        Iterator iter = params.entrySet().iterator();

        //Stores JSON
        JSONObject holder = new JSONObject();

        //using the earlier example your first entry would get email
        //and the inner while would get the value which would be 'foo@bar.com'
        //{ fan: { email : 'foo@bar.com' } }

        //While there is another entry
        while (iter.hasNext())
        {
            //gets an entry in the params
            Map.Entry pairs = (Map.Entry)iter.next();

            //creates a key for Map
            String key = (String)pairs.getKey();

            //Create a new map
            Map m = (Map)pairs.getValue();

            //object for storing Json
            JSONObject data = new JSONObject();

            //gets the value
            Iterator iter2 = m.entrySet().iterator();
            while (iter2.hasNext())
            {
                Map.Entry pairs2 = (Map.Entry)iter2.next();
                data.put((String)pairs2.getKey(), (String)pairs2.getValue());
            }

            //puts email and 'foo@bar.com'  together in map
            holder.put(key, data);
        }
        return holder;
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            HashMap values = new HashMap();

            switch (buttonPressed) {
                case 0:
                    break;
                case 1:
                    values.put(new String("requestType"), "signIn");
                    values.put(new String("email"), signInEmail.getText().toString());
                    values.put(new String("password"), signInPass.getText().toString());
                    break;
                case 2:
                    values.put(new String("requestType"), "signUp");
                    values.put(new String("firstName"), signUpName1.getText().toString());
                    values.put(new String("lastName"), signUpName2.getText().toString());
                    values.put(new String("email"), signUpEmail.getText().toString());
                    values.put(new String("password"), signUpPass.getText().toString());
                    break;
            }
            makePostRequest(values);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (success) {
                //Populate shared prefrences, Add data to ApplicationManager.user
                //Server must return all user data so it can be stored on sign in/ sign up

//                switch (buttonPressed) {
//                    case 0:
//                        break;
//                    case 1:
//
//                        ApplicationManager.user = new User("", "", signInEmail.getText().toString(), 1);
//                        break;
//                    case 2:
//                        ApplicationManager.user = new User(signUpName1.getText().toString(), signUpName2.getText().toString(),
//                                signUpEmail.getText().toString(), 1);
//                        break;
//                }

                prefs.edit().putString("UserFirstName", ApplicationManager.user.firstName);
                prefs.edit().putString("UserLastName", ApplicationManager.user.lastName);
                prefs.edit().putString("UserEmail", ApplicationManager.user.email);
                prefs.edit().putInt("UserID", ApplicationManager.user.ID);

                Intent j = new Intent(MyActivity.this, StudentTabHost.class);
                startActivity(j);
            }
        }

        protected Object makePostRequest(Map params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            JSONObject holder = new JSONObject(params);

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
                JSONObject json = new JSONObject(responseString);
                Log.d("Http Post Response:", json.getString("return"));
                if (json.getString("return").equals("success")) {
                    success = true;
                    if (buttonPressed == 1) {
                        ApplicationManager.user = new User(json.getString("firstname"),
                                json.getString("lastname"), json.getString("email"),
                                json.getInt("id"), signInPass.getText().toString());
                    }
                    else if (buttonPressed == 2) {
                        ApplicationManager.user = new User(json.getString("firstname"),
                                json.getString("lastname"), json.getString("email"),
                                json.getInt("id"), signUpPass.getText().toString());
                    }


                    if (buttonPressed == 1) {
                        if (json.getInt("ifTutor") == 1) {
                            ApplicationManager.user.isTutor = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

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
                if (json.getString("return").equals("success")) {
                    success = true;
                    ApplicationManager.user = new User(json.getString("firstname"),
                            json.getString("lastname"), json.getString("email"), json.getInt("id"), "");

                    if (buttonPressed == 1) {
                        if (json.getInt("ifTutor") == 1) {
                            ApplicationManager.user.isTutor = true;
                        }
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
