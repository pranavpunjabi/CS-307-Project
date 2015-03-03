package cs307.com.pranav.getguru;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TabHost;


public class StudentProfile extends FragmentActivity {

    TabHost tHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                AndroidFragment androidFragment = (AndroidFragment) fm.findFragmentByTag("android");
                Profile profileFragment = (Profile) fm.findFragmentByTag("profile");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(androidFragment!=null)
                    ft.detach(androidFragment);
                if(profileFragment!=null)
                    ft.detach(profileFragment);

                /** If current tab is android */
                if(tabId.equalsIgnoreCase("android")){

                    if(androidFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new AndroidFragment(), "android");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(androidFragment);
                    }

                }
                if(tabId.equalsIgnoreCase("profile")){

                    if(profileFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new Profile(), "profile");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(profileFragment);
                    }

                }
                ft.commit();
            }
        };

        /** Setting tabchangelistener for the tab */
        tHost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecProfile = tHost.newTabSpec("profile");
        tSpecProfile.setIndicator("Profile");
        tSpecProfile.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecProfile);

        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecAnd = tHost.newTabSpec("android");
        tSpecAnd.setIndicator("Android");
        tSpecAnd.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecAnd);

    }
}
