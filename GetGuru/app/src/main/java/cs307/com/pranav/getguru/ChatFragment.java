package cs307.com.pranav.getguru;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ChatFragment extends Fragment implements View.OnClickListener {

    View masterView;
    String URL;
    boolean flag = false;

    int id = 223;

    ButtonRectangle emit;
    EditText broadcastText;
    RelativeLayout chatScroll;

    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);


        chatScroll = (RelativeLayout) masterView.findViewById(R.id.rlchat);
        emit = (ButtonRectangle) masterView.findViewById(R.id.buttonemit);
        emit.setOnClickListener(this);
        mSocket.connect();


        broadcastText = (EditText)masterView.findViewById(R.id.broadcastText);

        mSocket.on("new message", onNewMessage);
        mSocket.connect();


        return masterView;



    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://4c8502db.ngrok.com");
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buttonemit) {

            String sendMessage = broadcastText.getText().toString();
            mSocket.emit("new message", sendMessage);
        }

        LayoutParams lparams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 50;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if (flag) {
            params.addRule(RelativeLayout.BELOW, id);
        }
        else {
            flag = true;
        }

        TextView newMess = new TextView(ApplicationManager.context);
        newMess.setId(id++);
        newMess.setBackgroundColor(Color.LTGRAY);
        newMess.setTextSize(25);
        newMess.setTextColor(Color.BLACK);
        newMess.setLayoutParams(lparams);
        newMess.setText(broadcastText.getText().toString());
        chatScroll.addView(newMess, params);

    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    LayoutParams lparams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                    RelativeLayout.LayoutParams params = new RelativeLayout.
                            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 50;
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    if (flag) {
                        params.addRule(RelativeLayout.BELOW, id);
                    }
                    else {
                        flag = true;
                    }


                    TextView newMess = new TextView(ApplicationManager.context);
                    newMess.setId(id++);
                    newMess.setBackgroundColor(Color.parseColor("#1E88E5"));
                    newMess.setTextSize(25);
                    newMess.setTextColor(Color.BLACK);
                    newMess.setLayoutParams(lparams);
                    newMess.setText(message);
                    chatScroll.addView(newMess, params);
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


}
