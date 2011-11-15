package com.shopping;

import android.widget.*;
import com.shopping.HorizontalListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ShoppingOverview extends Activity {

    private ArrayList<Movable> dataObjects2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview);

        HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
        listview.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();

        dataObjects2 = new ArrayList<Movable>();
        ArrayList<Movable> a = bundle.getParcelableArrayList("carts");
        ArrayList<Movable> b = bundle.getParcelableArrayList("offers");
        if(a!=null)
           dataObjects2.addAll(a);
        if(b!=null)
        dataObjects2.addAll(b);

    }

    	private static String[] dataObjects = new String[]{ "Text #1",
		"Text #2",
		"Text #3" };

    private BaseAdapter mAdapter = new BaseAdapter() {

        private OnClickListener mOnButtonClicked = new OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingOverview.this);
                builder.setMessage("hello from " + v);
                builder.setPositiveButton("Cool", null);
                builder.show();

            }
        };

        public int getCount() {
            return dataObjects2.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            TextView title = (TextView) retval.findViewById(R.id.title);
            Button button = (Button) retval.findViewById(R.id.clickbutton);
            ImageView iv = (ImageView) retval.findViewById(R.id.image);
            button.setOnClickListener(mOnButtonClicked);
            title.setText(dataObjects2.get(position).toString());
            iv.setImageBitmap(dataObjects2.get(position).getBitmap());

            return retval;
        }

    };

}
