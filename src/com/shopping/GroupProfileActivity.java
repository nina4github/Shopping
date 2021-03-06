package com.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;

/**
 * Created by IntelliJ IDEA. User: ahkj Date: 27/11/11 Time: 14.42 Profile view
 * for the group - shopping friends. Very similar to the ProfileActivity.class
 * and the two may be subject merged in to some degree.
 */
public class GroupProfileActivity extends android.app.Activity {
	private String TAG = "Group Profile Activity";
	private User user;
	private ArrayList<User> shoppingFriends;
	private View previousView;
	public static String SELECTED_USER = "selected_user";
	private Timer timer;
	protected ArrayList<ShoppingOffer> offers;
	private ProgressDialog dialog;
	private ArrayList<User> objects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		Bundle bundle = getIntent().getExtras();
		// Here selected is just user of this device
		user = (User) bundle.getParcelable(ProfileActivity.SELECTED_USER);
		shoppingFriends = bundle
				.getParcelableArrayList(ProfileActivity.SHOPPING_FRIENDS);
		objects = bundle.getParcelableArrayList(ProfileActivity.SHOPPING_OBJECTS);
		Drawable image = getResources().getDrawable(R.drawable.group);
		ImageView iv = (ImageView) findViewById(R.id.groupView);
		iv.setImageDrawable(image);
		TextView tv = (TextView) findViewById(R.id.groupTextView);
		String text = "Venner";
		tv.setText(text);

		// listener for left home button, shopping cart
		Button lhome = (Button) findViewById(R.id.grouplhomebtn);
		lhome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				timer.cancel();
				Intent intent = new Intent(GroupProfileActivity.this,
						HomeActivity.class);
				intent.putParcelableArrayListExtra(
						GalleryActivity.ACTIVE_USERS, shoppingFriends);
				startActivity(intent);
				finish();
			}
		});

		// listener for left home button, shopping cart
		Button rhome = (Button) findViewById(R.id.grouprhomebtn);
		rhome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				timer.cancel();
				finish();
			}
		});

		setupButtonListeners();
		restartTimer();
	}

	// who is shopping
	private void setProfileStatus(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.groupStatusLayout);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		ImageView iv;
		ImageView i;
		int x = 0;
		for (User u : shoppingFriends) {
			iv = new ImageView(GalleryActivity.getContext());
			iv.setLayoutParams(params);
			Drawable d = getUserImageDrawable(u.getImageUrl());
			iv.setImageDrawable(d);
			iv.setTranslationX(x);

			statusLayout.addView(iv);

			// Add shopping cart if shopping. This is the cart image underneath
			// the user image.
			if (u.getUserActivity() == UserActivity.Shopping) {
				i = new ImageView(this);
				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(100, 100);
//				p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//				p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				i.setLayoutParams(p);
				i
						.setImageDrawable(getResources().getDrawable(
								R.drawable.cart));
				i.setTranslationX(x);

				i.setTranslationY(d.getIntrinsicHeight() - 25);// 25 is magic
				// number for
				// distance to
				// user image
				statusLayout.addView(i);
			}
			x += d.getIntrinsicWidth() + 20; // next image is placed at width of
			// current image plus some
			// spacing

			enableSkypeOnLongClick(iv, u);
		}

		// TODO
		// TextView statusDataText = new TextView(this);
		// statusDataText.setLayoutParams(params);
		// statusDataText.setTop(300);
		// statusDataText.setTextSize(30);
		// String textData = getString(R.string.statusDataGroupMessageTMP);
		// statusDataText.setText(textData);
		// statusLayout.addView(statusDataText);

		// statusLayout.addView(statusText);
	}

	private void enableSkypeOnLongClick(ImageView iv, User u) {
		final User user = u;
		iv.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View arg0) {

				final String num;
				final String name;
				final String skypename;
				boolean isPerson = false;

				num = user.getBio();
				name = user.getFirstName();
				isPerson = true;

				if (isPerson) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							GroupProfileActivity.this);
					builder.setMessage("Do you want to call " + name + "?")
							.setCancelable(false).setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// Intent intent = new Intent(
											// Intent.ACTION_CALL);
											// intent
											// .setData(Uri
											// .parse("tel:"
											// + num));
											// startActivity(intent);

											Intent sky = new Intent(
													"android.intent.action.VIEW");
											String test = "echo123";

											String skypename = "sip_aw_"
													+ user.getFullName();
											// sky.setData(Uri.parse("skype:"
											// + test));
											sky.setData(Uri.parse("skype:"
													+ skypename));
											startActivity(sky);

										}
									}).setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
					TextView textView = (TextView) alert
							.findViewById(android.R.id.message);
					textView.setTextSize(30);

				}
				return false;
			}

		});

	}

	// THE four/three button operations in the top
	private void setWeekOverview(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.groupStatusLayout);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);
		
		ArrayList<User> entities = new ArrayList<User>();
		//entities.add(user);
		entities.addAll(shoppingFriends);
		entities.addAll(objects);
