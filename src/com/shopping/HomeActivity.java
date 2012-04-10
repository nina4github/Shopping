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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

//For when animation screen is tapped.
interface MyInterruptHandler {
	public abstract void myInterrupt();
}

public class HomeActivity extends Activity implements MyInterruptHandler,
		SensorEventListener {
	public static final String USER_ID = "user01"; // User of this particular
	// device
	public static final String ACTIVE_USERS = "active_users_constant";
	public static final String CONTACTS = "all_shopping_contacts_for_user";
	public static final String TEST_OFFER_URL = "http://idea.itu.dk:3000/uploads/images/scaled_full_fb79b5fef393d17fc2c5.jpg";
	public static final String TAG = "Home Activity";
	public static final String ACTIVITY = "shopping";

	// Animation view
	private HomeActivityView shoppingHomeHomeActivityView;
	// Broadcast receiver takes care of messages from the GenieHub/EventBus
	// initialized in WakeService
	private BroadcastReceiver receiver = new ShoppingReceiver();;

	private SensorManager accelerometerManager;
	private Sensor acceleromenter;
	private boolean acceleromenterInitialize;
	private float lastx;
	private float lasty;
	private float lastz;

	boolean interrupted;

	// Users are people, things and places. In this app, active users are people
	// out shopping.
	// there is a direct link between the name of the person and the name of the
	// thing that that person uses
	// Sadly this is a mixture of persons and objects e.g. rollators

	private ArrayList<User> activeUsers;

	private static Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		interrupted = false;
		acceleromenterInitialize = false;

		registerReceiver(receiver, new IntentFilter(
				WakeService.NEW_SHOPPING_ACTIVITY));

		accelerometerManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		acceleromenter = accelerometerManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// The shopping activity view runs in full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.animationview);
		shoppingHomeHomeActivityView = (HomeActivityView) findViewById(R.id.shoppingActivityView);
		Bundle bundle = getIntent().getExtras();
		// Again sending around the data we need, as we have no persistance
		activeUsers = bundle
				.getParcelableArrayList(GalleryActivity.ACTIVE_USERS);

		// DEBUG
		// for (User u : activeUsers)
		// Log.d("Active CONTACT", "" + u.getUserId());

		shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
		shoppingHomeHomeActivityView.update();
		shoppingHomeHomeActivityView.setMyInterruptHandler(this);

		showShoppingActivity();
		// TODO check if by any chance it can happen that the GB/EB is off
		// startService(new Intent(this, WakeService.class));
	}

	// Updates the animaiton view with users (and objects) that are shopping
	private void showShoppingActivity() {
		shoppingHomeHomeActivityView.clear();
		for (User u : activeUsers) {
			if (u.getUserActivity() == UserActivity.Shopping) {
				ShoppingCart sc = new ShoppingCart(this);
				sc.setId(u.getUserId());
				shoppingHomeHomeActivityView.addShopper(sc, false);
				// for (Movable so : u.getOffers()) { // Movables are
				// ShoppingOffer
				// // objects. Had problems
				// // with parceling these
				// shoppingHomeHomeActivityView.addOffer(so, false);
				// }
			} else if (u.getOffers().size() > 0) {
				for (Movable so : u.getOffers()) { // Movables are ShoppingOffer
					// objects. Had problems
					// with parceling these
					shoppingHomeHomeActivityView.addOffer(so, false);
				}
			}
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceBundle) {
		super.onRestoreInstanceState(savedInstanceBundle);
		interrupted = false;
		Log.d("shopping activity", "onRestoreInstanceState");
		shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
		shoppingHomeHomeActivityView.update();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		interrupted = false;
		Log.d("shopping activity", "onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		interrupted = false;
		registerReceiver(receiver, new IntentFilter(
				WakeService.NEW_SHOPPING_ACTIVITY));

		shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
		shoppingHomeHomeActivityView.update();

		accelerometerManager.registerListener(this, acceleromenter,
				SensorManager.SENSOR_DELAY_NORMAL);

		Log.d("shopping activity", "onResume");

	}

	@Override
	protected void onPause() {
		super.onPause();
		interrupted = false;
		Log.d("shopping activity", "onPause");
		accelerometerManager.unregisterListener(this);
		// shoppingActivity.setMode(HomeActivityView.PAUSE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		interrupted = false;
		unregisterReceiver(receiver);
		Log.d("shopping activity", "onStop");
		// shoppingActivity.setMode(HomeActivityView.PAUSE);
	}

	int i = 0;

	public void myInterrupt() {
		i++;
		if (!interrupted) {
			interrupted = true;
			Log.d("INTENT FIRESD", "" + i); // Leftover from problems with
			// interrupt being fired several
			// times
			Intent intent = new Intent(HomeActivity.this,
					ActivityOverview.class);
			ArrayList<User> shoppers = new ArrayList<User>();
			for (User u : activeUsers) {
				if (u.getUserActivity() == UserActivity.Shopping
						|| (u.getOffers().size() > 0))
					shoppers.add(u); // add all the users as shoppers ?
			}
			intent.putParcelableArrayListExtra(HomeActivity.ACTIVE_USERS,
					shoppers);
			// intent.putParcelableArrayListExtra(HomeActivity.ACTIVE_USERS,
			// activeUsers);
			startActivity(intent);
		}
	}

	/**
	 * Broadcast receiver to get notifications of new shopping activity from the
	 * service.
	 */
	private class ShoppingReceiver extends BroadcastReceiver {
		int testid = 1;

		@Override
		public void onReceive(Context context, Intent intent) {

			String activity = intent.getStringExtra(WakeService.ACTIVITY);
			String content = intent.getStringExtra(WakeService.CONTENT);
			String actor = intent.getStringExtra(WakeService.ACTOR);
			Log.d("GH event", activity + " " + actor + " " + content);
			User actor_u = null;
			actor_u = Utilities.getContactByFullName(actor, activeUsers);
			if (actor_u != null) {
				int actor_id = actor_u.getUserId();

				// message parsing

				// start / stop shopping
				if (activity.equalsIgnoreCase("shopping")
						&& (content.contains("start") || content
								.contains("stop"))) {

					// I am getting a message from a THING and here I want to
					// update the status of the person it is associated to
					// here association works by name
					String thing = actor;
					String userName = thing.split(" ")[0];
					userName = userName.substring(0, userName.length() - 1);
//					Log.d(TAG, "thing activity detected, username is: "
//							+ userName);

					actor_id = Utilities.getUserByObject(actor_u, activeUsers)
							.getUserId();
//					Log.d(TAG,"user id that I am going to update "+ actor_id);
					updateShoppingActivity(content.split(" ")[0], actor_id);

				}

				// 2. Shopping offer is activity "shopping" but with url as
				// content
				// ACTOR WILL ALWAYS BE A PERSON
				// TODO : why is it publishing 2 icons?
				else if (activity.equalsIgnoreCase("shopping")
						&& content.contains("spark:photo")) {
					Log.d(TAG, "I have found a new spark: "
							+ content.split("spark:photo:")[1].split(" ")[0]);
					ShoppingOffer so = new ShoppingOffer(HomeActivity.this);

					// so.setAltImageUrl("http://idea.itu.dk:3000/uploads/images/scaled_full_fb79b5fef393d17fc2c5.jpg");//content.split(":")[2]);
					so.setAltImageUrl(content.split("spark:photo:")[1]
							.split(" ")[0]);

					so.setId(actor_id);
					// if (activeUsers.contains(actor_u))
					actor_u.addOffer(so);

					shoppingHomeHomeActivityView.addOffer(so, true);
				}

				// 3. Place
				// A user is at at certain place.
				// This is e.g. Actor=place01 and
				// content is now the userId
				// We update this users location property "where is he"
				// the field ACTOR WILL ALWAYS BE of type PLACE
				else if (activity.equalsIgnoreCase("shopping")
						&& (content.contains("enter") || content
								.contains("leave"))) {
					Log.d(TAG, "place message");
					String location = null;
					if (content.contains("enter")) {
						location = actor_u.getFirstName();
						// TODO: do something to show that location has changed?
						ShoppingPlace sp = new ShoppingPlace(HomeActivity.this, actor_u.getUserImage());
						sp.setId(actor_u.getUserId());
						shoppingHomeHomeActivityView.addPlace(sp, true);
					} else {
						location = "unknown";
						// TODO: do something to show that location has changed?
						shoppingHomeHomeActivityView.removePlace(actor_u.getUserId());
					}
					User addressedUser = null;
					addressedUser = Utilities.getContactByFullName(content
							.split(" ")[0], activeUsers);
					addressedUser = Utilities.getUserByObject(addressedUser, activeUsers);
					if (addressedUser != null)
						addressedUser.setLocation(location);
					Log.d(TAG, "location is "+location);
				}
			}// end if actor_user!=null
		}

		private void updateShoppingActivity(String content, int userId) {

			if (content.contains("start")) {
				User user = null;
				for (User u : activeUsers) {
					if (u.getUserId() == userId) {
						user = u;
						break;
					}
				}

				// return if user is not known or already displayed
				// I often get several similar messages from genie hub
				if (user == null
						|| shoppingHomeHomeActivityView.isUserDisplayed(userId))
					return;

				// Bells and whistles
				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification();
				notification.defaults = Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE;
				nm.notify(1, notification);

				user.setUserActivity(UserActivity.Shopping);

				// We put shopping carts on the view for object on shopping
				ShoppingCart sc = new ShoppingCart(HomeActivity.this);
				sc.setId(userId);
				shoppingHomeHomeActivityView.addShopper(sc, true);// true for
				// flashing
				// some
				// color
			} else if (content.contains("stop")) {
				User user = null;
				for (User u : activeUsers) {
					if (u.getUserId() == userId) {
						user = u;
						break;
					}
				}
				if (user == null)
					return;
				user.setUserActivity(UserActivity.Unknown);
				shoppingHomeHomeActivityView.removeShopper(userId);
			}
		}
	}

	// Std way of keeping a context ref.
	public static Context getContext() {
		return mContext;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO not relevant

	}

	public void onSensorChanged(SensorEvent event) {

		// get the current values
		// if there is difference more than the noise limit
		// then there is a movement
		// switch view!

		float NOISE = (float) 3.0;
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		if (!acceleromenterInitialize) {
			lastx = x;
			lasty = y;
			lastz = z;
			acceleromenterInitialize = true;
		} else {
			float deltaX = Math.abs(lastx - x);
			float deltaY = Math.abs(lasty - y);
			float deltaZ = Math.abs(lastz - z);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;
			lastx = x;
			lasty = y;
			lastz = z;

			if (deltaX > NOISE || deltaY > NOISE || deltaZ > NOISE) {
				// Log.d(TAG,
				// "detected accelerometer event worth changing the view");
				myInterrupt();
			}
		}

	}
}
