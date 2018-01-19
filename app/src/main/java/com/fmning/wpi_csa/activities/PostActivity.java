package com.fmning.wpi_csa.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fmning.wpi_csa.R;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Button theButton = (Button) findViewById(R.id.testB);

        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(PostActivity.this, WritePost.class);
                //myIntent.putExtra("key", "from main"); //Optional parameters
                PostActivity.this.startActivity(myIntent);
            }
        });
    }
}
