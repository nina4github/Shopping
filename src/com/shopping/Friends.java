package com.shopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Friends extends Activity {
	private static final String TAG = "Friends";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.main);
        
        
        GridView gridview = (GridView) findViewById(R.id.statusgrid);
        gridview.setAdapter(new StatusAdapter(this));
        Log.d(TAG,"setted adapter");
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(Friends.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent frienddetails = new Intent(Friends.this,FriendDetails.class);
                
                frienddetails.putExtra("position", position);
                startActivity(frienddetails);
                Log.d(TAG, "Started Activity Friends Details");
            }
        });
       
	}
}
