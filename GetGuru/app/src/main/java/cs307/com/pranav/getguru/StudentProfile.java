package cs307.com.pranav.getguru;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;


public class StudentProfile extends FragmentActivity {

    ActionBar actionBar;
    ViewPager viewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);


        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();

        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        };

        /** Setting the pageChange listner to the viewPager */
        viewPager.setOnPageChangeListener(pageChangeListener);

        /** Creating an instance of FragmentPagerAdapter */
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);

        viewPager.setAdapter(fragmentPagerAdapter);
        actionBar.setDisplayShowTitleEnabled(true);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {

            }

            @Override
            public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        /** Creating Android Tab */
        // Tab tab = actionabar.newTab().setText("My Friends").setIcon(R.drawable.myfriends).setTabListener(tabListener);
        Tab tab = actionBar.newTab().setText("My Friends").setTabListener(tabListener);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText("Android Version").setTabListener(tabListener);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText("Android Phones").setTabListener(tabListener);
        actionBar.addTab(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_profile, menu);
        return true;
    }
}
