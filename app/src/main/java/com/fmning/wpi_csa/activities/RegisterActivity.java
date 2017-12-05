package com.fmning.wpi_csa.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.RegisterListAdapter;

/**
 * Created by fangmingning
 * On 12/4/17.
 */

public class RegisterActivity extends AppCompatActivity {

    RegisterListAdapter tableViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RecyclerView tableView = (RecyclerView) findViewById(R.id.registerList);

        tableViewAdapter = new RegisterListAdapter(this);

        tableView.setAdapter(tableViewAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}
