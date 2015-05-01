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
import java.util.ArrayList;

public class ChatFragment extends Fragment implements View.OnClickListener {

    View masterView;
    String URL;
    boolean flag = false;

    int vid = 223;

    ButtonRectangle emit;
    EditText broadcastText;
    RelativeLayout chatScroll;

    ArrayList<TextView> chatArray;

    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);

        chatArray = new ArrayList<TextView>();

        chatScroll = (RelativeLayout) masterView.findViewById(R.id.rlchat);
        emit = (ButtonRectangle) masterView.findViewById(R.id.buttonemit);
        emit.setOnClickListener(this);

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
        lparams.rightMargin = 50;
        lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if (flag) {
            lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
        }
        else {
            flag = true;
            lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
        lparams.topMargin = 50;

        TextView newMess = new TextView(ApplicationManager.context);
        newMess.setId(vid++);
        newMess.setBackgroundColor(Color.LTGRAY);
        newMess.setTextSize(25);
        newMess.setTextColor(Color.BLACK);
        newMess.setLayoutParams(lparams);
        newMess.setPadding(10, 10, 10, 10);
        newMess.setShadowLayer(50, 10, 10, Color.BLACK);
        newMess.setText(broadcastText.getText().toString());
        chatArray.add(newMess);
        chatScroll.addView(newMess);

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
                    lparams.leftMargin = 50;
                    lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    if (flag) {
                        lparams.addRule(RelativeLayout.BELOW, chatArray.get(chatArray.size() - 1).getId());
                    }
                    else {
                        flag = true;
                        lparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    }
                    lparams.topMargin = 50;

                    TextView newMess = new TextView(ApplicationManager.context);
                    newMess.setId(vid++);
                    newMess.setBackgroundColor(Color.parseColor("#1E88E5"));
                    newMess.setTextSize(25);
                    newMess.setPadding(10, 10, 10, 10);
                    newMess.setShadowLayer(50, 10, 10, Color.BLACK);
                    newMess.setTextColor(Color.BLACK);
                    newMess.setLayoutParams(lparams);
                    newMess.setText(message);
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


}
