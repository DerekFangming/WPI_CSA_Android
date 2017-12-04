package com.fmning.wpi_csa.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.BottomBarAdapter;
import com.fmning.wpi_csa.adapters.NoSwipePager;
import com.fmning.wpi_csa.fragments.LifeFragment;
import com.fmning.wpi_csa.fragments.SGFragment;
import com.fmning.wpi_csa.fragments.SettingFragment;

public class MainTabActivity extends AppCompatActivity {

    private NoSwipePager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        viewPager = (NoSwipePager) findViewById(R.id.mainTabPager);
        viewPager.setPagingEnabled(false);
        BottomBarAdapter pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        LifeFragment lifeFragment = new LifeFragment();//.newInstance("hahahah", "jajajaj");

        pagerAdapter.addFragments(lifeFragment);

        SGFragment fragment1 = new SGFragment();

        pagerAdapter.addFragments(fragment1);

        SettingFragment settingFragment = new SettingFragment();

        pagerAdapter.addFragments(settingFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionLife:
                        viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.actionSG:
                        viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.actionSetting:
                        viewPager.setCurrentItem(2, false);
                        break;

                }
                return true;
            }
        });

//        WCUtils.initSetup(this);
//
//        WCService.checkSoftwareVersion(this, "1.03.001", new WCService.OnCheckSoftwareVersionListener() {
//            @Override
//            public void OnCheckSoftwareVersionDone(String status, String title, String msg, String updates, String version) {
//                Utils.logMsg(status);
//            }
//        });



    }

}
