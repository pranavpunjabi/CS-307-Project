package cs307.com.pranav.getguru;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatFragment extends Fragment {

    View masterView;
    String URL;

    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_chat, container, false);
        masterView.setBackgroundColor(Color.WHITE);


        return masterView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
