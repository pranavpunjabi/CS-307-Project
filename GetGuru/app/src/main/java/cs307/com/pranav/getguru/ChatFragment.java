package cs307.com.pranav.getguru;

import android.content.Intent;
import android.graphics.Color;
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

public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    View masterView;
    String URL;

    ButtonRectangle testChat;

    ListView chatHolder;

    ArrayAdapter<String> chatAdapter;
    ArrayList<String> chatResults;
    ArrayList<Integer> chatIDs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);

        testChat = (ButtonRectangle) masterView.findViewById(R.id.buttonchattest);
        testChat.setOnClickListener(this);

        chatHolder = (ListView) masterView.findViewById(R.id.listViewchatdisplay);

        chatIDs = new ArrayList<Integer>();
        chatResults = new ArrayList<String>();

        chatAdapter = new ArrayAdapter<String>(ApplicationManager.context, android.R.layout.simple_list_item_1, chatResults);

        chatHolder.setAdapter(chatAdapter);
        chatHolder.setOnItemClickListener(this);

        URL = ApplicationManager.URL;
        URL += ApplicationManager.routes.get("ActiveChats");

        new NetworkTask().execute();

        return masterView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Parent", parent.toString());
        Log.d("Position", Integer.toString(position));

        ApplicationManager.searchTutorID = chatIDs.get(position);
        Intent i = new Intent(this.getActivity(), Chat.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonchattest) {
            Intent i = new Intent(this.getActivity(), Chat.class);
            startActivity(i);
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
            makeGetRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            chatAdapter.notifyDataSetChanged();
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

                chatResults.clear();
                chatIDs.clear();

                for (int i = 0; i < iter.length(); i++) {
                    JSONObject tutObj = (JSONObject) iter.get(i);
                    Log.d("Object in loop:", tutObj.toString());
                    String name = tutObj.getString("firstName");
                    name += " ";
                    name += tutObj.getString("lastName");
                    Log.d("Name found:", name);
                    chatResults.add(name);
                    chatIDs.add(tutObj.getInt("id"));
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            } catch (JSONException e) {

            }
        }
    }
}
