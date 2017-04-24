package com.example.nirmal.attendancetracker;

import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Button next;
    private int icons[] = {R.drawable.mapmarker,R.drawable.calendarclock};
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialise all the views
        initViews();

    }

    //Initialise all the views
    //Setup the viewpager and inflate them with the necessary data
    public void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewPager = (ViewPager) findViewById(R.id.viewpagers);
        tabLayout = (TabLayout) findViewById(R.id.tablayers);
        //Set up the adapter
        ViewPagerCustomAdapter adapter = new ViewPagerCustomAdapter(getSupportFragmentManager());
        adapter.addFragmentToTabs(new MapFragment(),"    Location");
        adapter.addFragmentToTabs(new RecordsFragment(),"    Records");
        //Assign the adapter to the viewpager
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //Set the tablayout with icons and text
        tabLayout.getTabAt(0).setIcon(icons[0]);
        tabLayout.getTabAt(1).setIcon(icons[1]);
    }

    public class ViewPagerCustomAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragmentList = new ArrayList<>();
        private final ArrayList<String> fragmentListTitle = new ArrayList<>();

        public ViewPagerCustomAdapter(FragmentManager manager){
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentListTitle.get(position);
        }

        public void addFragmentToTabs(Fragment fragment, String Title){
            fragmentList.add(fragment);
            fragmentListTitle.add(Title);
        }
    }
}
