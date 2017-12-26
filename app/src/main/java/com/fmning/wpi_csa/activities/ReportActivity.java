package com.fmning.wpi_csa.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.fmning.wpi_csa.R;

/**                                                                                                     WD
 * Created by Fangming
 * On 12/25/2017.
 */

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        LinearLayout reportView = (LinearLayout)findViewById(R.id.reportView);
        View v = findViewById(R.id.theView);
        reportView.removeView(v);

        //v.add

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}
