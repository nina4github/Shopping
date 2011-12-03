package com.shopping;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class ShoppingOverview extends Activity {

    private ArrayList<User> activeUser;
    private ArrayList<User> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The activity viev runs in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_overview);
        Bundle bundle = getIntent().getExtras();
        activeUser = bundle.getParcelableArrayList(HomeActivity.ACTIVE_USERS);
        contacts = bundle.getParcelableArrayList(HomeActivity.CONTACTS);

        for(User u : activeUser){
            addOverViewActivity(u);
        }

        if(activeUser.size() > 0)
            addDetailActivity(activeUser.get(0));
    }

    /**
     * Display all active users, that is, shoppers.
     * @param user
     */
    private void addOverViewActivity(User user) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.activityoverviewview);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //Code dublication, method needed for these lines and loop
        ImageButton btn = new ImageButton(this);
        btn.setId(user.getUserId());
        btn.setLayoutParams(p);
        btn.setImageBitmap(user.getUserImage());
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                for(User u : activeUser){
                    if(u.getUserId()==view.getId()){
                        addDetailActivity(u);
                        break;
                    }
                }
            }
        });
        linearLayout.addView(btn);

        //Add all shopping offers to the overview for this user
        for(Movable shoppingOffer : user.getOffers()){
            ImageButton btn2 = new ImageButton(this);
            btn2.setId(user.getUserId());
            btn2.setLayoutParams(p);
            btn2.setImageBitmap(shoppingOffer.getBitmap());
            btn2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    for(User u : activeUser){
                        if(u.getUserId()==view.getId()){
                            addDetailActivity(u);
                            break;
                        }
                    }
                }
            });
            linearLayout.addView(btn2);
        }
    }

    /**
     *  Display detailed view of user and her activities
     */
    private void addDetailActivity(User user) {
        android.util.Log.d("called listener", "movable " + user.toString());
        final int userId = user.getUserId();
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.activitydetailview);
        linearLayout.removeAllViews();
        linearLayout.invalidate();

        //Clickable user
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final ImageButton profile = new ImageButton(this);
        profile.setLayoutParams(p);
        profile.setId(user.getUserId());
        profile.setImageBitmap(user.getUserImage());
        profile.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                User selectedUser = null;
                for(User u : activeUser){
                    if(u.getUserId() == view.getId()){
                        selectedUser = u;
                        break;
                    }
                }
                Intent intent = new Intent(ShoppingOverview.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.SELECTED_USER, selectedUser);
                startActivity(intent);
            }
        });
        TextView nameView = new TextView(HomeActivity.getContext());
        nameView.setLayoutParams(p);
        String text = "";
        if(user.getFullName()==null || user.getFullName().isEmpty())
            text = "" + user.getUserId();
        else text = user.getFullName();
        nameView.setText(text);
        linearLayout.addView(profile);
        linearLayout.addView(nameView);

        //User location text
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = (TextView)findViewById(R.id.shoppinglocation);
        tv.setText(user.getFullName() + " handler ind i " + user.getLocation());

        //User offers
        final LinearLayout offersLayout = (LinearLayout)findViewById(R.id.activitydetailofferscontainer);
        offersLayout.removeAllViews();
        offersLayout.invalidate();
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv2 = new TextView(HomeActivity.getContext());
        nameView.setLayoutParams(p3);
        tv2.setTextSize(20.5f);
        if(user.getOffers() == null || user.getOffers().size() == 0){
            tv2.setText(user.getFullName() + " har ikke delt nogle tilbud endnu");
        }else{
            tv2.setText("Tilbud: ");
        }
        offersLayout.addView(tv2);

        for(Movable so : user.getOffers()){
            final int id = so.getId();
            final ImageButton offer = new ImageButton(this);
            offer.setLayoutParams(p3);
            offer.setImageBitmap(so.getBitmap());
            offer.setId(so.getId());
            offer.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Movable shoppingOffer = null;
                    User user = null;
                    /**
                     * Hidious search for user that has put offer. Will be improved when we have
                     * content providers - offers can now who created them
                     * @param id
                     * @return
                     */
                    for(User u : activeUser){
                        for(Movable so : u.getOffers()){
                            if(so.getId()==id){
                                shoppingOffer = so;
                                user = u;
                                break;
                            }
                        }
                    }
                    Intent intent = new Intent(ShoppingOverview.this, OfferActivity.class);
                    intent.putExtra(OfferActivity.SELECTED_SHOPPING_OFFER, shoppingOffer);
                    intent.putExtra(ProfileActivity.SELECTED_USER, user);
                    startActivity(intent);
                }
            });
            offersLayout.addView(offer);
        }
    }
}
