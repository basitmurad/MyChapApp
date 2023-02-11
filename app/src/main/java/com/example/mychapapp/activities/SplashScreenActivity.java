package com.example.mychapapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mychapapp.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.apname.animate().translationY(-1400).setDuration(2700).setStartDelay(0);
        binding.lottie.animate().translationX(2000).setDuration(2000).setStartDelay(2900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,PhoneNumberActivity.class);
                startActivity(intent);

            }
        },5000);

    }
}