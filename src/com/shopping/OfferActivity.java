package com.shopping;

import android.app.*;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 27/11/11
 * Time: 15.27
 * To change this template use File | Settings | File Templates.
 */
public class OfferActivity extends Activity {
    private User user;
    private Movable offer;
    public static String SELECTED_SHOPPING_OFFER = "selected_shopping_offer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                //The shopping activity viev runs in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.shoppingoffer);
        Bundle bundle = getIntent().getExtras();
        user = (User)bundle.getParcelable(ProfileActivity.SELECTED_USER);
        offer = (Movable)bundle.getParcelable(OfferActivity.SELECTED_SHOPPING_OFFER);
        ImageView iv = (ImageView)findViewById(R.id.offerView);
        iv.setImageBitmap(offer.getBitmap());
        TextView tv = (TextView)findViewById(R.id.offerViewByUser);
        String text = "Tilbud blev delt af ";
        if(user.getFullName()==null || user.getFullName().isEmpty())
            text += " " + user.getUserId();
        else text += user.getFullName() + ". ";
        text += "\nTilbud id " + offer.getId();
        tv.setText(text);
    }
}
