/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Comment
 * Date: 14/11/11
 * Time: 15.29
 * Activity for the animation view which is composed onto this class.
 *
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
import android.test.InstrumentationTestRunner;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

//For when animation screen is tapped.
interface MyInterruptHandler {
    public abstract void myInterrupt();
}

public class HomeActivity extends Activity implements MyInterruptHandler {
    public static final String USER_ID = "user01"; //User of this particular device
    public static final String ACTIVE_USERS = "active_users_constant";
    public static final String CONTACTS = "all_shopping_contacts_for_user";
    //Animation view
    private HomeActivityView shoppingHomeHomeActivityView;
    private BroadcastReceiver receiver;
    boolean interrupted;

    //Not a very good abstraction, but users are people out shopping.
    //Sadly this is a mixture of persons and objects like rollators - very bad name...
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
        //Again sending around the data we need, as we have no persistance
        activeUsers = bundle.getParcelableArrayList(GalleryActivity.ACTIVE_USERS);
        getContacts();
//        getShoppingActivity();
        for(User u : activeUsers)
            Log.d("Active CONTACT", "" + u.getUserId());

        shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
        shoppingHomeHomeActivityView.update();
        shoppingHomeHomeActivityView.setMyInterruptHandler(this);

        showShoppingActivity();
        startService(new Intent(this, WakeService.class));
    }

    private void getContacts() {
        if(contacts != null)
            contacts.clear();
        contacts = FetchActivityTask.getContactsForUser(false, HomeActivity.USER_ID, "person");
        contacts.addAll(FetchActivityTask.getContactsForUser(false, HomeActivity.USER_ID, "thing"));
    }

    //Updates the animaiton view with users (and objects) that are shopping
    private void showShoppingActivity() {
        shoppingHomeHomeActivityView.clear();
        for(User u : activeUsers){
            if(u.getUserActivity() == UserActivity.Shopping){
                ShoppingCart sc = new ShoppingCart(this);
                sc.setId(u.getUserId());
                shoppingHomeHomeActivityView.addShopper(sc, false);
                for(Movable so : u.getOffers()){ //Movables are ShoppingOffer objects. Had problems with parceling these
                    shoppingHomeHomeActivityView.addOffer(so, false);
                }
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
            Log.d("INTENT FIRESD", "" + i); //Leftover from problems with interrupt being fired several times
            Intent intent = new Intent(HomeActivity.this, ActivityOverview.class);
            ArrayList<User> shoppers = new ArrayList<User>();
            for(User u : activeUsers){
                if(u.getUserActivity()==UserActivity.Shopping)
                    shoppers.add(u);
            }
            intent.putParcelableArrayListExtra(HomeActivity.ACTIVE_USERS, shoppers);
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
            String actor =  intent.getStringExtra(WakeService.ACTOR);

            //Not a very good way of making distinction between messages, and won't scale well with more message types.
            //1 shopping star | stop
            if(activity.equalsIgnoreCase("shopping") &&
                    (content.equalsIgnoreCase("start") ||
                    content.equalsIgnoreCase("stop"))){
                updateShoppingActivity(content, Integer.parseInt(actor));

            //Shopping offer is activity "shopping" but with url as content
            } else if(activity.equalsIgnoreCase("shopping")){
                ShoppingOffer so = new ShoppingOffer(HomeActivity.this);
                so.setAltImageUrl(content);
                so.setId(Integer.parseInt(actor));
                for(User u : activeUsers){
                    if(u.getUserId()==Integer.parseInt(actor)){
                        u.addOffer(so);
                        break;
                    }
                }
                shoppingHomeHomeActivityView.addOffer(so, true);
            }
            //A user is at at certain place. This is e.g. Actor=Fakta and content is now the userId
            //We update this users location property "where is he"
            else {
                for(User u : activeUsers){
                    if(u.getUserId()==Integer.parseInt(content)){
                        u.setLocation(actor);
                        break;
                    }
                }
            }
        }

        private void updateShoppingActivity(String content, int userId) {
            if(content.equalsIgnoreCase("start")){
                User user = null;
                for(User u : activeUsers){
                    if(u.getUserId()==userId){
                        user=u;
                    }
                }

                //return if user is not know or already displayed
                //I often get several similar messages from genie hub
                if(user==null || shoppingHomeHomeActivityView.isUserDisplayed(userId))return;

                //Bells and whistles
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification();
                notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                nm.notify(1, notification);

                user.setUserActivity(UserActivity.Shopping);

                //We put shopping carts on the view for object on shopping
                ShoppingCart sc = new ShoppingCart(HomeActivity.this);
                sc.setId(userId);
                shoppingHomeHomeActivityView.addShopper(sc, true);//true for flashing some color
            }else if(content.equalsIgnoreCase("stop")){
                User user = null;
                for(User u : activeUsers){
                    if(u.getUserId()==userId){
                        user=u;
                    }
                }
                if(user==null)return;
                user.setUserActivity(UserActivity.Unknown);
                shoppingHomeHomeActivityView.removeShopper(userId);
            }
        }
    }
    //Std way of keeping a context ref.
    public static Context getContext(){
        return mContext;
    }

}
