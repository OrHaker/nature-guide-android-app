package com.example.natureguide;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //create animation
        ImageView splashImage = findViewById(R.id.splashScreenImage);
        splashImage.animate().rotationBy(360).setDuration(DELAY_TIME);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //move to new activity in 2 seconds
                startActivity(new Intent(SplashScreen.this,LoginPage.class));
                SplashScreen.this.finish();
            }
        },DELAY_TIME);
    }
}
