package com.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by IntelliJ IDEA. User: ahkj Date: 27/11/11 Time: 14.42 Similar to
 * GroupProfileActivity, just view pushing stuff.
 */
public class ProfileActivity extends android.app.Activity {
	private User user;
	private View previousView;
	public static String SELECTED_USER = "selected_user";
	private Timer timer;
	public static final String SHOPPING_FRIENDS = "user_shopping_friends";
	protected static final String SHOPPING_OBJECTS = "user_shopping_contacts";
	private ArrayList<User> shoppingFriends;
	private ArrayList<ShoppingOffer> offers;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile);
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getParcelable(ProfileActivity.SELECTED_USER);
		Log.d("profile activity", "active user id = "+user.getFullName());
		shoppingFriends = bundle
				.getParcelableArrayList(ProfileActivity.SHOPPING_FRIENDS);
		setUserImage(user.getImageUrl()); // downloading img
		TextView tv = (TextView) findViewById(R.id.userTextView);
		String text = "";
		if (user.getFullName() == null || user.getFullName().isEmpty())
			text = "" + user.getUserId();
		else
			text = user.getFirstName();
		tv.setText(text);

		// listener for left home button, shopping cart
		Button lhome = (Button) findViewById(R.id.profilelhomebtn);
		lhome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				timer.cancel();
				// TODO activate loading
				Intent intent = new Intent(ProfileActivity.this,
						HomeActivity.class);
				intent.putParcelableArrayListExtra(
						GalleryActivity.ACTIVE_USERS, shoppingFriends);
				startActivity(intent);
			}
		});

		// listener for left home button, shopping cart
		Button rhome = (Button) findViewById(R.id.profilerhomebtn);
		rhome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				timer.cancel();
				finish(); // ah, this way you are coming back to the main
				// activity?!
			}
		});

		setupButtonListeners();
		restartTimer();
	}

	private void setProfileStatus(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.statusLayout);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		TextView statusText = new TextView(this);
		statusText.setLayoutParams(params);
		statusText.setTextSize(20.0f);
		String text;
		
		if (user.getUserActivity() == UserActivity.Shopping)
			text = user.getFirstName()
					+ " er på indkøb "
					+ (user.getLocation().isEmpty() ? "" : "i "
							+ user.getLocation());
		else
			text = "Ingen status på " + user.getFirstName();
		statusText.setText(text);
		statusText.setTextSize(40);
		statusLayout.addView(statusText);
		
		//TODO
//		TextView statusDataText = new TextView(this);
//		statusDataText.setLayoutParams(params);
//		statusDataText.setTop(200);
//		statusDataText.setTextSize(30);
//		String textData = getString(R.string.statusDataMessageTMP);
//		statusDataText.setText(textData);
//		statusLayout.addView(statusDataText);
		
		
	}

	private void setWeekOverview(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.statusLayout);
		// ArrayList<ArrayList<Movable>> weekActivity =
		// FetchActivityTask.getWeekActivity(HomeActivity.USER_ID);
		// populateWeekView(weekActivity);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);
	}

	private void populateWeekView(ArrayList<ArrayList<Movable>> weekActivity) {

	}

	private void setSparks(View view) {
		updateButtonColor(view);
		
		dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		

		new Thread() {
			public void run() {
//				offers = FetchActivityTask
//						.getAllOffersForUser(HomeActivity.USER_ID);
				offers = FetchActivityTask
				.getOffersByUser(HomeActivity.USER_ID,user);
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
			
			
			RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.statusLayout);
			statusLayout.removeAllViews();
			statusLayout.invalidate();
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.FILL_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
			
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
							
//							int userId = offers.get(position)
//									.getSharedByUserId();
//							User u = Utilities.getContactById(userId,
//									shoppingFriends);
//							// if the spark has been shared by current user
//							if (u == null) {
//								u = user;
//							}
//							
//							String text = "shared by: " + u.getFirstName();
//							message.setText(text);
//							message.setTextSize(40);

						}

						public void onNothingSelected(AdapterView<?> arg0) {
							// image_selected.setImageDrawable(shoppingoffers.get(0).getImageFile());

						}
					});
		}

	}

	private void setShoppingStats(View view) {
		RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.statusLayout);
		statusLayout.removeAllViews();
		statusLayout.invalidate();
		updateButtonColor(view);
	}

	private void setupButtonListeners() {
		Button b = (Button) findViewById(R.id.profileBtn1);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Profile", "loading profile");
				restartTimer();
				setProfileStatus(view);
			}
		});
		// Initialize with the profile view set
		setProfileStatus(b);

		b = (Button) findViewById(R.id.profileBtn2);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Profile", "loading week");
				restartTimer();
				setWeekOverview(view);
			}
		});

		b = (Button) findViewById(R.id.profileBtn3);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("Profile", "loading sparks");
				restartTimer();
				setSparks(view);
			}
		});

		// Flest indk√∏b
		// b = (Button)findViewById(R.id.profileBtn4);
		// b.setOnClickListener(new View.OnClickListener(){
		// public void onClick(View view) {
		// restartTimer();
		// setShoppingStats( view);
		// }
		// });
	}

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
				Intent intent = new Intent(ProfileActivity.this,
						HomeActivity.class);
				ArrayList<User> objs = new ArrayList<User>();
				objs.addAll(shoppingFriends);
				objs.addAll(FetchActivityTask
						.getObjectsActivity(HomeActivity.USER_ID));
				intent.putParcelableArrayListExtra(
						GalleryActivity.ACTIVE_USERS, objs);
				startActivity(intent);
			}
		}, GalleryActivity.SLEEP_DELAY);
	}

	// Get image from server
	private void setUserImage(String url) {
		Drawable image = ImageOperations(this, url, "image.jpg");
		ImageView iv = (ImageView) findViewById(R.id.userView);
		iv.setImageDrawable(image);
		
		enableSkypeOnLongClick(iv);
		
		// TODO add the ON/OFFLINE Icon on the profile view
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
			//i.setPadding(10, 10, 10, 10);
			
			int height = parent.getHeight();
			i.setLayoutParams(new Gallery.LayoutParams(height-20, height-10));
			
			
			
			RelativeLayout rl = new RelativeLayout(ProfileActivity.this);
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
	
	private void enableSkypeOnLongClick(ImageView iv) {
		
		iv.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View arg0) {
				
				Log.d("profile activity", "onlongclick detected");
				
				final String num;
				final String name;
				final String skypename;
				boolean isPerson = false;

				num = user.getBio();
				name = user.getFirstName();
				isPerson = true; // in this case all the users are persons
				

				if (isPerson) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ProfileActivity.this);
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
//											sky.setData(Uri.parse("skype:"
//													+ test));
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

}
