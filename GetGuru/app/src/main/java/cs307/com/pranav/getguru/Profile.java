package cs307.com.pranav.getguru;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Profile extends ListFragment{

    /** An array of items to display in ArrayList */
    String myfriends_list[] = new String[]{
            "Abhi Tripathi",
            "Sandeep Pal",
            "Avi Diwakar",
            "Shishir Verma",
            "Amit Verma"
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /** Creating array adapter to set data in listview */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_multiple_choice, myfriends_list);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        /** Setting the multiselect choice mode for the listview */
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }
}

