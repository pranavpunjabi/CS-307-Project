package cs307.com.pranav.getguru;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Chat extends Activity implements View.OnClickListener {

    String URL;
    boolean flag = false;

    int vid = 223;

    ButtonRectangle emit;
    EditText broadcastText;
    RelativeLayout chatScroll;

    ArrayList<TextView> chatArray;
    ArrayList<String> chatHist;
    ArrayList<Integer> chatInd;

    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatArray = new ArrayList<TextView>();
        chatHist = new ArrayList<String>();
        chatInd = new ArrayList<Integer>();

        chatScroll = (RelativeLayout) findViewById(R.id.rlchat);
        emit = (ButtonRectangle) findViewById(R.id.buttonemit);
        emit.setOnClickListener(this);

        broadcastText = (EditText) findViewById(R.id.broadcastText);

        ApplicationManager.context = this;

        mSocket.on("new message", onNewMessage);
        mSocket.connect();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://4c8502db.ngrok.com");
        } catch (URISyntaxException e) {}
    }

    void populateChat() {
        for (int i = 0; i < chatHist.size(); i++) {
            if (chatInd.get(i) == 1) {
                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lparams.rightMargin = 50;
                lparams.leftMargin = 100;
                lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                if (flag) {
                    lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
                }
                else {
                    flag = true;
                    lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                }
                lparams.topMargin = 50;

                TextView newMess = new TextView(this);
                newMess.setId(vid++);
                newMess.setBackgroundColor(Color.LTGRAY);
                newMess.setTextSize(20);
                newMess.setTextColor(Color.BLACK);
                newMess.setLayoutParams(lparams);
                newMess.setPadding(10, 10, 10, 10);
                newMess.setText(chatHist.get(i));


                newMess.setBackground(getResources().getDrawable(R.drawable.rounded_button2));
                chatArray.add(newMess);
                chatScroll.addView(newMess);
            }
            else {
                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lparams.leftMargin = 50;
                lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                if (flag) {
                    lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
                } else {
                    flag = true;
                    lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                }
                lparams.topMargin = 50;
                lparams.rightMargin = 100;

                TextView newMess = new TextView(ApplicationManager.context);
                newMess.setId(vid++);
                newMess.setBackgroundColor(Color.parseColor("#1E88E5"));
                newMess.setTextSize(20);
                newMess.setPadding(10, 10, 10, 10);
                newMess.setTextColor(Color.BLACK);
                newMess.setLayoutParams(lparams);
                newMess.setText(chatHist.get(i));

                newMess.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                chatArray.add(newMess);
                chatScroll.addView(newMess);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buttonemit) {

            String sendMessage = broadcastText.getText().toString();
            mSocket.emit("new message", sendMessage);
        }

        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lparams.rightMargin = 50;
        lparams.leftMargin = 100;
        lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if (flag) {
            lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
        }
        else {
            flag = true;
            lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
        lparams.topMargin = 50;

        TextView newMess = new TextView(this);
        newMess.setId(vid++);
        newMess.setBackgroundColor(Color.LTGRAY);
        newMess.setTextSize(20);
        newMess.setTextColor(Color.BLACK);
        newMess.setLayoutParams(lparams);
        newMess.setPadding(10, 10, 10, 10);
        newMess.setText(broadcastText.getText().toString());


        newMess.setBackground(getResources().getDrawable(R.drawable.rounded_button2));
        chatArray.add(newMess);
        chatScroll.addView(newMess);

        broadcastText.setText("");
        broadcastText.clearFocus();

    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    Log.d("Response: ", args[0].toString());

                    JSONObject data = (JSONObject) args[0];

                    String message;
                    try {
                        message = data.getString("key");
                    } catch (JSONException e) {
                        return;
                    }

                    RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lparams.leftMargin = 50;
                    lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    if (flag) {
                        lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
                    } else {
                        flag = true;
                        lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    }
                    lparams.topMargin = 50;
                    lparams.rightMargin = 100;

                    TextView newMess = new TextView(ApplicationManager.context);
                    newMess.setId(vid++);
                    newMess.setBackgroundColor(Color.parseColor("#1E88E5"));
                    newMess.setTextSize(20);
                    newMess.setPadding(10, 10, 10, 10);
                    newMess.setTextColor(Color.BLACK);
                    newMess.setLayoutParams(lparams);
                    newMess.setText(message);

                    newMess.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                    chatArray.add(newMess);
                    chatScroll.addView(newMess);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
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
            makeGetRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

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
                Log.d("Http Get Response:", json.getString("return"));


                JSONArray iter = json.getJSONArray("return");
                Log.d("iter:", iter.toString());


                for (int i = 0; i < iter.length(); i++) {
                    JSONObject tutObj = (JSONObject) iter.get(i);
                    Log.d("Object in loop:", tutObj.toString());
                    String name = tutObj.getString("firstName");
                    name += " ";
                    name += tutObj.getString("lastName");
                    Log.d("Name found:", name);

                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
