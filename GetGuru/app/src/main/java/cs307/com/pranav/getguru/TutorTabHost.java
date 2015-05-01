package cs307.com.pranav.getguru;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
        tHost.setBackgroundColor(Color.parseColor("#1E88E5"));
        tHost.setup();

        ApplicationManager.context = this.getBaseContext();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                TutorProfileFragment profileFragment = (TutorProfileFragment) fm.findFragmentByTag("profile");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ChatFragment chatFragment = (ChatFragment) fm.findFragmentByTag("chat");

                if(profileFragment!=null)
                    ft.detach(profileFragment);
                if(chatFragment!=null)
                    ft.detach(chatFragment);

                if(tabId.equalsIgnoreCase("chat")){

                    if(chatFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new ChatFragment(), "chat");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(chatFragment);
                    }

                }
                if(tabId.equalsIgnoreCase("profile")){

                    if(profileFragment==null){
                        ft.add(R.id.realtabcontent2,new TutorProfileFragment(), "profile");
                    }else{
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

        /** Defining tab builder for Chat tab */
        TabHost.TabSpec tSpecChat = tHost.newTabSpec("chat");
        tSpecChat.setIndicator("Chat");
        tSpecChat.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecChat);
    }
}
