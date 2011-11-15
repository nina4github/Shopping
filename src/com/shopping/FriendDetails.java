package com.shopping;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendDetails extends Activity {
	
	ImageView friendImageView;
	TextView statusTextView; 
	ImageButton tilbudImage;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		int position = getIntent().getExtras().getInt("position");
		Log.d("position","the position is: "+position);
		
		setContentView(R.layout.frienddetails);
		
		friendImageView = (ImageView) findViewById(R.id.friendimage);
		statusTextView = (TextView) findViewById(R.id.friendstatus);
		friendImageView.setImageResource(mThumbIds[position]);
		statusTextView.setText(statusTextIds[position]);
		
		tilbudImage = (ImageButton) findViewById(R.id.tilbudImage);
		tilbudImage.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			tilbudImage.setMinimumHeight(500);
			tilbudImage.setMinimumWidth(500);
			
		}
	});
		
		
	}
	
	private Integer[] mThumbIds = {
			R.drawable.senior1_80px, // 3,4,6 are males
			R.drawable.senior2_80px, R.drawable.senior3_80px,
			R.drawable.senior4_80px, R.drawable.senior5_80px,
			R.drawable.senior6_80px };

	private Integer[] statusTextIds ={
		R.string.senior1_status,
		R.string.senior2_status,
		R.string.senior3_status,
		R.string.senior4_status,
		R.string.senior5_status,
		R.string.senior6_status
	};
	
	
}
