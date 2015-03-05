package cs307.com.pranav.getguru;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TabHost;


public class TutorTabHost extends FragmentActivity {

    TabHost tHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_tab_host);

        tHost = (TabHost) findViewById(R.id.tabhost2);
        tHost.setup();

        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
            TutorProfileFragment profileFragment = (TutorProfileFragment) fm.findFragmentByTag("profile");
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

            @Override
            public void onTabChanged(String tabId) {
                if(profileFragment!=null)
                    ft.detach(profileFragment);

                if(tabId.equalsIgnoreCase("profile")){

                    if(profileFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent2,new TutorProfileFragment(), "profile");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(profileFragment);
                    }

                }
                ft.commit();

            }

        };

        tHost.setOnTabChangedListener(tabChangeListener);

        TabHost.TabSpec tSpecProfile = tHost.newTabSpec("profile");
        tSpecProfile.setIndicator("Profile");
        tSpecProfile.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecProfile);
    }
}
