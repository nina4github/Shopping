package com.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.eclipse.jetty.util.resource.Resource;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 01/12/11
 * Time: 22.46
 * To change this template use File | Settings | File Templates.
 */
public class ActivityOverview extends android.app.Activity {
    private ArrayList<User> shoppingFriends;
    private int objectsForGallery=0;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activityoverview);
        shoppingFriends = getIntent().getExtras().getParcelableArrayList(HomeActivity.ACTIVE_USERS);

        /**
         * For our gallery we will show all all objects being users and offers shared by these
         */
        for(User u : shoppingFriends){
            objectsForGallery++;
            objectsForGallery += u.getOffers().size();
        }

        Gallery gallery = (Gallery) findViewById(R.id.overviewgallery);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                setDetailViewForObject(position);
            }
        });

        //listener for left home button, shopping cart
        Button lhome = (Button)findViewById(R.id.lhomebtn);
        lhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();//We only get here from activity stacked underneath
            }
        });

        //listener for left home button, shopping cart
        Button rhome = (Button)findViewById(R.id.rhomebtn);
        rhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ActivityOverview.this, GalleryActivity.class);
                startActivity(intent);
            }
        });
        setDetailViewForObject(0);
        restartTimer();
    }

    private void restartTimer(){
        if(timer!=null)timer.cancel();
        timer = new Timer("sleeptime");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, GalleryActivity.SLEEP_DELAY);

    }

    /**
     * An item clicked in the gallery. Set the object in the detail view.
     * @param position
     */
    private void setDetailViewForObject(int position) {
        int index=0;
        boolean found=false;
        for(User u : shoppingFriends){
            if(position==index){
                TextView tv = (TextView)findViewById(R.id.activityoverviewtext);
                tv.setText(u.getFirstName() + " er på indkøb i " + u.getLocation());
                tv.setTextSize(50.0f);

                ImageView iv = (ImageView)findViewById(R.id.activityoverviewimage);
                iv.setImageBitmap(null);
                iv.invalidate();
                break;
            }
            index++;
            for(Movable m : u.getOffers()){
                if(index==position){
                    Drawable image = ImageOperations(this, m.getAltImageUrl(), "image.jpg");
                    ImageView iv = (ImageView)findViewById(R.id.activityoverviewimage);
                    if(image != null)
                        iv.setImageDrawable(image);
                    else iv.setImageBitmap(m.getBitmap());

                    TextView tv = (TextView)findViewById(R.id.activityoverviewtext);
                    tv.setText(u.getFullName() + " har delt dette tilbud");
                    tv.setTextSize(40.0f);
                    found=true;
                    break;
                }
                index++;
            }
            if(found)break;
        }
    }

    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
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
            return objectsForGallery;
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
                convertView = inflater.inflate(R.layout.mysmallgalleryitem, parent, false);

                holder = new ViewHolder();
                holder.img = (ImageView)convertView.findViewById(R.id.smallimageicon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ImageView imageView = holder.img;
            imageView.setImageBitmap(getBitMapForObject(position));

            return convertView;
        }

        /**
         * Get the picture for the position, being, e.g. user, offer, offer, user, user, offer etc.
         * @param position
         * @return
         */
        private Bitmap getBitMapForObject(int position) {
            //Index is relative to number of users and there respective offers
            int index=0;
            Bitmap bitmap=null;
            for(User u : shoppingFriends){
                if(position==index){
                    bitmap = BitmapFactory.decodeResource(GalleryActivity.getContext().getResources(), R.drawable.cart);
                    break;
                }
                index++;
                for(Movable m : u.getOffers()){
                    if(index==position){
                        bitmap=m.getBitmap();
                        break;
                    }
                    index++;
                }
                if(bitmap!=null)break;
            }
            Log.d("TJEK", bitmap.toString());
            return bitmap;
        }
    }

    class ViewHolder {
        ImageView img;
    }
}
