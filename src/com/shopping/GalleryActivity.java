package com.shopping;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 30/11/11
 * Time: 18.54
 * To change this template use File | Settings | File Templates.
 */
public class GalleryActivity extends Activity {
    //Not a very good abstraction, but users are people out shopping.
    private ArrayList<User> shoppingFriends;
    public static final String ACTIVE_USERS = "active_users_const";
    private static Context mContext;
    private Timer timer;
    public static final long SLEEP_DELAY = 1000 * 60 * 2; //milliseconds

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchActivityTask.getAllOffersForUser("user01");
        //The  activity viev runs in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get activity, boolean for including self.
        shoppingFriends = FetchActivityTask.getContactsForUser(true, "user01");
        FetchActivityTask.setUserActivity(shoppingFriends, "user01");

        setContentView(R.layout.mygallery);

        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                timer.cancel();
                //Check if it is the group
                if(position==shoppingFriends.size()){
                    Intent intent = new Intent(GalleryActivity.this, GroupProfileActivity.class);
                    //This user
                    intent.putExtra(ProfileActivity.SELECTED_USER, shoppingFriends.get(0));
                    //Friends
                    intent.putExtra(ProfileActivity.SHOPPING_FRIENDS, shoppingFriends);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(GalleryActivity.this, ProfileActivity.class);
                    //Selected user
                    intent.putExtra(ProfileActivity.SELECTED_USER, shoppingFriends.get(position));
                    //Friends
                    intent.putExtra(ProfileActivity.SHOPPING_FRIENDS, shoppingFriends);
                    startActivity(intent);
                }
            }
        });

        //listener for left home button, shopping cart
        Button lhome = (Button)findViewById(R.id.lhomebtn);
        lhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timer.cancel();
                FetchActivityTask.setUserActivity(shoppingFriends, HomeActivity.USER_ID);
                Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
                shoppingFriends.remove(0);
                intent.putParcelableArrayListExtra(GalleryActivity.ACTIVE_USERS, shoppingFriends);
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
        restartTimer();
        startService(new Intent(this, WakeService.class));

        ArrayList<User> shoppingFriends1 = FetchActivityTask.getContactsForUser(true, "user01");
        FetchActivityTask.setUserActivity(shoppingFriends1, "user01");
        for(User u : shoppingFriends1){
        if (u.getUserActivity() == UserActivity.Shopping)
            Toast.makeText(this, u.getFirstName() + " is shopping.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get activity
        FetchActivityTask.setUserActivity(shoppingFriends, HomeActivity.USER_ID);
    }

    private void restartTimer(){
        if(timer!=null)timer.cancel();
        timer = new Timer("sleeptime");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
                shoppingFriends.remove(0);
                intent.putParcelableArrayListExtra(GalleryActivity.ACTIVE_USERS, shoppingFriends);
                startActivity(intent);
            }
        },GalleryActivity.SLEEP_DELAY);
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
            return shoppingFriends.size() + 1;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            restartTimer();
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
            if(position == shoppingFriends.size()){
                imageView.setBackgroundResource(R.drawable.dgroup);
                textView.setText("Venner");
            }
            else{
                String name = position == 0 ? "Min profil" : shoppingFriends.get(position).getFirstName();
                if(shoppingFriends.get(position).getUserActivity() == UserActivity.Shopping){
                    imageView.setImageResource(R.drawable.dshopuser);
                    textView.setText(name);
                }else{
                    imageView.setImageResource(R.drawable.duser);
                    textView.setText(name);
                }

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

