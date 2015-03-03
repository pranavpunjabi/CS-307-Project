package cs307.com.pranav.getguru;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_student, container, false);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

