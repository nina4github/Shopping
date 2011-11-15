package com.shopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class EMShopping extends Activity {
	private static final String TAG = "EMShopping";

	Button friendB;
	Button offerB;
	Button communityB;
	Button glowB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.intro);

		friendB = (Button) findViewById(R.id.friendButton);
		offerB = (Button) findViewById(R.id.offerButton);
		communityB = (Button) findViewById(R.id.communityButton);
		glowB = (Button) findViewById(R.id.glowButton);

		friendB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//                Animation a;
//                a = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
//                friendB.startAnimation(a);

//				Intent friends = new Intent(EMShopping.this, Friends.class);
//				startActivity(friends);
//				Log.d(TAG, "Started Activity Friends ");

			}
		});

		offerB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(EMShopping.this, Offer.class);
				startActivity(intent);
				Log.d(TAG, "Started Activity Offer ");
			}
		});

		communityB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(EMShopping.this, Community.class);
				startActivity(intent);
				Log.d(TAG, "Started Activity Community ");
			}
		});

		glowB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(EMShopping.this, GlowActivity.class);
				startActivity(intent);
				Log.d(TAG, "Started Activity Glow ");

			}
		});
		startService(new Intent(this, WakeService.class));
		Log.d(TAG, "Started Service WakeService");
	}
}