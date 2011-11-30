/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Comment
 * Date: 14/11/11
 * Time: 15.29
 */

package com.shopping;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

interface MyInterruptHandler {
    public abstract void myInterrupt();
}

public class HomeActivity extends Activity implements MyInterruptHandler {
    public static final String USER_ID = "user01";
        public static final String ACTIVE_USERS = "active_users_constant";
    public static final String CONTACTS = "all_shopping_contacts_for_user";
    private HomeActivityView shoppingHomeHomeActivityView;
    private BroadcastReceiver receiver;
    boolean interrupted;

    //Not a very good abstraction, but users are people out shopping.
    private ArrayList<User> activeUsers;
    //With no persistent data in place we keep all contacts
    private ArrayList<User> contacts;
    private static Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        interrupted = false;
        //The shopping activity viev runs in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.animationview);
        shoppingHomeHomeActivityView = (HomeActivityView) findViewById(R.id.shoppingActivityView);
        Bundle bundle = getIntent().getExtras();
        activeUsers = bundle.getParcelableArrayList(GalleryActivity.ACTIVE_USERS);
        getContacts();
//        getShoppingActivity();
        for(User u : activeUsers)
         Log.d("Active CONTACT", "" + u.getUserId());

        shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
        shoppingHomeHomeActivityView.update();
        shoppingHomeHomeActivityView.setMyInterruptHandler(this);


        startService(new Intent(this, WakeService.class));
        showShoppingActivity();
    }

    private void getContacts() {
        if(contacts != null)
            contacts.clear();
        contacts = FetchActivityTask.getContacts();
    }

    private void showShoppingActivity() {
        shoppingHomeHomeActivityView.clear();
        for(User u : activeUsers){
            ShoppingCart sc = new ShoppingCart(this);
            sc.setId(u.getUserId());
            shoppingHomeHomeActivityView.addShopper(sc);
            for(Movable so : u.getOffers()){
                shoppingHomeHomeActivityView.addOffer(so);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceBundle){
        super.onRestoreInstanceState(savedInstanceBundle);
        interrupted = false;
        Log.d("shopping activity", "onRestoreInstanceState");
        shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
        shoppingHomeHomeActivityView.update();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        interrupted = false;
        Log.d("shopping activity", "onRestart");
    }

    @Override
    public void onResume(){
        super.onResume();
        interrupted = false;
        receiver = new ShoppingReceiver();
        registerReceiver(receiver, new IntentFilter(WakeService.NEW_SHOPPING_ACTIVITY));
        shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
        shoppingHomeHomeActivityView.update();
        Log.d("shopping activity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        interrupted = false;
        Log.d("shopping activity", "onPause");
        //      shoppingActivity.setMode(HomeActivityView.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        interrupted = false;
        unregisterReceiver(receiver);
        Log.d("shopping activity", "onStop");
        //      shoppingActivity.setMode(HomeActivityView.PAUSE);
    }
       int i = 0;
    public void myInterrupt() {
        i++;
        if(!interrupted){
            interrupted = true;
        Log.d("INTENT FIRESD", "" + i);
        Intent intent = new Intent(HomeActivity.this, ShoppingOverview.class);
        intent.putParcelableArrayListExtra(HomeActivity.ACTIVE_USERS, activeUsers);
        intent.putParcelableArrayListExtra(HomeActivity.CONTACTS, contacts);
        startActivity(intent);
        }
    }

    /**
     * Broadcast receiver to get notifications of new shopping activity from the service.
     */
    private class ShoppingReceiver extends BroadcastReceiver{
        int testid = 1;
        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getStringExtra(WakeService.ACTIVITY);
            String content  =  intent.getStringExtra(WakeService.CONTENT);
            String userName =  intent.getStringExtra(WakeService.USER_NAME);

            if(activity.equalsIgnoreCase("shopping"))
                updateShoppingActivity(content, userName);

            else if(activity.equalsIgnoreCase("offer123")){
                 ShoppingOffer so = new ShoppingOffer(HomeActivity.this);
                so.setId(testid);
                shoppingHomeHomeActivityView.addOffer(so);
            }
        }

        private void updateShoppingActivity(String content, String userName) {
            if(content.equalsIgnoreCase("start")){
                //TODO We need id for this user?!
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification();
                notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                nm.notify(1, notification);

                User u = new User();
                u.setFullName(userName);
                u.setUserId(123);
                u.setUserActivity(UserActivity.Shopping);

                ShoppingCart sc = new ShoppingCart(HomeActivity.this);
                sc.setId(testid);
                shoppingHomeHomeActivityView.addShopper(sc);
            }else if(content.equalsIgnoreCase("stop")){
                for(User u : activeUsers){
                    if(u.getFullName().endsWith(userName))
                        activeUsers.remove(u);
                }
                shoppingHomeHomeActivityView.removeShopper();
            }
        }
    }

    public static Context getContext(){
        return mContext;
    }

}