//		for (User user : entities) {
//			Log.d(TAG,"entities list: "+user.getFirstName());
//		}
//		
		WeekActivities weekActivities = FetchActivityTask.getWeekActivity(HomeActivity.USER_ID,entities);
		// ArrayList<HashMap<Integer,
														// Integer>>
														// weekActivity =
		//DEBUG
		Log.d(TAG, "week activities object has (person): "+weekActivities.countByType("person"));
		weekActivities.print();
		
		populateWeekView(weekActivities,statusLayout);
		
	
	}

	
	private void populateWeekView(
			WeekActivities activities, RelativeLayout statusLayout) {
		
		Log.d(TAG,"populate week");
		// what day is today?
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_WEEK);
		// today goes from 1 to 7, our stream goes from 0 to 6

		// order the array relativeWeek to have today as the last element
		int sparkscounter[] = new int[7];
		int shoppingcounter[] = new int[7]; 
		int placecounter[] = new int[7];
		
		LinearLayout weekLayout = new LinearLayout(GalleryActivity.getContext());
		LinearLayout.LayoutParams weekparams = new LinearLayout.LayoutParams(750,450);
		weekLayout.setLayoutParams(weekparams);
	
		weekLayout.setOrientation(LinearLayout.HORIZONTAL);
		String weekday[] = {"S","M","T","O","T","F","L"};
		
		statusLayout.addView(weekLayout);
		LinearLayout[] dayLayout = new LinearLayout[7];
		int x = 5;
		
		for (int i = 0; i < 7; i++) {
			int day = (i+today)%7;
			sparkscounter[i] = activities.countByDayAndType(day, "person");
			shoppingcounter[i]=activities.countByDayAndType(day, "thing");
			placecounter[i]=activities.countByDayAndType(day, "place");
			
			Log.d(TAG, "dimensions "+sparkscounter[i] + " "+ shoppingcounter[i]+ " "+ placecounter[i]);
			
			dayLayout[i] = new LinearLayout(this);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 400);
			dayLayout[i].setLayoutParams(params);
			dayLayout[i].setOrientation(LinearLayout.VERTICAL);
			if (i%2!=0){
			dayLayout[i].setBackgroundColor(R.color.myblue);}
			dayLayout[i].setTranslationX(x);
			Log.d(TAG, "traslation "+ x);
			x+=(10);
			TextView dayText = new TextView(this);
			dayText.setGravity(Gravity.CENTER_HORIZONTAL);
			dayText.setTextSize(20);
			dayText.setTextColor(R.color.black);
			dayText.setText(weekday[day]);
			dayLayout[i].addView(dayText);
			populateDayView(sparkscounter[i],shoppingcounter[i],placecounter[i],dayLayout[i]);
			weekLayout.addView(dayLayout[i]);
		}


	}
	
	private void populateDayView(int sparks, int shopping, int place, LinearLayout day){
		Log.d(TAG, "populate day");
		
		
		
		for (int i = 0; i <sparks; i++) {
			ImageView shoppingCarts = new ImageView(this);
			shoppingCarts.setImageResource( R.drawable.tilbud);
			//shoppingCarts.setScaleType(ScaleType.FIT_XY);
			shoppingCarts.setLayoutParams(new LinearLayout.LayoutParams(60,60));
			day.addView(shoppingCarts);
		}
		for (int i = 0; i <shopping; i++) {
			ImageView shoppingOffers = new ImageView(this);
			shoppingOffers.setImageResource( R.drawable.cart);
			//shoppingOffers.setScaleType(ScaleType.FIT_XY);
			shoppingOffers.setLayoutParams(new LinearLayout.LayoutParams(60,60));
			day.addView(shoppingOffers);
		}
	}

	private void setSparks(View view) {

		updateButtonColor(view);

		dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {
			public void run() {
				// offers = FetchActivityTask
				// .getAllOffersForUser(HomeActivity.USER_ID);
				offers = FetchActivityTask
						.getAllWeekOffers(HomeActivity.USER_ID);//.getAllOffersForUser(HomeActivity.USER_ID);
				runOnUiThread(new Runnable() {
					public void run() {
						onOffers();
					}
				});
				dialog.dismiss();
			}
		}.start();

	}

	private void onOffers() {
		if (offers != null) {

			RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.groupStatusLayout);
			statusLayout.removeAllViews();
			statusLayout.invalidate();

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.FILL_PARENT);
			params
					.addRule(RelativeLayout.CENTER_IN_PARENT,
							RelativeLayout.TRUE);

			Gallery offersGallery = new Gallery(this);

			offersGallery.setLayoutParams(params);

			offersGallery.setTop(10);
			offersGallery.setBottom(10);

			statusLayout.addView(offersGallery);

			ImageAdapterCircleGallery galleryAdapter = new ImageAdapterCircleGallery(
					this);
			Bitmap[] offers_images = new Bitmap[offers.size()];

			int i = 0;
			for (ShoppingOffer offer : offers) {

				offers_images[i] = offer.getBitmap();
				i = i + 1;
			}
			galleryAdapter.setmImageBitmaps(offers_images);
			Log.d("Profile Activity - set spark", "gallery bitmaps: "
					+ galleryAdapter.getmImageBitmaps().length);

			offersGallery.setAdapter(galleryAdapter);
			galleryAdapter.notifyDataSetChanged();

			final TextView message = new TextView(this);

			statusLayout.addView(message);

			offersGallery
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> arg0, View v,
								int position, long id) {

							// int userId = offers.get(position)
							// .getSharedByUserId();
							// User u = Utilities.getContactById(userId,
							// shoppingFriends);
							// // if the spark has been shared by current user
							// if (u == null) {
							// u = user;
							// }
							//							
							// String text = "shared by: " + u.getFirstName();
							// message.setText(text);
							// message.setTextSize(40);

						}

						public void onNothingSelected(AdapterView<?> arg0) {
							// image_selected.setImageDrawable(shoppingoffers.get(0).getImageFile());

						}
					});
		}

	}

	private void setShoppingStats(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.groupStatusLayout);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);
	}

	private void setupButtonListeners() {
		Button b = (Button) findViewById(R.id.groupBtn1);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				restartTimer();
				setProfileStatus(view);
			}
		});
		// Initialize with the profile view set
		setProfileStatus(b);

		b = (Button) findViewById(R.id.groupBtn2);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				restartTimer();
				setWeekOverview(view);
			}
		});

		b = (Button) findViewById(R.id.groupBtn3);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				restartTimer();
				setSparks(view);
			}
		});
		// flest indkob
		// b = (Button)findViewById(R.id.groupBtn4);
		// b.setOnClickListener(new View.OnClickListener(){
		// public void onClick(View view) {
		// restartTimer();
		// setShoppingStats( view);
		// }
		// });
	}

	// Swap button background - which one is highlighted
	private void updateButtonColor(View newButton) {
		// Update button row
		if (previousView != null)
			previousView.setBackgroundColor(getResources().getColor(
					R.color.white));
		newButton.setBackgroundColor(getResources().getColor(R.color.myblue));
		previousView = newButton;
	}

	private void restartTimer() {
		if (timer != null)
			timer.cancel();
		timer = new Timer("sleeptime");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent(GroupProfileActivity.this,
						HomeActivity.class);
				ArrayList<User> objs = shoppingFriends;
				objs.addAll(FetchActivityTask
						.getObjectsActivity(HomeActivity.USER_ID));
				intent.putParcelableArrayListExtra(
						GalleryActivity.ACTIVE_USERS, objs);
				startActivity(intent);
			}
		}, GalleryActivity.SLEEP_DELAY);
	}

	// Get image from server
	private Drawable getUserImageDrawable(String url) {
		Drawable image = ImageOperations(this, url, "image.jpg");
		return image;
	}

	private Drawable ImageOperations(Context ctx, String url,
			String saveFilename) {
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

	public Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
	}

	public class ImageAdapterCircleGallery extends BaseAdapter {

		private Context mContext;

		private Integer[] mImageIds;
		private String[] mImageUrls;
		private Drawable[] mImageDrawables;

		private Bitmap[] mImageBitmaps;

		public Bitmap[] getmImageBitmaps() {
			return mImageBitmaps;
		}

		public void setmImageBitmaps(Bitmap[] mImageBitmaps) {
			this.mImageBitmaps = mImageBitmaps;
		}

		public Drawable[] getmImageDrawables() {
			return mImageDrawables;
		}

		public void setmImageDrawables(Drawable[] mImageDrawables) {
			this.mImageDrawables = mImageDrawables;
		}

		public String[] getmImageUrls() {
			return mImageUrls;
		}

		public void setmImageUrls(String[] mImageUrls) {
			this.mImageUrls = mImageUrls;
		}

		public Integer[] getmImageIds() {
			return mImageIds;
		}

		public void setmImageIds(Integer[] mImageIds) {
			this.mImageIds = mImageIds;
		}

		public ImageAdapterCircleGallery(Context c) {
			mContext = c;
		}

		// public ImageAdapterCircleGallery(Context c, Integer[] imgIds) {
		// mContext = c;
		// mImageIds=imgIds;
		//
		// }

		public int getCount() {
			return mImageBitmaps.length;// Integer.MAX_VALUE;
		}

		public Object getItem(int position) {
			return position;// getPosition(position);
		}

		public long getItemId(int position) {
			return position;// getPosition(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Log.d("ProfileActivity Offers Overview", "drawing offer # "
					+ position + " of " + mImageBitmaps.length);
			ImageView i = new ImageView(mContext);
			// position = getPosition(position);

			// i.setImageDrawable(mImageDrawables[position]);

			i.setImageBitmap(mImageBitmaps[position]);
			// i.setImageResource(R.drawable.aase);

			i.setScaleType(ImageView.ScaleType.FIT_XY);
			// i.setPadding(10, 10, 10, 10);

			int height = parent.getHeight();
			i
					.setLayoutParams(new Gallery.LayoutParams(height - 20,
							height - 10));

			RelativeLayout rl = new RelativeLayout(GroupProfileActivity.this);
			rl.setPadding(10, 5, 10, 5);
			rl.setBackgroundColor(R.color.myblue);
			rl.setLayoutParams(new Gallery.LayoutParams(height, height));
			rl.addView(i);
			// i.setBackgroundColor();
			// TextView captionNotification= new TextView(mContext);
			// captionNotification.setText("This is user "+position);
			// captionNotification.setTextSize(30);
			// LinearLayout l = new LinearLayout(mContext);
			// l.setOrientation(LinearLayout.VERTICAL);
			// l.addView(i);
			// l.addView(captionNotification);
			return rl;
		}

		public int checkPosition(int position) {
			return getPosition(position);
		}

		int getPosition(int position) {
			if (position >= mImageBitmaps.length) {
				position = position % mImageBitmaps.length;
			}
			return position;
		}
	}
}
