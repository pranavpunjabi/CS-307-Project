package cs307.com.pranav.getguru;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;



public class MyActivity extends Activity implements View.OnClickListener {


    Button signUpButton, signInButton;
    EditText signInEmail, signInPass, signUpName, signUpEmail, signUpPass, signUpRepass;
    protected String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        signInEmail = (EditText) findViewById(R.id.sieditTextemail);
        signInPass = (EditText) findViewById(R.id.sieditTextpass);
        signUpName = (EditText) findViewById(R.id.suedittextname);
        signUpEmail = (EditText) findViewById(R.id.suedittextemail);
        signUpPass = (EditText) findViewById(R.id.suedittextpass);
        signUpRepass = (EditText) findViewById(R.id.suedittextrepass);

        signInButton = (Button) findViewById(R.id.sibutton);
        signUpButton = (Button) findViewById(R.id.subutton);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        URL = "";

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

    protected String addLocationToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();


        params.add(new BasicNameValuePair("message", "hello"));
        params.add(new BasicNameValuePair("me", "1"));
        params.add(new BasicNameValuePair("you", "2"));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    private boolean inputValidated(int buttonPressed) {
        boolean validated = false;
        switch (buttonPressed) {
            case 1:
                String siEmail = signInEmail.getText().toString();
                String siPass = signInPass.getText().toString();
                if (!siEmail.matches("") && !siPass.matches("")) {
                    validated = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "One or more required fields is empty",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                String suName = signUpName.getText().toString();
                String suEmail = signUpEmail.getText().toString();
                String suPass = signUpPass.getText().toString();
                String suRepass = signUpRepass.getText().toString();
                if (!suName.matches("") && !suEmail.matches("")
                        && !suPass.matches("") && !suRepass.matches("")) {
                    if (suPass.matches(suRepass)) {
                        validated = true;
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

        switch (v.getId()) {
            case R.id.sibutton:
                if (inputValidated(1))
                    new NetworkTask().execute();
                break;
            case R.id.subutton:
                if (inputValidated(2))
                    new NetworkTask().execute();
                break;
        }
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(addLocationToUrl(URL));

            //making GET request.
            InputStream inputStream = null;
            String responsestr = "";
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

                responsestr = sb.toString();
                Log.d("Http Get Response:", responsestr);
                JSONObject json = new JSONObject(responsestr);

            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
