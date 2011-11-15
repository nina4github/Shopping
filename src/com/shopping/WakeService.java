package com.shopping;

import java.util.Iterator;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class WakeService extends Service {
	private static final String EB = "EventBus";
	private static final String TAG = "WakeService";

    ShoppingActivityView scv;
	
	PowerManager.WakeLock wl;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    public void start(ShoppingActivityView scv){
        this.scv = scv;
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Shopping events");


		startListener();
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// here start event bus
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Shopping events");
		
			
		startListener();
		
		// TODO Auto-generated method stub
		return  START_NOT_STICKY; 
	}
	
	private void startListener(){
		
		final String state = "state";
	    final String activity = "activity";
	    final String objectid = "objectid";
	    final String time_stamp = "timestamp";
		
	    EventBus eb = new EventBus("tiger.itu.dk", 8004);
        Log.d(EB,"EB initialiazed");
	    
	    Listener l = new Listener(new PatternBuilder()
        .addMatchAll(state)
        .add(activity,PatternOperator.EQ,"shopping")
        .addMatchAll(objectid)
        .addMatchAll(time_stamp)
        .getPattern()){
			
			@Override
			public void cleanUp() throws Exception {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onMessage(Map<String, Object> msg) {
				showScreen(msg);
				
			}
			public void onStarted() {};
		};
	    
		try{
			eb.start();
			Log.d(EB, "EB started");
			eb.addListener(l);
			Log.d(EB, "EB added listener");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showScreen(Map<String, Object> msg) {
        Iterator it = msg.entrySet().iterator();
        String activity = "";
        String objectId = "";
        while (it.hasNext()) {
           Map.Entry pairs = (Map.Entry)it.next();
           if(pairs.getKey().toString().equalsIgnoreCase("activity"))
               activity = pairs.getValue().toString();
           else if(pairs.getKey().toString().equalsIgnoreCase("objectid"))
               objectId = pairs.getValue().toString();
           it.remove(); // avoids a ConcurrentModificationException
        }
        if(objectId.equalsIgnoreCase("offer0"))
            scv.removeOffer();
        else if(objectId.equalsIgnoreCase("offer1"))
            scv.addOffer();

        else if(activity.equalsIgnoreCase("shopping")){
            if(objectId.equalsIgnoreCase("start"))
                scv.addShopper();
            else scv.removeShopper();
        }
		//wl.acquire(10000); // on for 10 seconds
		Intent start = new Intent(this,GlowActivity.class);
		start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(start);
		Log.d(EB, "EBListener started glow activity");
		//this.startActivity(new Intent(this, GlowActivity.class));
	}
}
