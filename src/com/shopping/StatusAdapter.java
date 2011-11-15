package com.shopping;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {
	private static final String TAG = "StatusAdapter";
	private Context mContext;

	public int getCount() {
		return mThumbIds.length;
	}

	public StatusAdapter(Context c) {
		mContext = c;
		Log.d(TAG,"into status adapter");
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ImageView imageView;
		TextView textView;

		
		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			
			LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = layout.inflate(R.layout.facestatus, null);
			v.setLayoutParams(new GridView.LayoutParams(400, 120));
			
			imageView = (ImageView) v.findViewById(R.id.friendimage);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			//imageView.setPadding(5, 5, 5, 5);
			
			textView = (TextView) v.findViewById(R.id.friendstatus);
			//textView.setTextSize(20);
			textView.setEnabled(true);
			//textView.setPadding(5, 5, 5, 5);
			textView.setVisibility(View.VISIBLE);
		} else {
			imageView = (ImageView) v.findViewById(R.id.friendimage);
			textView   = (TextView) v.findViewById(R.id.friendstatus);
			
		}

		imageView.setImageResource(mThumbIds[position]);
		textView.setText(statusTextSample[position]);
	

		return v;
	}

	private Integer[] mThumbIds = {
			R.drawable.senior1_80px, // 3,4,6 are males
			R.drawable.senior2_80px, R.drawable.senior3_80px,
			R.drawable.senior4_80px, R.drawable.senior5_80px,
			R.drawable.senior6_80px };

	private String[] statusTextSample = { "Lize er p� indk�b nu.",
			"Marie var p� indk�b i g�r til 11.00.",
			"Peter var p� indk�b i g�r til 14.00.",
			"Jonas delt et tilbud fra Fakta 1 time siden.",
			"Elsemarie var p� indk�b i morges til 10.30",
			"Jens var p� indk�b i morges til 11.30"
			};
}
