package cs307.com.pranav.getguru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DiGiT_WiZARD on 3/4/15.
 */
public class TutorProfileFragment extends Fragment implements OnClickListener {

    Button toggle, edit;
    View masterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);
        masterView = inflater.inflate(R.layout.profile_tutor, container, false);

        toggle = (Button) masterView.findViewById(R.id.toggle2);
        edit = (Button) masterView.findViewById(R.id.editInfo2);

        toggle.setOnClickListener(this);
        edit.setOnClickListener(this);

        return masterView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editInfo2:
                Intent i = new Intent(this.getActivity(), Edit.class);
                startActivity(i);
                break;
            case R.id.toggle2:
                Intent j = new Intent(this.getActivity(), StudentTabHost.class);
                startActivity(j);
                break;
        }
    }
}
