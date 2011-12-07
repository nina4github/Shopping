package com.shopping;

import android.content.Context;
import android.graphics.*;
import android.os.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 16.32
 * View doing the actual annimations
 */
public class HomeActivityView extends View {

    private static final String TAG = "HomeActivityView";
    private int touch;

    private int movableOffset = 50;

    /**
     *  Movables fly around
     */
    private static final Random rn = new Random();

    /**
     * States for resuming
     */
    private static int mState = -1;
    public static final int HIDDEN = 0;
    public static final int VISIBLE = 1;

    /**
     * Animation speed
     */
    private long mMoveDelay = 10;

    private ArrayList<Movable> carts;
    private ArrayList<Movable> offers;
    //We always put one cart on the screen to make the screen look alive
    private Movable defaultCart;
    private int x, y=0;
    private Paint paint = new Paint();

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     * Note to self:
     * "Handler can post methods onto the thread in which the handler was created."
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();



    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if(!(mState == HIDDEN)){
                HomeActivityView.this.update();
                HomeActivityView.this.invalidate();
            }
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    private MyInterruptHandler myInterruptHandler;
    private boolean adding;

    //CONSTRUCTORS
    public HomeActivityView(Context context) {
        super(context);
        init();
    }

    public HomeActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFocusable(true);
        carts = new ArrayList<Movable>();
        offers = new ArrayList<Movable>();

        //Put cart somewhere meaningful
        //For bottom of screen we can start offscreen
        defaultCart = new ShoppingCart(getContext(), R.drawable.homecart);
        defaultCart.setX(defaultCart.getBitmap().getWidth() * -1);
        //defaultCart.setY(5);
        defaultCart.setXDirection(1);
        defaultCart.setYDirection(1);

        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == 1){
                    //Todo pause animations and save state
                    saveState();
                    setState(HIDDEN);
                    if (myInterruptHandler != null)
                        myInterruptHandler.myInterrupt();
                }
                return true;
            }
        });
    }

    protected void addShopper(Movable sc, boolean flashScreen){
        if(flashScreen)touch = 1; //see draw method for function of touch counter
        movableOffset+=10; //just so objects are not on top of eachother
        sc.setX(movableOffset);
        sc.setY(150);
        synchronized (carts){
            if(rn.nextInt(1) == 1)
                sc.setXDirection(rn.nextInt(2) + 1);
            else
                sc.setXDirection(rn.nextInt(2) - 2);
            if(rn.nextInt(1) == 1)
                sc.setYDirection(rn.nextInt(2) + 1);
            else
                sc.setYDirection(rn.nextInt(2) - 2);

            this.carts.add(sc);
        }
    }

    public void removeShopper(int id) {
        if(carts.size()>0)
            synchronized (carts){
                for(Movable cart : carts){
                    if(cart.getId()==id)   {
                        carts.remove(cart);
                        return;
                    }
                }
            }
    }


    public void addOffer(Movable so, boolean flashScreen) {
        if(flashScreen)touch = 1;
        movableOffset+=60;
        so.setX(movableOffset);
        so.setY(300);
        synchronized (offers){
            if(rn.nextInt(1) == 1)
                so.setXDirection(rn.nextInt(2) + 1);
            else
                so.setXDirection(rn.nextInt(2) - 2);
            if(rn.nextInt(1) == 1)
                so.setYDirection(rn.nextInt(2) + 1);
            else
                so.setYDirection(rn.nextInt(2) - 2);

            this.offers.add(so);
        }
    }

    public void removeOffer(int offerId) {
        synchronized (offers){
            for(Movable so : offers){
                if(so.getId() == offerId){
                    offers.remove(so);
                    break;
                }
            }
        }
    }


    /**
     *
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        //Todo, list of cart coordinates
        //   map.putSerializable("mCartList", carts);
        return map;
    }

    /**
     */
