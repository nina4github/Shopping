package com.shopping;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 18.16
 * To change this template use File | Settings | File Templates.
 */
public class ShoppingCart extends Movable{
    public ShoppingCart(Context context){
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.cart));
    }
    public ShoppingCart(Context context, int drawableResource){
        super(BitmapFactory.decodeResource(context.getResources(), drawableResource));
    }
}
