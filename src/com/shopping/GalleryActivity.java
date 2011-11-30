package com.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 30/11/11
 * Time: 18.54
 * To change this template use File | Settings | File Templates.
 */
public class GalleryActivity extends Activity {
    //Not a very good abstraction, but users are people out shopping.
    private ArrayList<User> activeUsers;
    public static final String ACTIVE_USERS = "active_users_const";
    private static Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The shopping activity viev runs in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get activity
        activeUsers = FetchActivityTask.getTestUsers(this);

        setContentView(R.layout.mygallery);

        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //listener for left home button, shopping cart
        Button lhome = (Button)findViewById(R.id.lhomebtn);
        lhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
                intent.putParcelableArrayListExtra(GalleryActivity.ACTIVE_USERS, activeUsers);
                startActivity(intent);
            }
        });

        //listener for left home button, shopping cart
        Button rhome = (Button)findViewById(R.id.rhomebtn);
        rhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(GalleryActivity.this, "Du er hjemme.", Toast.LENGTH_LONG).show();
            }
        });
        mContext = this;
    }

    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        private LayoutInflater mInflater;

        public ImageAdapter(Context c) {
            mContext = c;
            TypedArray attr = mContext.obtainStyledAttributes(R.styleable.com_shopping_GalleryActivity);
            mGalleryItemBackground = attr.getResourceId(
                    R.styleable.com_shopping_GalleryActivity_android_galleryItemBackground, 0);
            attr.recycle();
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public int getCount() {
            //Count is all users plus one icon for the group
            return activeUsers.size() + 1;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.mygalleryitem, parent, false);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.imageicon);
                holder.lbl = (TextView) convertView.findViewById(R.id.imagelabel);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            ImageView imageView = holder.img;
            TextView textView = holder.lbl;
            //  imageView.setImageResource(mImageIds[position]);
            if(position == activeUsers.size()){
                imageView.setBackgroundResource(R.drawable.dgroup);
                textView.setText("Venner");
            }
            else{
                imageView.setImageResource(R.drawable.duser);
                textView.setText(activeUsers.get(position).getFullName());
            }


            return convertView;
        }
    }
    class ViewHolder {
        ImageView img;
        TextView lbl;
    }

    public static Context getContext(){
        return mContext;
    }
}


//        ImageView imageView;
//        //reuse
//        if(convertView == null){
//            LinearLayout linearLayout = new LinearLayout(mContext);
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            linearLayout.setLayoutParams(p);
//            linearLayout.setOrientation(LinearLayout.VERTICAL);
//            //Some magic numbers here, it's the size of the icon.
//            //The multiply is because I've been scaling
//            int iconX = 276*2;
//            int iconY = 230*2;
//            imageView = new ImageView(mContext);
//
//
//            imageView.setLayoutParams(new Gallery.LayoutParams(iconX, iconY));
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            //     imageView.setBackgroundResource(mGalleryItemBackground);
//
//            TextView tv = new TextView(mContext);
//            tv.setLayoutParams(p);
//            tv.setText("NAME");
//
//            linearLayout.addView(imageView);
//            linearLayout.addView(tv);
//
//        } else{
//            imageView = (ImageView)convertView;
//        }
//
//        //  imageView.setImageResource(mImageIds[position]);
//        if(position == GROUP_ICON_POSITION)
//                imageView.setBackgroundResource(R.drawable.dgroup);
//        else
//                imageView.setImageResource(R.drawable.duser);
//
//        return imageView;