//    public void restoreState(Bundle icicle) {
//        setMode(PAUSE);
//
//        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
//        mDirection = icicle.getInt("mDirection");
//        mNextDirection = icicle.getInt("mNextDirection");
//        mMoveDelay = icicle.getLong("mMoveDelay");
//        mScore = icicle.getLong("mScore");
//        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
//    }

    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */
    public void update() {
        if(!(mState == HIDDEN)){
            mRedrawHandler.sleep(mMoveDelay);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        if(mState==HIDDEN)return;
        if(adding) return;
        super.onDraw(canvas);

        //If not 0 we will put some colored background on for 20 'clicks'
        if(touch > 0){
            canvas.drawRect(getCanvasSizedRect(canvas) , getBluePaint());
            touch++;
            if(touch == 20){
                touch = 0;
            }
        }

        updatedDefaultCartAtBottom(defaultCart, canvas);

        synchronized (carts){
            for (Movable m  : carts){
                updateMovable(m, canvas);
            }
        }
        synchronized (offers){
            for (Movable m  : offers){
                updateMovable(m, canvas);
            }
        }
    }

    /**
     * Helper method to see if a cart for a certain user i already displayed
     * @param userId
     * @return
     */
    public boolean isUserDisplayed(int userId) {
        synchronized (carts){
            for(Movable m : carts){
                if(m.getId()==userId)
                    return true;
            }
        }
        return false;
    }

    /**
     * Update default cart. Method that makes it move along bottom of screen
     */
    private void updatedDefaultCartAtBottom(Movable m, Canvas canvas) {
        int w = canvas.getWidth();
        if(m.getX() > w){
            m.setX(m.getBitmap().getWidth() * -1);
        }else{
            m.setX(m.getX() + m.getXDirection());
            m.setY(canvas.getHeight() - m.getBitmap().getHeight());
        }

        //Update direction and draw
        m.updatePosition();
        drawImage(m, canvas);
    }

    /**
     * OLD STD with home animation in top left corner
     * Update the position of a single Movable, that moves as the default cart.
     * For now, some magic numbers here to define the space of the movable.
     */
    private void updatedDefaultCart(Movable m, Canvas canvas) {
        //Here, default cart is given the upper left fifth of the screen
        int w = (int)(canvas.getWidth() * 0.20);
        int h = (int)(canvas.getHeight() * 0.20);
        int dirSpeed = rn.nextInt(4) + 1;
        if(m.getX() + m.getBitmap().getWidth() >= w && m.getXDirection() > 0)
            m.setXDirection(dirSpeed * -1);
        else if(m.getX() <= 0 && m.getXDirection() < 0)
            m.setXDirection(dirSpeed);

        if(m.getY() + m.getBitmap().getHeight() >= h && m.getYDirection() > 0)
            m.setYDirection(dirSpeed * -1);
        else if(m.getY() <= 0 && m.getYDirection() < 0)
            m.setYDirection(dirSpeed);

        //Update direction and draw
        m.updatePosition();
        drawImage(m, canvas);
    }

    /**
     * NEW STD with home cart at bottom of screen
     * Update of a movable's position that will make it bounce off the edges of the screen.
     * @param m
     * @param canvas
     */

    private void updateMovable(Movable m, Canvas canvas){
        //If movable is at an edge we updates its position
        if(m.getX() + m.getBitmap().getWidth() >= canvas.getWidth() || m.getX() <= 0)
            m.setXDirection(m.getXDirection() * -1);
        if(m.getY() + m.getBitmap().getHeight() >= canvas.getHeight() || m.getY() <= 0)
            m.setYDirection(m.getYDirection() * -1);

        //Update direction and draw
        m.updatePosition();
        drawImage(m, canvas);
    }

    private void drawImage(Movable m, Canvas canvas){
        canvas.drawBitmap(m.getBitmap(), m.getX(), m.getY(), paint);
    }

    public void clear() {
        offers = new ArrayList<Movable>();
        carts = new ArrayList<Movable>();
    }

    //****************************************************************
    //Objects used for drawing
    private Paint bluePaint;
    private Paint getBluePaint(){
        if(bluePaint == null){
            bluePaint = new Paint();
            bluePaint.setColor(Color.BLUE);
        }
        return bluePaint;
    }

    private Rect cRect;
    private Rect getCanvasSizedRect(Canvas canvas){
        if(cRect == null){
            cRect = new Rect();
            cRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        return cRect;
    }

    //****************************************************************
    //Getters and setters
    public void setMyInterruptHandler(MyInterruptHandler ih) {
        this.myInterruptHandler = ih;
    }

    public void setState(int state) {
        mState = state;
    }

    public ArrayList<Movable> getOffers() {
        return offers;
    }

    public ArrayList<Movable> getCarts() {
        return carts;
    }
}
