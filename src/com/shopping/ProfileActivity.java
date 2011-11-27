package com.shopping;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 27/11/11
 * Time: 14.42
 * To change this template use File | Settings | File Templates.
 */
public class ProfileActivity extends android.app.Activity {
    private User user;
    private View previousView;
    public static String SELECTED_USER = "selected_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Bundle bundle = getIntent().getExtras();
        user = (User)bundle.getParcelable(ProfileActivity.SELECTED_USER);
        ImageView iv = (ImageView)findViewById(R.id.userView);
        iv.setImageBitmap(user.getUserImage());
        TextView tv = (TextView)findViewById(R.id.userTextView);
        String text = "";
        if(user.getFullName()==null || user.getFullName().isEmpty())
            text = "" + user.getUserId();
        else text = user.getFullName();
        tv.setText(text);

        setupButtonListeners();
    }


    private void setProfileStatus(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.statusLayout);
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
        String text = user.getFullName() + " seneste aktivitet\n";
        text +=  user.getFullName() + " forrige aktivitet\n";
        statusText.setText(text);
        statusLayout.addView(statusText);
    }

    private void setWeekOverview(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.statusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);
    }

    private void setSparks(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.statusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);
    }

    private void setShoppingStats(View view) {
        RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.statusLayout);
        statusLayout.removeAllViews();
        statusLayout.invalidate();
        updateButtonColor(view);
    }


    private void setupButtonListeners() {
        Button b = (Button)findViewById(R.id.profileBtn1);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setProfileStatus( view);
            }
        });
        //Initialize with the profile view set
        setProfileStatus(b);

        b = (Button)findViewById(R.id.profileBtn2);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setWeekOverview( view);
            }
        });

        b = (Button)findViewById(R.id.profileBtn3);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setSparks( view);
            }
        });

        b = (Button)findViewById(R.id.profileBtn4);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setShoppingStats( view);
            }
        });
    }

    private void updateButtonColor(View newButton){
        //Update button row
        if(previousView != null)
            previousView.setBackgroundColor(getResources().getColor(R.color.white));
        newButton.setBackgroundColor(getResources().getColor(R.color.mygreen));
        previousView = newButton;
    }
}
