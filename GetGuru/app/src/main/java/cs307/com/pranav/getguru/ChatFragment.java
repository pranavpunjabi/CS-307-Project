package cs307.com.pranav.getguru;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ChatFragment extends Fragment implements View.OnClickListener {

    View masterView;
    String URL;

    ButtonRectangle emit;

    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);

        emit = (ButtonRectangle) masterView.findViewById(R.id.buttonemit);
        emit.setOnClickListener(this);
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
            mSocket.emit("new message", "This message");
        }

    }
}
