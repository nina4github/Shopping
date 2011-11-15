package com.shopping;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SoundEffectConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;

interface InterruptHandler{
    public abstract void interrupt();
}

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 15.29
 */
public class ShoppingActivity extends Activity implements InterruptHandler{
    private ShoppingActivityView shoppingActivityView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shopping_activity);

        shoppingActivityView = (ShoppingActivityView) findViewById(R.id.cart);
        shoppingActivityView.setState(ShoppingActivityView.VISIBLE);
        shoppingActivityView.update();
        shoppingActivityView.setInterruptHandler(this);

        startService(new Intent(this, WakeService.class));


//        //First launch
//        if (savedInstanceState == null) {
//            shoppingActivity.setMode(ShoppingActivityView.READY);
//        } else {
//            // We are being restored
//            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
//            if (map != null) {
//                shoppingActivity.restoreState(map);
//            } else {
//                shoppingActivity.setMode(ShoppingActivityView.PAUSE);
//            }
//        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceBundle){
        super.onRestoreInstanceState(savedInstanceBundle);
         Log.d("shopping activity", "onRestoreInstanceState");
        shoppingActivityView.setState(ShoppingActivityView.VISIBLE);
        shoppingActivityView.update();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.d("shopping activity", "onRestart");
    }

    @Override
    public void onResume(){
        super.onResume();
        shoppingActivityView.setState(ShoppingActivityView.VISIBLE);
        shoppingActivityView.update();
        Log.d("shopping activity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("shopping activity", "onPause");
        //      shoppingActivity.setMode(ShoppingActivityView.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("shopping activity", "onStop");
        //      shoppingActivity.setMode(ShoppingActivityView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        //      outState.putBundle(ICICLE_KEY, shoppingActivity.saveState());
    }

    public void interrupt() {
        Intent intent = new Intent(ShoppingActivity.this, ShoppingOverview.class);
        ArrayList<Movable> a = new ArrayList<Movable>(shoppingActivityView.getCarts().subList(1, shoppingActivityView.getCarts().size()
        ));
        intent.putParcelableArrayListExtra("carts", a);
        intent.putParcelableArrayListExtra("offers", shoppingActivityView.getOffers());
        startActivity(intent);
        Log.d("hej", "Started ahoppingoverview ");
    }
}
