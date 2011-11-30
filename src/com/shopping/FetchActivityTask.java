package com.shopping;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 21/11/11
 * Time: 23.01
 * To change this template use File | Settings | File Templates.
 */                                           //Input, Progress Report Type, Result Type
public class FetchActivityTask extends AsyncTask<String, Integer, Boolean> {
    private static final String JSON_GET_ACTIVITY =  "http://idea.itu.dk:8080/activities.json?user=" + HomeActivity.USER_ID +"@idea.itu.dk:3000";
    private static final String JSON_GET_CONTACTS =  "http://idea.itu.dk:8080/activities/shopping/contacts.json?user=" + HomeActivity.USER_ID +"@idea.itu.dk:3000";
    private static ArrayList<User> contacts;
    @Override
    protected Boolean doInBackground(String... strings){
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    public static ArrayList<User> getTestUsers(Context context){
        ArrayList<User> users = new ArrayList<User>();

        User a = new User();
        a.setUserId(0);
        a.setUserActivity(UserActivity.Shopping);
        a.setFullName("Anders");

        User b = new User();
        b.setUserId(1);
        b.setUserImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.senior2_80px));
        b.setFullName("Bjørn");
        b.setUserActivity(UserActivity.Shopping);
        ShoppingOffer so = new ShoppingOffer(context);
        so.setId(2);
        b.addOffer(so);

        User c = new User();
        c.setUserId(3);
        c.setUserImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.senior3_80px));
        c.setFullName("Carsten");
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
     * TODO non static and async task
     * @return
     */
    public static ArrayList<User> getActivity(){
        if(contacts == null)
            contacts = getContacts();
        ArrayList<User> activeUsers = new ArrayList<User>();
        String jString = readActivity(JSON_GET_ACTIVITY);
        /**
         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
         */
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(jString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         *  Convert JSONArray to our types.
         */
        try {
            JSONArray jsonArray = jObj.getJSONArray("aspects");
            Log.i(FetchActivityTask.class.getName(), "Number of entries " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("aspect");

                if(jsonObject.getString("name").equalsIgnoreCase("shopping")){
                    User u = getUserwithId(jsonObject.getInt("user_id"));
                    u.setUserActivity(UserActivity.Shopping);
                    activeUsers.add(u);
                    Log.i(FetchActivityTask.class.getName(), "User id found: " + jsonObject.getString("user_id"));
                }else{
                    Log.i(FetchActivityTask.class.getName(), "User id found: " + jsonObject.getString("user_id"));
                    Log.i(FetchActivityTask.class.getName(), "User activity: " + jsonObject.getString("name"));
                    /**
                     *
                     * set stuff like
                     *         {"name":"shopping",
                     "created_at":"2011-11-13T14:10:06Z",
                     "updated_at":"2011-11-25T10:04:05Z",
                     "order_id":null,
                     "id":56,
                     "user_id":12,
                     "contacts_visible":true}
                     */
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activeUsers;
    }

    /**
     * Get a new user if user is not already in our collection
     * @param user_id
     * @return
     */
    private static User getUserwithId(int user_id) {
        User newUser = null;
        for(User u : contacts){
            if(u.getUserId() == user_id)
                return u;
        }
        newUser = new User();
        newUser.setUserId(user_id);
        return newUser;
    }

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
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(FetchActivityTask.class.toString(), "Failed to download JSON statuscode: " + statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static ArrayList<User> getContacts() {
        contacts = new ArrayList<User>();
        String jString = readActivity(JSON_GET_CONTACTS);
        /**
         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
         */
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(jString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         *  Convert JSONArray to our user type.
         */
        try {
            JSONArray jsonArray = jObj.getJSONArray("contacts");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("profile");
                Log.i("image url medium", "JSon is: " + jsonObject);
                addNewContact(jsonObject);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private static void addNewContact(JSONObject jsonObject) throws JSONException {
        /**
         * Can be written shorter when we know what to do with objects being null.
         * I have put them in vars in case we want to check and set them here.
         */
        User newContact = getUserwithId(jsonObject.getInt("id"));
        String image_url = jsonObject.getString("image_url");
        String image_url_medium = jsonObject.getString("image_url_medium");
        String image_url_small = jsonObject.getString("image_url_small");
        String location = jsonObject.getString("location");
        String gender =                             jsonObject.getString("gender");
        String first_name = jsonObject.getString("first_name");
        String last_name = jsonObject.getString("last_name");
        String full_name = jsonObject.getString("full_name");
        String birthday = jsonObject.getString("birthday");
        String bio = jsonObject.getString("bio");

        newContact.setImageUrl(image_url);
        newContact.setImageUrlMedium(image_url_medium);
        newContact.setImageUrlSmall(image_url_small);
        newContact.setLocation(location);
        if(gender.equalsIgnoreCase("male"))
            newContact.setGender(Gender.Male);
        else newContact.setGender(Gender.Female);
        newContact.setFirstName(first_name);
        newContact.setLastName(last_name);
        newContact.setFullName(full_name);
        newContact.setBirthDay(new Date()); //Birthday
        newContact.setBio(bio);

        contacts.add(newContact);
    }
}
