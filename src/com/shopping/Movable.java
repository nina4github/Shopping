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
    private int id;

    /**
     * Position of movable
     */
    private int x;


    private int y;
    private int xDirection;
    private int yDirection;

    public Movable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    protected Bitmap getBitmap(){
        return bitmap;
    }

    public void updatePosition() {
       x += xDirection;
       y += yDirection;
    }

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getXDirection(){ return xDirection; }
    public int getYDirection(){ return yDirection; }
    public void setXDirection(int dir){ this.xDirection=dir; }
    public void setYDirection(int dir){ this.yDirection=dir; }

   // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        bitmap.writeToParcel(out, PARCELABLE_WRITE_RETURN_VALUE);
        out.writeInt(id);
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
        setId(in.readInt());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
