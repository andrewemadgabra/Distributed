package com.example.andrew.newsapp;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class NewsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NewsFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);
        adapter = new NewsFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public NewsFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    LocalNewsFragment tab1 = new LocalNewsFragment();
                    return tab1;
                case 1:

                    GlobalNewsFragment tab2 = new GlobalNewsFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return "Local";
                case 1:
                    return "Global";
                default:
                    return null;
            }
        }
    }
}
