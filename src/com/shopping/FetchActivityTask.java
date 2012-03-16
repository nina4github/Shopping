package com.shopping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.util.IO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * Created by IntelliJ IDEA. User: ahkj Date: 21/11/11 Time: 23.01
 * 
 * FetchActivitiTask - fetches JSON from server TODO Everything in here should
 * be done asynchroneously. Thus the AsyncTask. Not implemented.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class FetchActivityTask {// extends AsyncTask<String, Integer, Boolean> {
	// Note to self on suggested types for an AsyncTask//Input, Progress Report
	// Type, Result Type
	public static final File offers_dir = new File(Environment
			.getExternalStorageDirectory(), "EM/shopping/offers/");

	// @Override
	// protected Boolean doInBackground(String... strings) {
	// return null; // To change body of implemented methods use File |
	// // Settings | File Templates.
	// }

	// @Override
	// protected void onPreExecute() {
	// // TODO Auto-generated method stub
	// super.onPreExecute();
	// }

	// @Override
	// protected void onPostExecute(Boolean result) {
	// // TODO Auto-generated method stub
	// super.onPostExecute(result);
	// }

	/**
	 * Test users.
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<User> getTestUsers(Context context) {
		ArrayList<User> users = new ArrayList<User>();

		User a = new User();
		a.setUserId(0);
		a.setUserActivity(UserActivity.Shopping);
		a.setFullName("Anders");

		User b = new User();
		b.setUserId(1);
		b.setUserImage(BitmapFactory.decodeResource(context.getResources(),
				R.drawable.senior2_80px));
		b.setFullName("Bjorn");
		b.setUserActivity(UserActivity.Shopping);
		ShoppingOffer so = new ShoppingOffer(context);
		so.setId(2);
		b.addOffer(so);

		User c = new User();
		c.setUserId(3);
		c.setUserImage(BitmapFactory.decodeResource(context.getResources(),
				R.drawable.senior3_80px));
		c.setFullName("Carsten");
		c.setUserActivity(UserActivity.Billard);
		ShoppingOffer so1 = new ShoppingOffer(context);
		ShoppingOffer so2 = new ShoppingOffer(context);
		so1.setId(4);
		so2.setId(5);
		c.addOffer(so1);
		c.addOffer(so2);

		User d = new User();
		d.setUserId(12);
		d.setUserActivity(UserActivity.Shopping);
		d.setFullName("Kirsten");
		//
		users.add(a);
		users.add(b);
		users.add(c);
		users.add(d);
		return users;
	}

	/**
	 * Get a new user if user is not already in our collection
	 * 
	 * @param user_id
	 * @return
	 */
	private static User getUserwithId(int user_id, ArrayList<User> contacts) {
		User newUser = null;
		for (User u : contacts) {
			if (u.getUserId() == user_id)
				return u;
		}
		newUser = new User();
		newUser.setUserId(user_id);
		return newUser;
	}

	/**
	 * HTTP request for getting a JSON string
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String readActivity(String jsonString) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(jsonString);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(FetchActivityTask.class.toString(),
						"Failed to download JSON statuscode: " + statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * Filter could be "person" or "thing" or "place". The filter was a quick
	 * fix for filtering either persons or objects/things from the contacts
	 * response.
	 * 
	 * @param includeSelf
	 * @param userId
	 * @param filter
	 * @return
	 */
	public static ArrayList<User> getContactsForUser(boolean includeSelf,
			String userId, String filter) {

		ArrayList<User> contacts = new ArrayList<User>();
		if (includeSelf)
			contacts.add(getUserForId(userId));

		String jString = readActivity(getContactsString(userId));

		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		/**
		 * Convert JSONArray to our user type.
		 */
		try {
			JSONObject con = jObj.getJSONObject("contacts");
			JSONArray jsonArray = con.getJSONArray("actor");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (filter.equals("")) {
					addNewContact(jsonObject, contacts);
				} else {
					JSONArray tags = jsonObject.getJSONArray("tags");
					// Log.i("Contact", "JSon is: " + jsonObject + "\n");
					for (int j = 0; j < tags.length(); j++) {

						if (tags.get(j).toString().equalsIgnoreCase(filter)) {
							addNewContact(jsonObject, contacts);
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contacts;
	}

	public static ArrayList<User> getContactsForUser(boolean includeSelf,
			String userId) {
		return getContactsForUser(includeSelf, userId, "");
	}

	/**
	 * Helper method for creating a new User object.
	 * 
	 * @param jsonObject
	 * @param contacts
	 * @throws JSONException
	 */
	private static void addNewContact(JSONObject jsonObject,
			ArrayList<User> contacts) throws JSONException {
		// Log.i("Contact", "Adding:  " + jsonObject.getString("name") + "\n");
		/**
		 * Can be written shorter when we know what to do with objects being
		 * null. I have put them in vars in case we want to check and set them
		 * here.
		 */
		User newContact = getUserwithId(jsonObject.getInt("id"), contacts);
		String image_url = jsonObject.getString("picture");
		String gender = jsonObject.getString("gender");
		String first_name = jsonObject.getString("name");
		String last_name = jsonObject.getString("nichname");
		String full_name = jsonObject.getString("preferredUsername");
		String bio = jsonObject.getString("note");

		newContact.setImageUrl(image_url);
		if (gender.equalsIgnoreCase("male"))
			newContact.setGender(Gender.Male);
		else
			newContact.setGender(Gender.Female);
		newContact.setFirstName(first_name);
		newContact.setLastName(last_name);
		newContact.setFullName(full_name);
		newContact.setBirthDay(new Date()); // Birthday
		newContact.setBio(bio);

		contacts.add(newContact);
	}

	enum EntityType {
		person, thing, place;
		static EntityType fromString(String str) {
			for (EntityType t : EntityType.values()) {
				if (t.name().equals(str)) {
					return t;
				}
			}
			return null;
		}
	}

	/**
	 * Helper method for creating a new User object.
	 * 
	 * @param jsonObject
	 * @param contacts
	 * @throws JSONException
	 */
	private static User createUser(JSONObject jsonObject) throws JSONException {
		// Log.i("Contact", "Adding:  " + jsonObject.getString("name") + "\n");
		/**
		 * Can be written shorter when we know what to do with objects being
		 * null. I have put them in vars in case we want to check and set them
		 * here.
		 */
		User newUser = new User();
		int user_id = jsonObject.getInt("id");
		String image_url = jsonObject.getString("picture");
		String gender = jsonObject.getString("gender");
		String first_name = jsonObject.getString("name"); // readable name, eg.
		// Ove
		String last_name = jsonObject.getString("nichname"); // complete
		// diaspora name
		// e.g.
		// user01@idea.itu.dk:3000
		String full_name = jsonObject.getString("preferredUsername"); // user
		// name,
		// eg.
		// user01
		String bio = jsonObject.getString("note");

		JSONArray tags = jsonObject.getJSONArray("tags");
		ArrayList<String> tags_list = new ArrayList<String>(tags.length());
		String type = null;
		for (int i = 0; i < tags.length(); i++) {
			tags_list.add(tags.getString(i));
			EntityType t = EntityType.fromString(tags.getString(i));
			if (null != t) {
				type = t.name();
			}
		}

		newUser.setImageUrl(image_url);
		if (gender.equalsIgnoreCase("male"))
			newUser.setGender(Gender.Male);
		else
			newUser.setGender(Gender.Female);
		newUser.setFirstName(first_name);
		newUser.setLastName(last_name);
		newUser.setFullName(full_name);
		newUser.setBirthDay(new Date()); // Birthday
		newUser.setBio(bio);
		newUser.setType(type);
		newUser.setUserId(user_id);

		return newUser;
	}

	/**
	 * Updates a list of users setting the user to shopping if "shopping"
	 * "start" are the last tags on the users activity stream.
	 * 
	 * @param shoppingFriends
	 * @param uId
	 */
	public static void setUserActivity(ArrayList<User> shoppingFriends,
			String uId) {
		String jString = readActivity(getActivityString(uId));
		/**
		 * Server return a JSONObject "aspects" which contain JSONArray of
		 * objects "aspect"
		 */
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONArray jArr = jObj.getJSONArray("stream");
			// Take oldest updates first
			for (int i = jArr.length() - 1; 0 <= i; i--) {
				boolean shopping = false;
				boolean start = false;
				boolean stop = false;
				String data = "";
				String loc = "";
				JSONObject actor = null;
				JSONObject object = null;

				actor = jArr.getJSONObject(i).getJSONObject("actor");
				object = jArr.getJSONObject(i).getJSONObject("object");

				// // Set user
				// JSONArray tags = object.getJSONArray("tags");
				// for (int j = 0; j < tags.length(); j++) {
				// if (tags.get(j).toString().equalsIgnoreCase("shopping"))
				// shopping = true;
				// else if (tags.get(j).toString().equalsIgnoreCase("start"))
				// start = true;
				// else if (tags.get(j).toString().equalsIgnoreCase("stop"))
				// stop = true;
				// else
				// loc = tags.get(j).toString();
				//
				// }

				// Set user v2
				String content = object.getString("content");

				shopping = true;
				if (content.contains("start") || content.contains("stop")) {
					start = content.contains("start") ? true : false;
					String thingName = actor.getString("name");
					Log.d("fetch", "detected start or stop by :" + thingName);

					String userName = thingName.split(" ")[0];
					userName = userName.substring(0, userName.length() - 1);
					// I am getting a message from a THING and here I want to
					// update the status of the person it is associated to
					// here association works by name

					for (User user : shoppingFriends) {
						if (userName.equals(user.getFirstName())) {
							user.setUserActivity(start ? UserActivity.Shopping
									: UserActivity.Unknown);
							break;
						}
					}

				} else if (content.contains("data:")) {
					data = content.split(" ")[0].split(":")[1];
					// TODO find a way to handle this data to the user or user
					// activity
				}

				else if (content.contains("enter") || content.contains("exit")) {
					loc = content.contains("enter") ? actor.getString("name")
							: "";
					//
					// example of the content that I have to parse
					// <a data-hovercard='/people/8'
					// href='/u/bagrollator02' class='mention hovercardable'
					// >Toves Taske Rollator </a> enter <a
					// href=\"/tags/shopping\" class=\"tag\">#shopping</a>
					//
					String thingname = content.split(">")[0].split("<")[0];
					Log.d("fetch activity task", "thing in a new location: "
							+ thingname);
					String userName = thingname.split(" ")[0];
					userName = userName.substring(0, userName.length() - 1);

					for (User user : shoppingFriends) {
						if (userName.equals(user.getFirstName())) {
							user.setLocation(loc);
							break;
						}
					}
				}
				// // Find user among shopping friends and update activity
				// int userId = actor.getInt("id");
				// if (shopping) {
				// for (User u : shoppingFriends) {
				// if (u.getUserId() == userId) {
				// if (start)
				// u.setUserActivity(UserActivity.Shopping);
				// else if (stop)
				// u.setUserActivity(UserActivity.Unknown);
				// }
				// }
				//
				// }

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Needed this for getting the user of this tablet. E.g user01 would not
	 * find himself among his contacts, so how to get data for his own profile?
	 * In general, and for a bigger application, this method may be useful
	 * 
	 * @param id
	 * @return
	 */
	private static User getUserForId(String id) {
		// String JSON_GET_USER_PROFILE =
		// "http://idea.itu.dk:8080/profiles/12.json?user=" + id +
		// "@idea.itu.dk:3000";
		String JSON_GET_USER_PROFILE = "http://idea.itu.dk:8080/me.json?user="
				+ id + "@idea.itu.dk:3000";
		String jString = readActivity(JSON_GET_USER_PROFILE);
		User user = null;
		/**
		 * Server return a JSONObject "aspects" which contain JSONArray of
		 * objects "aspect"
		 */
		JSONObject jObj = null;
		try {
			if (jString == null) {
				Log.d("Fetching user", "empty response");
			}
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// JSONArray jArr;
		// try {
		// jArr = jObj.getJSONArray("actor");
		// if(jArr.length() > 0){
		// JSONObject jsonObject =
		// jArr.getJSONObject(0).getJSONObject("profile");
		// user = new User();
		// user.setUserId(jsonObject.getInt("id"));
		// user.setImageUrl(jsonObject.getString("image_url"));
		// if(jsonObject.getString("gender").equalsIgnoreCase("male")) //no
		// convention, I had 'gender' from the start
		// user.setGender(Gender.Male);
		// else user.setGender(Gender.Female);
		// user.setFirstName(jsonObject.getString("full_name"));
		// user.setLastName(jsonObject.getString("diaspora_handle"));
		// user.setFullName(jsonObject.getString("diaspora_handle"));
		// user.setBio(jsonObject.getString("bio"));
		// }
		//
		// } catch (JSONException e) {
		// e.printStackTrace(); //To change body of catch statement use File |
		// Settings | File Templates.
		// }

		JSONObject actor = null;
		try {
			actor = jObj.getJSONObject("actor");
			user = createUser(actor);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Get activity of objects - rollators and the like. This method almost is a
	 * dublicate of the one for person activity...
	 */
	public static ArrayList<User> getObjectsActivity(String uId) {
		ArrayList<User> activeObjects = new ArrayList<User>();
		String jString = readActivity(getActivityString(uId));

		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONArray jArr = jObj.getJSONArray("stream");
			// Take oldest updates first
			for (int i = jArr.length() - 1; 0 <= i; i--) {
				boolean shopping = false;
				boolean start = false;
				boolean stop = false;
				boolean isObject = false;
				JSONObject actor = null;
				JSONObject object = null;

				// See if this actor is an objects
				actor = jArr.getJSONObject(i).getJSONObject("actor");
				JSONArray actorTags = actor.getJSONArray("tags");
				for (int h = 0; h < actorTags.length(); h++) {
					// Last tag and not an object
					if (actorTags.getString(h).equalsIgnoreCase("thing"))
						isObject = true;
				}
				if (!isObject)
					continue;

				// Add object to active objects, if not there already and update
				// its status
				FetchActivityTask.addNewContact(actor, activeObjects);

				object = jArr.getJSONObject(i).getJSONObject("object");

				JSONArray tags = object.getJSONArray("tags");
				for (int j = 0; j < tags.length(); j++) {
					if (tags.get(j).toString().equalsIgnoreCase("shopping"))
						shopping = true;
					else if (tags.get(j).toString().equalsIgnoreCase("start"))
						start = true;
					else if (tags.get(j).toString().equalsIgnoreCase("stop"))
						stop = true;
				}
				// Find user among shopping friends and update activity
				int userId = actor.getInt("id");
				if (shopping) {
					for (User u : activeObjects) {
						if (u.getUserId() == userId) {
							if (start)
								u.setUserActivity(UserActivity.Shopping);
							else if (stop)
								u.setUserActivity(UserActivity.Unknown);
						}
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return activeObjects;
	}

	/**
	 * One ugly mother. Returns a list of seven list one for each day. Each of
	 * these day lists will cotain persons who shared a #shopping #start. Needs
	 * to work for things also but didn't understand that part when implementing
	 * this method.
	 * 
	 * @param id
	 * @return
	 */
	// public static ArrayList<ArrayList<Movable>> getWeekActivity(String id) {

	public static WeekActivities getWeekActivity(String id,
			ArrayList<User> entities) {
		String JSON_GETWEEK_ACTIVITY = "http://idea.itu.dk:8080/activities/shopping/week.json?user="
				+ id + "@idea.itu.dk:3000";
		String jString = readActivity(JSON_GETWEEK_ACTIVITY);

		/**
		 * Server return a JSONObject "aspects" which contain JSONArray of
		 * objects "aspect"
		 */
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject stream = null;
		WeekActivities weekActivities = new WeekActivities();

		try {
			stream = jObj.getJSONObject("stream");

			// Get all 7 days a week,
			for (int i = 0; i < 7; i++) {

				// If days is there we parse all activity
				// check that for that day there were activities recorded
				if (!stream.isNull("" + i)) {
					// take all the activities for a certain day
					JSONArray dayArr = stream.getJSONArray("" + i);

					// Check all activities
					for (int h = 0; h < dayArr.length(); h++) {
						// parse the event post
						JSONObject event = dayArr.getJSONObject(h);
						int actorId = event.getJSONObject("actor").getInt("id"); // the
						// machine
						// name
						User actor = Utilities
								.getContactById(actorId, entities);

						if (actor != null) {
							// if there is new activity add it to the counter
							// and save it in the WeekElement
							if (event.getJSONObject("object").getString(
									"content").contains("start") // started
																	// activity
									|| event.getJSONObject("object").getString(
											"content").contains("enter") // entered
									// a
									// location
									|| event.getString("verb")
											.equalsIgnoreCase("photo")) { // shared
																			// a
																			// spark

								weekActivities
										.addActivityPerDayByUser(i, actor);
								Log.d("FetchActivity week",
										"added activity for day: " + i
												+ " for actor "
												+ actor.getFirstName());
							}
						}

						// // add found activities to its day i
						// if (shopping && start) {
						// activityObjects.get(i).add(
						// new ShoppingCart(GalleryActivity
						// .getContext()));
						// }
					}// end for
				}// end control on stream being null for a day
			}
		} catch (JSONException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}
		return weekActivities;
	}

	/**
	 * Filters all shopping offers in stream for the friends. For this string is
	 * can be anyone of the shopping friends. Who shared what offer will have to
	 * be filtered afterwards, see setSharedByUserId on Movable
	 * 
	 * @param id
	 * @return
	 */
	public static ArrayList<ShoppingOffer> getAllOffersForUser(String id) {
		ArrayList<ShoppingOffer> so = new ArrayList<ShoppingOffer>();
		String JSON_GET_OFFERS_ACTIVITY = "http://idea.itu.dk:8080/activities/shopping.json?user="
				+ id + "@idea.itu.dk:3000";
		Log.d("Fetch all offers", "request: " + JSON_GET_OFFERS_ACTIVITY);
		String jString = readActivity(JSON_GET_OFFERS_ACTIVITY);

		/**
		 * Server return a JSONObject "aspects" which contain JSONArray of
		 * objects "aspect"
		 */
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONArray jArr = jObj.getJSONArray("stream");

			// Take oldest updates first
			for (int i = 0; i < jArr.length(); i++) {

				JSONObject object = jArr.getJSONObject(i);
				if (object.getString("verb").equalsIgnoreCase("Photo")) {
					int objectId = object.getInt("id");
					JSONObject tobject = object.getJSONObject("object");
					int actorId = object.getJSONObject("actor").getInt("id");

					// String actor_name =
					// Utilities.getContactById(Integer.parseInt(objectId),getContacts()).getFullName();
					String filename = actorId + "_offer_" + objectId + ".jpg";

					String originalImageUrl = tobject
							.getString("remotePhotoPath")
							+ "" + tobject.getString("remotePhotoName");

					Bitmap b = ((BitmapDrawable) fetchBitmap(originalImageUrl,
							filename)).getBitmap(); // getImage(originalImageUrl,filename);
					ShoppingOffer s = new ShoppingOffer(GalleryActivity
							.getContext(), b);
					s.setSharedByUserId(object.getJSONObject("actor").getInt(
							"id"));
					s.setName(tobject.getString("remotePhotoName"));
					so.add(s);
					System.gc();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return so;
	}

	// Get image from server helper methods
	private static Bitmap getImage(String url, String filename) {

		// Drawable image = ImageOperations(GalleryActivity.getContext(), url,
		// "image.jpg");
		// return ((BitmapDrawable)image).getBitmap();
		return ((BitmapDrawable) fetchBitmap(url, filename)).getBitmap();
	}

	private static Drawable fetchBitmap(String url, String saveFilename) {
		offers_dir.mkdirs();

		String filename = offers_dir.getPath() + "/" + saveFilename;
		String filename_tab = offers_dir.getPath() + "/tab_" + saveFilename;

		File f = new File(filename);
		Drawable bitmap = null;
		try {
			if (!f.exists()) {
				InputStream is = (InputStream) fetch(url);

				try {
					// save image to filename
					FileOutputStream out = new FileOutputStream(filename);
					IO.copy(is, out); // copy copy image to file ;)
					out.close();

					// create a thumbnail to display in the view
					Bitmap scaled = Utilities.createThumbnail(Uri
							.parse("file://" + filename));

					FileOutputStream outTabnail = new FileOutputStream(
							filename_tab);
					scaled
							.compress(Bitmap.CompressFormat.JPEG, 100,
									outTabnail);

					bitmap = new BitmapDrawable(scaled);
					outTabnail.close();

					System.gc(); // call garbage collector
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				FileInputStream is = new FileInputStream(new File(filename_tab));
				bitmap = Drawable.createFromStream(is, filename_tab);
				is.close();
			}
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Drawable ImageOperations(Context ctx, String url,
			String saveFilename) {
		try {
			InputStream is = (InputStream) FetchActivityTask.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			is.close();
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	// END

	private static String getActivityString(String userId) {
		return "http://idea.itu.dk:8080/activities/shopping/today.json?user="
				+ userId + "@idea.itu.dk:3000";
	}

	private static String getContactsString(String userId) {
		return "http://idea.itu.dk:8080/activities/shopping/contacts.json?user="
				+ userId + "@idea.itu.dk:3000";
	}

	/**
	 * Filters all shopping offers in stream for the friends. For this string is
	 * can be anyone of the shopping friends. Who shared what offer will have to
	 * be filtered afterwards, see setSharedByUserId on Movable
	 * 
	 * @param id
	 * @return
	 */
	public static ArrayList<ShoppingOffer> getOffersByUser(String id, User user) {
		ArrayList<ShoppingOffer> so = new ArrayList<ShoppingOffer>();
		String JSON_GET_USER_OFFERS_ACTIVITY = "http://idea.itu.dk:8080/activities/shopping.json?user="
				+ id + "@idea.itu.dk:3000";
		Log.d("Fetch user uploaded offers", "request: "
				+ JSON_GET_USER_OFFERS_ACTIVITY);
		String jString = readActivity(JSON_GET_USER_OFFERS_ACTIVITY);

		/**
		 * Server return a JSONObject "aspects" which contain JSONArray of
		 * objects "aspect"
		 */
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(jString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONArray jArr = jObj.getJSONArray("stream");

			// Take oldest updates first
			for (int i = 0; i < jArr.length(); i++) {

				JSONObject object = jArr.getJSONObject(i);
				if (object.getString("verb").equalsIgnoreCase("Photo")) {
					int objectId = object.getInt("id");
					JSONObject tobject = object.getJSONObject("object");
					int actorId = object.getJSONObject("actor").getInt("id");
					Log.d("Fetch offers", "actor id " + actorId
							+ " =? user id " + user.getUserId());
					if (actorId == user.getUserId()) {
						Log.d("Fetch offers", "matching");
						// String actor_name =
						// Utilities.getContactById(Integer.parseInt(objectId),getContacts()).getFullName();
						String filename = actorId + "_offer_" + objectId
								+ ".jpg";

						String originalImageUrl = tobject
								.getString("remotePhotoPath")
								+ "" + tobject.getString("remotePhotoName");

						Bitmap b = ((BitmapDrawable) fetchBitmap(
								originalImageUrl, filename)).getBitmap(); // getImage(originalImageUrl,filename);
						ShoppingOffer s = new ShoppingOffer(GalleryActivity
								.getContext(), b);
						s.setSharedByUserId(object.getJSONObject("actor")
								.getInt("id"));
						s.setName(tobject.getString("remotePhotoName"));
						so.add(s);
						System.gc();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return so;
	}

}
