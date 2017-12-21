package com.fmning.wpi_csa.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.BottomBarAdapter;
import com.fmning.wpi_csa.adapters.NoSwipePager;
import com.fmning.wpi_csa.fragments.LifeFragment;
import com.fmning.wpi_csa.fragments.SGFragment;
import com.fmning.wpi_csa.fragments.SettingFragment;
import com.fmning.wpi_csa.helpers.Utils;

public class MainTabActivity extends AppCompatActivity {

    private NoSwipePager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        DisplayMetrics window = getResources().getDisplayMetrics();
        Utils.padding15 = (int)(window.density * 15);
        Utils.paddingFullWidth = window.widthPixels;

        viewPager = (NoSwipePager) findViewById(R.id.mainTabPager);
        viewPager.setPagingEnabled(false);
        BottomBarAdapter pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        LifeFragment lifeFragment = new LifeFragment();
        pagerAdapter.addFragments(lifeFragment);
        SGFragment fragment1 = new SGFragment();
        pagerAdapter.addFragments(fragment1);
        SettingFragment settingFragment = new SettingFragment();
        pagerAdapter.addFragments(settingFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionLife:
                        viewPager.setCurrentItem(0, false);

                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                        break;
                    case R.id.actionSG:
                        viewPager.setCurrentItem(1, false);

                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


                        break;
                    case R.id.actionSetting:
                        viewPager.setCurrentItem(2, false);

                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                        break;

                }
                return true;
            }
        });

    }

}
