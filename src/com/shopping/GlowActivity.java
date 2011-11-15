package com.shopping;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class GlowActivity extends Activity {
	private static final String TAG = "GLOW";

	LinearLayout screen;
	  
	public GlowActivity() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.glow);
        
		screen = (LinearLayout) findViewById(R.id.glowscreen);
		
		ImageView image = (ImageView) findViewById(R.id.glowImage);
		
		
		
//		for (int i = 0; i < 5; i++) {
//		
//			image.setImageResource(R.drawable.white1024x600);
//			image.setImageResource(R.drawable.black1024x600);
//		}
		
		Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.jump);
		for(int i=0; i<=5; i++){
			image.startAnimation(hyperspaceJump);
		}
		
		screen.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setContentView(R.layout.update);
				//finish();
			}
		});
		
		
//        for (int i = 0; i < 65535; i+=0.01) {
//            screen.setBackgroundColor(0xff000000 + i);
//        }
	}
}
