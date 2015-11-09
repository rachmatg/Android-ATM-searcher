package com.cdvdev.atmsearcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cdvdev.atmsearcher.BuildConfig;
import com.cdvdev.atmsearcher.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView appVersion = (TextView) findViewById(R.id.text_version);
        if (appVersion != null) {
            String version = getResources().getString(R.string.label_app_version) + " " +  BuildConfig.VERSION_NAME;
            appVersion.setText(version);
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                1500
        );
    }
}
