package cs307.com.pranav.getguru;


import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TabHost;


public class StudentTabHost extends FragmentActivity {
    TabHost tHost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tab_host);

        tHost = (TabHost) findViewById(R.id.tabhost1);
        tHost.setBackgroundColor(Color.parseColor("#1E88E5"));
        tHost.setup();

        ApplicationManager.context = this.getBaseContext();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                FavoritesFragment favoritesFragment = (FavoritesFragment) fm.findFragmentByTag("favorite");
                StudentProfileFragment profileFragment = (StudentProfileFragment) fm.findFragmentByTag("profile");
                SearchFragment searchFragment = (SearchFragment) fm.findFragmentByTag("search");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(favoritesFragment!=null)
                    ft.detach(favoritesFragment);
                if(profileFragment!=null)
                    ft.detach(profileFragment);
                if(searchFragment!=null)
                    ft.detach(searchFragment);

                if(tabId.equalsIgnoreCase("favorite")){

                    if(favoritesFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new FavoritesFragment(), "favorite");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(favoritesFragment);
                    }

                }
                if(tabId.equalsIgnoreCase("profile")){

                    if(profileFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new StudentProfileFragment(), "profile");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(profileFragment);
                    }

                }
                if(tabId.equalsIgnoreCase("search")){

                    if(searchFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new SearchFragment(), "search");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(searchFragment);
                    }

                }
                ft.commit();
            }
        };

        /** Setting tabchangelistener for the tab */
        tHost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Profile tab */
        TabHost.TabSpec tSpecProfile = tHost.newTabSpec("profile");
        tSpecProfile.setIndicator("Profile");
        tSpecProfile.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecProfile);

        /** Defining tab builder for Search tab */
        TabHost.TabSpec tSpecSearch = tHost.newTabSpec("search");
        tSpecSearch.setIndicator("Search");
        tSpecSearch.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecSearch);

        /** Defining tab builder for Favorite tab */
        TabHost.TabSpec tSpecFavorite = tHost.newTabSpec("favorite");
        tSpecFavorite.setIndicator("Favorite");
        tSpecFavorite.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecFavorite);

        /** Defining tab builder for Chat tab */
        TabHost.TabSpec tSpecChat = tHost.newTabSpec("chat");
        tSpecChat.setIndicator("Chat");
        tSpecChat.setContent(new TabContent(getBaseContext()));
        tHost.addTab(tSpecChat);

//        /** Defining tab builder for Android (test) tab */
//        TabHost.TabSpec tSpecAnd = tHost.newTabSpec("android");
//        tSpecAnd.setIndicator("Android");
//        tSpecAnd.setContent(new TabContent(getBaseContext()));
//        tHost.addTab(tSpecAnd);

    }
}
