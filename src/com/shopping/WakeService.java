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
    public static final String CONTENT_URI = "idea.itu.dk.content.stuff";
    public static final String NEW_SHOPPING_ACTIVITY = "New_Shopping_Activity";
    public static final String ACTIVITY = "activity";
    public static final String CONTENT = "content";
    public static final String USER_ID = "user_id";

	private static final String EB = "EventBus";
	private static final String TAG = "WakeService";



    @Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startListener();
		
		// TODO Auto-generated method stub
		return  START_NOT_STICKY; 
	}
	
	private void startListener(){

        final String activity = "activity";
		final String actor = "actor";
	    final String content = "content";
	    final String timestamp = "timestamp";

		
	    EventBus eb = new EventBus("tiger.itu.dk", 8004);
        Log.d(EB,"EB initialiazed");
	    
	    Listener l = new Listener(new PatternBuilder()
        .addMatchAll(activity)
        .addMatchAll(actor)
        .addMatchAll(content)
        .addMatchAll(timestamp)
        .getPattern()){
			
			@Override
			public void cleanUp() throws Exception {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onMessage(Map<String, Object> msg) {
				handleMessage(msg);
				
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

    /**
     * Routine for unwrapping the contents of the received message.
     * @param msg
     */
	private void handleMessage(Map<String, Object> msg) {
        Log.i(WakeService.class.getName(), "MESSAGE RECEIVED");
        Iterator it = msg.entrySet().iterator();
        String activity = "";
        String content = "";
        int userId = 0;
        while (it.hasNext()) {
           Map.Entry pairs = (Map.Entry)it.next();
           if(pairs.getKey().toString().equalsIgnoreCase("activity"))
               activity = pairs.getValue().toString();
           else if(pairs.getKey().toString().equalsIgnoreCase("content"))
               content = pairs.getValue().toString();
            else if(pairs.getKey().toString().equalsIgnoreCase("actor"))
               userId = Integer.parseInt(pairs.getValue().toString());
           it.remove(); // avoids a ConcurrentModificationException
        }
        announceNewShoppingActivity(activity, content, userId);
	}

    /**
     * Broadcast a new shopping activity.
     */
    private void announceNewShoppingActivity(String activity, String content, int userId) {
        Intent intent = new Intent(NEW_SHOPPING_ACTIVITY);
        intent.putExtra(ACTIVITY, activity);
        intent.putExtra(CONTENT, content);
        intent.putExtra(USER_ID, userId);
        sendBroadcast(intent);
    }


}
