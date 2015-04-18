package edu.nyu.scps.cursoradapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by danielessien on 4/17/15.
 */
public class OnSwipeTouchListener implements OnTouchListener{
    public final GestureDetector gestureDetector;
    public float x_pos = 0;
    public float y_pos = 0;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onClickLong(){

    }

    public void onClickDouble(){

    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            x_pos = e1.getX();
            y_pos = e1.getY();
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e){
            x_pos = e.getX();
            y_pos = e.getY();
            onClickLong();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e){
            x_pos = e.getX();
            y_pos = e.getY();
            onClickDouble();
            return true;
        }
    }
}
