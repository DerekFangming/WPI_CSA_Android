package com.fmning.wpi_csa.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.fragments.LifeFragment;
import com.fmning.wpi_csa.helpers.BottomBarAdapter;
import com.fmning.wpi_csa.helpers.NoSwipePager;
import com.fmning.wpi_csa.http.OnJsonCompleteListener;
import com.fmning.wpi_csa.http.WCService;
import com.fmning.wpi_csa.http.WCUtils;

import org.json.JSONObject;

public class MainTabActivity extends AppCompatActivity {

    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        LifeFragment fragment = new LifeFragment().newInstance("hahahah", "jajajaj");

        pagerAdapter.addFragments(fragment);

        LifeFragment fragment1 = new LifeFragment().newInstance("hahahah1", "jajajaj");

        pagerAdapter.addFragments(fragment1);

        LifeFragment fragment2 = new LifeFragment().newInstance("hahahah2", "jajajaj");

        pagerAdapter.addFragments(fragment2);

        viewPager.setAdapter(pagerAdapter);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_life:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_sg:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_setting:
                        viewPager.setCurrentItem(2);
                        break;

                }
                return true;
            }
        });

        WCUtils.initSetup(this);

        WCService.checkSoftwareVersion("1.03.001", new OnJsonCompleteListener() {
            @Override
            public void onJsonComplete(JSONObject jsonObject) {
                try{
                    Log.d("haha", "Got responds 22 " + jsonObject.getString("status"));
                } catch(Exception e){}
            }
        });

        WCService.checkSoftwareVersion("1.00.001", new OnJsonCompleteListener() {
            @Override
            public void onJsonComplete(JSONObject jsonObject) {
                try{
                    Log.d("haha", "Got responds " + jsonObject.getString("status"));
                } catch(Exception e){}
            }
        });
    }
}
