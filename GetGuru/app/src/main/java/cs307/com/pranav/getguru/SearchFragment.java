package cs307.com.pranav.getguru;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    View masterView;
    Button toggle, edit;
    String URL;

    private String provider;

    float lat;
    float lng;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        masterView = inflater.inflate(R.layout.search_student, container, false);
        toggle = (Button) masterView.findViewById(R.id.toggle);
        edit = (Button) masterView.findViewById(R.id.editInfo);

        locationManager = (LocationManager) ApplicationManager.context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        toggle.setOnClickListener(this);
        edit.setOnClickListener(this);

        URL = "http://1cbf193.ngrok.com/server/tutor";

        //return super.onCreateView(inflater, container, savedInstanceState);

        return masterView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }
}
