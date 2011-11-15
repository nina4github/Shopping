package com.shopping;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 21.58
 * To change this template use File | Settings | File Templates.
 */
public class ShoppingOffer extends Movable{
    public ShoppingOffer(Context context) {
       super(BitmapFactory.decodeResource(context.getResources(), R.drawable.offer));
    }

    @Override
    public String toString(){
        return "Offer";
    }
}
