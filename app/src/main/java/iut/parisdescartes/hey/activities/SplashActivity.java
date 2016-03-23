package iut.parisdescartes.hey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import iut.parisdescartes.hey.activities.MainActivity;

/**
 * Created by Kévin on 03/03/2016.
 */
public class SplashActivity extends AppCompatActivity{

    private static final int SPLASH_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ConnectionActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME);
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();*/
    }
}
