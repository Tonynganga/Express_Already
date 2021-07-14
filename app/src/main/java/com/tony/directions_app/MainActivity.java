package com.tony.directions_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  {
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    viewPagerAdapter viewPagerAdapter;
    FirebaseAuth mAuth;
    String psource;
    String pdestination;
    String csource;
    String cdestination;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        viewPager=findViewById(R.id.pager);
        tabLayout=findViewById(R.id.tab_layout);
        viewPagerAdapter=new viewPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        Intent intentopen = getIntent();
        if(intentopen.hasExtra("psource") && intentopen.hasExtra("pdestination")){

            viewPager.setCurrentItem(0);
        }else {
            viewPager.setCurrentItem(1);
        }
        FloatingActionButton fab1 = findViewById(R.id.fab_action1);

        Intent intent = getIntent();
        if(intent.hasExtra("psource") && intent.hasExtra("pdestination")) {
            psource = getIntent().getStringExtra("psource");
            pdestination = getIntent().getStringExtra("pdestination");

        }

        Intent intent1 = getIntent();
        if(intent1.hasExtra("Csource") && intent1.hasExtra("Cdestination")) {
            csource = getIntent().getStringExtra("Csource");
            cdestination = getIntent().getStringExtra("Cdestination");

        }



        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab_action12);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

    }
    public  Bundle getMyData(){
        Bundle hn = new Bundle();
        hn.putString("psource", psource);
        hn.putString("pdestination", pdestination);
        return hn;
    }

    public  Bundle getMyData2(){
        Bundle hm = new Bundle();
        hm.putString("Csource", csource);
        hm.putString("Cdestination", cdestination);
        return hm;
    }

}


