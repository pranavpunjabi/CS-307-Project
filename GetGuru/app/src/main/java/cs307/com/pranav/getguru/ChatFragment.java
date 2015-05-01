package cs307.com.pranav.getguru;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

    ButtonRectangle emit;
    EditText broadcastText;

    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);

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

                    broadcastText.setText(message);
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
