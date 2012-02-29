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

//For when animation screen is tapped.
interface MyInterruptHandler {
	public abstract void myInterrupt();
}

public class HomeActivity extends Activity implements MyInterruptHandler {
	public static final String USER_ID = "user01"; // User of this particular
	// device
	public static final String ACTIVE_USERS = "active_users_constant";
	public static final String CONTACTS = "all_shopping_contacts_for_user";
	public static final String TEST_OFFER_URL = "http://idea.itu.dk:3000/uploads/images/scaled_full_fb79b5fef393d17fc2c5.jpg";
	public static final String TAG = "Home Activity";
	// Animation view
	private HomeActivityView shoppingHomeHomeActivityView;
	private BroadcastReceiver receiver;
	boolean interrupted;

	// Not a very good abstraction, but users are people out shopping.
	// Sadly this is a mixture of persons and objects like rollators - very bad
	// name...
	private ArrayList<User> activeUsers;
	// With no persistent data in place we keep all contacts
	private ArrayList<User> contacts;
	private static Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		interrupted = false;
		// The shopping activity viev runs in full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.animationview);
		shoppingHomeHomeActivityView = (HomeActivityView) findViewById(R.id.shoppingActivityView);
		Bundle bundle = getIntent().getExtras();
		// Again sending around the data we need, as we have no persistance
		activeUsers = bundle
				.getParcelableArrayList(GalleryActivity.ACTIVE_USERS);
		getContacts();
		// getShoppingActivity();
		for (User u : activeUsers)
			Log.d("Active CONTACT", "" + u.getUserId());

		shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
		shoppingHomeHomeActivityView.update();
		shoppingHomeHomeActivityView.setMyInterruptHandler(this);

		showShoppingActivity();
		//startService(new Intent(this, WakeService.class));
	}

	private void getContacts() {
		if (contacts != null)
			contacts.clear();
		contacts = FetchActivityTask.getContactsForUser(false,
				HomeActivity.USER_ID); // no need for filter :)
	}

	// Updates the animaiton view with users (and objects) that are shopping
	private void showShoppingActivity() {
		shoppingHomeHomeActivityView.clear();
		for (User u : activeUsers) {
			if (u.getUserActivity() == UserActivity.Shopping) {
				ShoppingCart sc = new ShoppingCart(this);
				sc.setId(u.getUserId());
				shoppingHomeHomeActivityView.addShopper(sc, false);
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
		if (receiver == null) {
			receiver = new ShoppingReceiver();
			registerReceiver(receiver, new IntentFilter(
					WakeService.NEW_SHOPPING_ACTIVITY));
		}
		shoppingHomeHomeActivityView.setState(HomeActivityView.VISIBLE);
		shoppingHomeHomeActivityView.update();
		Log.d("shopping activity", "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		interrupted = false;
		Log.d("shopping activity", "onPause");
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
				if (u.getUserActivity() == UserActivity.Shopping)
					shoppers.add(u);
			}
			intent.putParcelableArrayListExtra(HomeActivity.ACTIVE_USERS,
					shoppers);
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
			actor_u = Utilities.getContactByFullName(actor, contacts);
			if (actor_u != null) {
				int actor_id = actor_u.getUserId();
				// Not a very good way of making distinction between messages,
				// and
				// won't scale well with more message types.
				// 1. shopping star | stop
				if (activity.equalsIgnoreCase("shopping")
						&& (content.contains("start") || content
								.contains("stop"))) {

					
					// I am getting a message from a THING and here I want to
					// update the status of the person it is associated to
					// here association works by name
					String thing = actor;
					String userName = thing.split(" ")[0];
					userName = userName.substring(0, userName.length()-1);
					
					for (User user : contacts) {
						if (userName.equals(user.getFirstName())) {
							actor_id = user.getUserId();
							break;
						}
					}
					updateShoppingActivity(content.split(" ")[0], actor_id);

				}
				// else if(activity.equalsIgnoreCase("shopping")&&
				// (content.contains("enter") ||
				// content.contains("leave"))){
				//               
				// }

				// 2. Shopping offer is activity "shopping" but with url as
				// content
				// ACTOR WILL ALWAYS BE A PERSON
				// TODO : why is it publishing 2 icons?
				else if (activity.equalsIgnoreCase("shopping")
						&& content.contains("spark:photo")) {
					Log.d(TAG, "I have found a new spark");
					ShoppingOffer so = new ShoppingOffer(HomeActivity.this);

					//so.setAltImageUrl("http://idea.itu.dk:3000/uploads/images/scaled_full_fb79b5fef393d17fc2c5.jpg");//content.split(":")[2]);
					so.setAltImageUrl(content.split("spark:photo:")[1].split(" ")[0]);
					
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
				// ACTOR WILL ALWAYS BE A PLACE
				else {
					String location = null;
					if (content.contains("enter")) {
						location = actor_u.getFirstName();
						// TODO: do something to show that location has changed?
					} else {
						location = "unknown";
						// TODO: do something to show that location has changed?
					}
					User addressedUser = null;
					addressedUser = Utilities.getContactByFullName(content
							.split(" ")[0], contacts);
					if (addressedUser != null)
						addressedUser.setLocation(location);
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

}
