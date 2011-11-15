package com.shopping;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 18.16
 * To change this template use File | Settings | File Templates.
 */
public class Movable implements Parcelable{

    protected final Bitmap bitmap;

    /**
     * Position of movable
     */
    protected int x = 10, y = 10;
    protected int xDirection = 2, yDirection = 2;

    /**
     *  Movables fly around
     */
    private static final Random rn = new Random();

    public Movable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    protected Bitmap getBitmap(){
        return bitmap;
    }

    public void updatePosition(int width, int height) {
       if(x + bitmap.getWidth() >= width || x <= 0) xDirection *= -1;
        //todo fix canvas screen height
       if(y + bitmap.getHeight() + 100 >= height || y <= 0) yDirection *= -1;
       x += xDirection;
       y += yDirection;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }

   // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        bitmap.writeToParcel(out, PARCELABLE_WRITE_RETURN_VALUE);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movable> CREATOR = new Parcelable.Creator<Movable>() {
        public Movable createFromParcel(Parcel in) {
            return new Movable(in);
        }

        public Movable[] newArray(int size) {
            return new Movable[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Movable(Parcel in) {
        bitmap = Bitmap.CREATOR.createFromParcel(in);
    }
}
