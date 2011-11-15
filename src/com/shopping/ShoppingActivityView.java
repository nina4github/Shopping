package com.shopping;

import android.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.*;
import android.media.JetPlayer;
import android.media.ToneGenerator;
import android.os.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import org.apache.commons.logging.Log;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ahkj
 * Date: 14/11/11
 * Time: 16.32
 * To change this template use File | Settings | File Templates.
 */
public class ShoppingActivityView extends View {

    private static final String TAG = "ShoppingActivityView";
    private int touch;

    PowerManager.WakeLock wl;

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
    private int x, y=0;
    private Paint paint = new Paint();

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    private InterruptHandler interruptHandler;
    private boolean adding;

    public void setInterruptHandler(InterruptHandler ih) {
        this.interruptHandler = ih;
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

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if(!(mState == HIDDEN)){
                ShoppingActivityView.this.update();
                ShoppingActivityView.this.invalidate();
            }
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    //CONSTRUCTORS
    public ShoppingActivityView(Context context) {
        super(context);
        init();
    }

    public ShoppingActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShoppingActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFocusable(true);
        //Add a default cart
        carts = new ArrayList<Movable>();
        carts.add(0, new ShoppingCart(getContext()));

        offers = new ArrayList<Movable>();

        WakeService ws = new WakeService();
        ws.start(this);

        PowerManager pm;
        pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Shopping events");

        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Todo pause animations and save state
                saveState();
                setState(HIDDEN);
                interruptHandler.interrupt();
                return true;
            }
        });
    }

    protected void addShopper(){
        adding = true;
        touch = 1;
        this.carts.add(new ShoppingCart(getContext()));
        adding = false;
    }

    public void removeShopper() {
        adding = true;
        if(carts.size()>1)carts.remove(0);
        adding = false;
    }

    private Notification notification;

    public void addOffer() {
        JetPlayer jetPlayer = JetPlayer.getJetPlayer();
    //      jetPlayer.loadJetFile(getContext().getResources().openRawResourceFd(R.));
        byte segmentId = 0;

// queue segment 5, repeat once, use General MIDI, transpose by -1 octave
        jetPlayer.queueJetSegment(0, -1, 1, -1, 0, segmentId++);

        jetPlayer.play();

        notification = new Notification();
        adding = true;
        offers.add(0, new ShoppingOffer(getContext()));
        adding = false;
    }

    public void removeOffer() {
        adding = true;
        if(offers.size()>0)offers.remove(0);
        adding = false;
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
        Rect r;
        if(touch > 0){
            r = new Rect();
            r.set(0, 0, canvas.getWidth(), canvas.getHeight());
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            canvas.drawRect(r, p);
            touch++;
            if(touch == 20){
                touch = 0;
            }
        }

        for (Movable m  : carts){
            m.updatePosition(canvas.getWidth(), canvas.getHeight());
            drawImage(m, canvas);
        }
        for (Movable m  : offers){
            m.updatePosition(canvas.getWidth(), canvas.getHeight());
            drawImage(m, canvas);
        }
    }

    private void drawImage(Movable m, Canvas canvas){
        canvas.drawBitmap(m.getBitmap(), m.getX(), m.getY(), paint);
    }
}
