package com.esys.leopardimpdemoapp;

import com.esys.leopardimpdemoapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
public class Act_SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	ScaleAnimation scale;
	ImageView img_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		 img_view =(ImageView)findViewById(R.id.imgLogo);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(Act_SplashScreen.this, Act_Main.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
	@Override
	 protected void onResume() {
	  super.onResume();
	  Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
	  img_view.startAnimation(animAlpha);
	 }
}
