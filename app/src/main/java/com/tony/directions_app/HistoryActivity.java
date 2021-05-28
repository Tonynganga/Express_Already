package com.tony.directions_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tony.directions_app.Models.Model;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ViewPager viewPager2;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    viewPagerAdapter2 viewPagerAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        viewPager2=findViewById(R.id.pager1);
        tabLayout=findViewById(R.id.tab_layout1);


        viewPagerAdapter2=new viewPagerAdapter2(getSupportFragmentManager());


        tabLayout.setupWithViewPager(viewPager2);
        viewPager2.setAdapter(viewPagerAdapter2);
    }

}