package com.shopping;

import android.content.Context;
import android.graphics.BitmapFactory;

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
       super(BitmapFactory.decodeResource(context.getResources(), R.drawable.shoppingcart));
    }

    @Override
    public String toString(){
        return "Cart";
    }
}
