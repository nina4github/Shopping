package com.shopping;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.View;
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
 * Date: 27/11/11
 * Time: 14.42
*  Profile view for the group - shopping friends. Very similar to the ProfileActivity.class and the two
 *  may be subject merged in to some degree.
 */
public class GroupProfileActivity extends android.app.Activity {
    private User user;
    private ArrayList<User> shoppingFriends;
    private View previousView;
    public static String SELECTED_USER = "selected_user";
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group);
        Bundle bundle = getIntent().getExtras();
        //Here selected is just user of this device
        user = (User)bundle.getParcelable(ProfileActivity.SELECTED_USER);
        shoppingFriends = bundle.getParcelableArrayList(ProfileActivity.SHOPPING_FRIENDS);
        Drawable image = getResources().getDrawable(R.drawable.group);
        ImageView iv = (ImageView)findViewById(R.id.groupView);
        iv.setImageDrawable(image);
        TextView tv = (TextView)findViewById(R.id.groupTextView);
        String text = "Venner";
        tv.setText(text);

        //listener for left home button, shopping cart
        Button lhome = (Button)findViewById(R.id.grouplhomebtn);
        lhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timer.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, HomeActivity.class);
                intent.putParcelableArrayListExtra(GalleryActivity.ACTIVE_USERS, shoppingFriends);
                startActivity(intent);
                finish();
            }
        });

        //listener for left home button, shopping cart
        Button rhome = (Button)findViewById(R.id.grouprhomebtn);
        rhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timer.cancel();
                finish();
            }
        });

        setupButtonListeners();
        restartTimer();
    }


    //who is shopping
    private void setProfileStatus(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.groupStatusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //  params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ImageView iv;
        int x = 0;
        for(User u : shoppingFriends){
            iv = new ImageView(GalleryActivity.getContext());
            iv.setLayoutParams(params);
            Drawable d = getUserImageDrawable(u.getImageUrl());
            iv.setImageDrawable(d);
            iv.setTranslationX(x);
            statusLayout.addView(iv);

            //Add shopping cart if shopping. This is the cart image underneath the user image.
            if(u.getUserActivity()==UserActivity.Shopping){
                iv = new ImageView(GalleryActivity.getContext());
                iv.setLayoutParams(params);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.cart));
                iv.setTranslationX(x);
                iv.setTranslationY(d.getIntrinsicHeight() -25);//25 is magic number for distance to user image
                statusLayout.addView(iv);
            }
            x+=d.getIntrinsicWidth() +20; //next image is placed at width of current image plus some spacing
        }

        //   statusLayout.addView(statusText);
    }

    //THE four/three button operations in the top
    private void setWeekOverview(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.groupStatusLayout);
//        ArrayList<ArrayList<Movable>> weekActivity = FetchActivityTask.getWeekActivity(HomeActivity.USER_ID);
//        populateWeekView(weekActivity);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);
    }


    private void populateWeekView(ArrayList<ArrayList<Movable>> weekActivity) {

    }

    private void setSparks(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.groupStatusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);

    }

    private void setShoppingStats(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.groupStatusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);
    }


    private void setupButtonListeners() {
        Button b = (Button)findViewById(R.id.groupBtn1);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                restartTimer();
                setProfileStatus( view);
            }
        });
        //Initialize with the profile view set
        setProfileStatus(b);

        b = (Button)findViewById(R.id.groupBtn2);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                restartTimer();
                setWeekOverview(view);
            }
        });

        b = (Button)findViewById(R.id.groupBtn3);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                restartTimer();
                setSparks( view);
            }
        });
//flest indkob
//        b = (Button)findViewById(R.id.groupBtn4);
//        b.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view) {
//                restartTimer();
//                setShoppingStats( view);
//            }
//        });
    }

    //Swap button background - which one is highlighted
    private void updateButtonColor(View newButton){
        //Update button row
        if(previousView != null)
            previousView.setBackgroundColor(getResources().getColor(R.color.white));
        newButton.setBackgroundColor(getResources().getColor(R.color.myblue));
        previousView = newButton;
    }

    private void restartTimer(){
        if(timer!=null)timer.cancel();
        timer = new Timer("sleeptime");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(GroupProfileActivity.this, HomeActivity.class);
                ArrayList<User> objs = shoppingFriends;
                objs.addAll(FetchActivityTask.getObjectsActivity(HomeActivity.USER_ID));
                intent.putParcelableArrayListExtra(GalleryActivity.ACTIVE_USERS, objs);
                startActivity(intent);
            }
        },GalleryActivity.SLEEP_DELAY);
    }


    //Get image from server
    private Drawable getUserImageDrawable(String url){
        Drawable image = ImageOperations(this, url, "image.jpg");
        return image;
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

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
