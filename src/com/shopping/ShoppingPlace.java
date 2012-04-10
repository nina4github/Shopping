package com.shopping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 21.58
 * To change this template use File | Settings | File Templates.
 */
public class ShoppingPlace extends Movable{
    public ShoppingPlace(Context context) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.tilbud));
    }
    public ShoppingPlace(Context context, Bitmap bitmap) {
        super(bitmap);
    }
}
