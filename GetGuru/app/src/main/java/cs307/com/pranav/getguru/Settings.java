package cs307.com.pranav.getguru;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class Settings extends Activity implements View.OnClickListener {

    ButtonRectangle delAcc, unReg, edit;
    MaterialEditText fn, ln, em, pass;

    String URL;
    int buttonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        delAcc = (ButtonRectangle) findViewById(R.id.buttondelacc);
        unReg = (ButtonRectangle) findViewById(R.id.buttonuregisterastut);
        edit = (ButtonRectangle) findViewById(R.id.buttoneditsett);

        fn = (MaterialEditText) findViewById(R.id.setteditfn);
        ln = (MaterialEditText) findViewById(R.id.setteditln);
        em = (MaterialEditText) findViewById(R.id.setteditEmail);
        pass = (MaterialEditText) findViewById(R.id.setteditpass);


        delAcc.setOnClickListener(this);
        unReg.setOnClickListener(this);
        edit.setOnClickListener(this);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("Unregister");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
    public void onBackPressed() {
        Intent k = new Intent(Settings.this, StudentTabHost.class);
        startActivity(k);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttondelacc) {
            URL = ApplicationManager.URL;
            URL += ApplicationManager.routes.get("Delete");
            buttonPressed = 1;
            new NetworkTask().execute();
        }
        if (v.getId() == R.id.buttonuregisterastut) {
            URL = ApplicationManager.URL;
            URL += ApplicationManager.routes.get("Unregister");
            buttonPressed = 2;
            new NetworkTask().execute();
        }
        if (v.getId() == R.id.buttoneditsett) {
            URL = ApplicationManager.URL;
            URL += ApplicationManager.routes.get("EditUserInfo");
            buttonPressed = 3;
            new NetworkTask().execute();
        }
    }

    private class NetworkTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... params) {
            HashMap values = new HashMap();

            switch (buttonPressed) {
                case 0:
                    break;
                case 1:
                    values.put(new String("id"), ApplicationManager.user.ID);
                    break;
                case 2:
                    values.put(new String("id"), ApplicationManager.user.ID);
                    break;
                case 3:
                    values.put(new String("id"), ApplicationManager.user.ID);
                    values.put(new String("firstname"), fn.getText().toString());
                    values.put(new String("lastname"), ln.getText().toString());
                    values.put(new String("email"), em.getText().toString());
                    values.put(new String("password"), pass.getText().toString());
                    break;
            }
            makePostRequest(values);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            switch (buttonPressed) {
                case 0:
                    break;
                case 1:
                    Intent k = new Intent(Settings.this, MyActivity.class);
                    startActivity(k);
                    break;
                case 2:
                    ApplicationManager.user.isTutor = false;
                    Toast.makeText(getApplicationContext(), "You have unregistered as tutor.",
                            Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    //ApplicationManager.user.
                    Toast.makeText(getApplicationContext(), "Your information has been updated.",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }

        protected Object makePostRequest(Map params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(ApplicationManager.user.email, ApplicationManager.user.password));
            httpClient.setCredentialsProvider(credProvider);
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
                if (buttonPressed == 3) {
                    if (pass.getText().toString().equals("")) {
                        String oldPass = ApplicationManager.user.password;
                        ApplicationManager.user = new User(json.getString("firstname"),
                                json.getString("lastname"), json.getString("email"),
                                json.getInt("id"), oldPass);
                    }
                    else {
                        ApplicationManager.user = new User(json.getString("firstname"),
                                json.getString("lastname"), json.getString("email"),
                                json.getInt("id"), json.getString("password"));
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
