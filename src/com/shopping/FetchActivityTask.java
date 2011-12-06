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
import org.eclipse.jetty.util.ajax.JSON;
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


    private static ArrayList<User> contacts;
    @Override
    protected Boolean doInBackground(String... strings){
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Test users.
     * @param context
     * @return
     */
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

    public static ArrayList<User> getContactsForUser(boolean includeSelf, String userId) {
        contacts = new ArrayList<User>();
        if(includeSelf)
            contacts.add(getUserForId(userId));
        String jString = readActivity(getContactsString(userId));
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
            JSONObject contacts = jObj.getJSONObject("contacts");
            JSONArray jsonArray = contacts.getJSONArray("actor");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray tags = jsonObject.getJSONArray("tags");
                Log.i("Contact", "JSon is: " + jsonObject + "\n");
                for(int j = 0; j < tags.length(); j++){
                    if(tags.get(j).toString().equalsIgnoreCase("person")){
                        addNewContact(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private static void addNewContact(JSONObject jsonObject) throws JSONException {
        Log.i("Contact", "Adding:  " + jsonObject.getString("name") + "\n");
        /**
         * Can be written shorter when we know what to do with objects being null.
         * I have put them in vars in case we want to check and set them here.
         */
        User newContact = getUserwithId(jsonObject.getInt("id"));
        String image_url = jsonObject.getString("picture");
        String gender = jsonObject.getString("gender");
        String first_name = jsonObject.getString("name");
        String last_name = jsonObject.getString("nichname");
        String full_name = jsonObject.getString("preferredUsername");
        String bio = jsonObject.getString("note");

        newContact.setImageUrl(image_url);
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

    public static void setUserActivity(ArrayList<User> shoppingFriends, String uId) {
        String jString = readActivity(getActivityString(uId));
        /**
         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
         */
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(jString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jArr = jObj.getJSONArray("stream");
            //Take oldest updates first
            for(int i = jArr.length()-1; 0 <= i; i--){
                boolean shopping  = false;
                boolean start     = false;
                boolean stop     = false;
                JSONObject actor  = null;
                JSONObject object = null;

                actor  = jArr.getJSONObject(i).getJSONObject("actor");
                object = jArr.getJSONObject(i).getJSONObject("object");

                //Set user
                JSONArray tags = object.getJSONArray("tags");
                for(int j = 0; j < tags.length(); j++){
                    if(tags.get(j).toString().equalsIgnoreCase("shopping"))
                        shopping = true;
                    else if(tags.get(j).toString().equalsIgnoreCase("start"))
                        start = true;
                    else if(tags.get(j).toString().equalsIgnoreCase("stop"))
                        stop = true;

                }
                //Find user among shopping friends and update activity
                int userId = actor.getInt("id");
                if(shopping){
                    for(User u : shoppingFriends){
                        if(u.getUserId()==userId){
                            if(start)
                                u.setUserActivity(UserActivity.Shopping);
                            else if(stop)
                                u.setUserActivity(UserActivity.Unknown);
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static User getUserForId(String id){
        String JSON_GET_USER_PROFILE = "http://idea.itu.dk:8080/profiles/12.json?user=" + id + "@idea.itu.dk:3000";
        String jString = readActivity(JSON_GET_USER_PROFILE);
        User user=null;
        /**
         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
         */
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(jString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jArr;
        try {
            jArr = jObj.getJSONArray("profiles");
            if(jArr.length() > 0){
                JSONObject jsonObject = jArr.getJSONObject(0).getJSONObject("profile");
                user = new User();
                user.setUserId(jsonObject.getInt("id"));
                user.setImageUrl(jsonObject.getString("image_url"));
                if(jsonObject.getString("gender").equalsIgnoreCase("male"))
                    user.setGender(Gender.Male);
                else user.setGender(Gender.Female);
                user.setFirstName(jsonObject.getString("full_name"));
                user.setLastName(jsonObject.getString("diaspora_handle"));
                user.setFullName(jsonObject.getString("diaspora_handle"));
                user.setBio(jsonObject.getString("bio"));
            }

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return user;
    }

//    public static ArrayList<ArrayList<Movable>> getWeekActivity(String id){
//        String JSON_GETWEEK_ACTIVITY = "http://idea.itu.dk:8080/activities/shopping/week.json?user="+id+"@idea.itu.dk:3000";
//        String jString = readActivity(JSON_GETWEEK_ACTIVITY);
//        ArrayList<ArrayList<Movable>> activityObjects = new ArrayList<ArrayList<Movable>>();
//        for(int j = 0; j < 7; j++){
//            activityObjects.add(j, new ArrayList<Movable>());
//        }
//        /**
//         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
//         */
//        JSONObject jObj = null;
//        try {
//            jObj = new JSONObject(jString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONObject stream = null;
//        try {
//            stream = jObj.getJSONObject("stream");
//            //Get all 7 days a week
//            for(int i = 0; i < 7; i++){
//                //If days is there we parse all activity
//                if(!stream.isNull("" + i)){
//                    JSONArray dayArr = stream.getJSONArray("" + i);
//                    //Check all activities
//                    for(int h = 0; h < dayArr.length(); h++){
//                        JSONObject day = dayArr.getJSONObject(h);
//                        String actor = day.getJSONObject("actor").getString("preferredUsername");
//                        //We look for activity of this single user, continue if this is not us
//                        if(!actor.equalsIgnoreCase(HomeActivity.USER_ID))continue;
//                        boolean shopping = false;
//                        boolean start    = false;
//                        JSONArray tags = day.getJSONObject("object").getJSONArray("tags");
//                        for(int k = 0; k < tags.length(); k++){
//                            if(tags.get(k).toString().equalsIgnoreCase("shopping")){
//                                shopping=true;
//                            }else if(tags.get(k).toString().equalsIgnoreCase("start")){
//                                start = true;
//                            }
//                        }
//                        //add found activities to its day i
//                        if(shopping && start) {
//                            activityObjects.get(i).add(new ShoppingCart(GalleryActivity.getContext()));
//                        }
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        return activityObjects;
//    }

    /**
     * Filters all shopping offers in stream for the user.
     * @param id
     * @return
     */
    public static ArrayList<ShoppingOffer> getAllOffersForUser(String id){
        String JSON_GET_OFFERS_ACTIVITY = "http://idea.itu.dk:8080/activities/shopping.json?user="+id+"@idea.itu.dk:3000";
        String jString = readActivity(JSON_GET_OFFERS_ACTIVITY);
        ArrayList<ShoppingOffer> offers = new ArrayList<ShoppingOffer>();
        /**
         * Server return a JSONObject "aspects" which contain JSONArray of objects "aspect"
         */
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(jString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jArr = jObj.getJSONArray("stream");
            //Take oldest updates first
            for(int i = 0; i < jArr.length(); i++){
                 JSONObject object = jArr.getJSONObject(i);
                 if(object.getString("verb").equalsIgnoreCase("Photo")){
                     //Add shopping offer
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return offers;
    }

    private static String getActivityString(String userId){
     return  "http://idea.itu.dk:8080/activities/shopping.json?user=" + userId +"@idea.itu.dk:3000";
    }

        private static String getContactsString(String userId){
     return  "http://idea.itu.dk:8080/activities/shopping/contacts.json?user=" + userId +"@idea.itu.dk:3000";
    }


}
