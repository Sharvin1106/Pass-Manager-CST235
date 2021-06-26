package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class EventTabs extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventmain);


        tabLayout= findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);
        FragmentAdapter fragment = new FragmentAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragment.addFragment(new FirstFragment(), "TODAY");
        fragment.addFragment(new SecondFragment(), "LAST WEEK");
        fragment.addFragment(new ThirdFragment(), "LAST MONTH");
        viewPager.setAdapter(fragment);

    }


}
