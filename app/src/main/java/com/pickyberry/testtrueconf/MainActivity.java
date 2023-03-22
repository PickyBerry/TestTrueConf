package com.pickyberry.testtrueconf;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private TextView tv;
    private Handler handler;
    private ObjectAnimator animatorFirst;
    private ObjectAnimator animatorUp;
    private ObjectAnimator animatorDown;
    private AnimatorListenerAdapter adapter_down;
    private AnimatorListenerAdapter adapter_up;
    private boolean animCancelled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set touch listener and animation listeners
        findViewById(R.id.root).setOnTouchListener(handleTouch);
        setupAnimationListeners();


        //Stop animation on click
        tv = findViewById(R.id.tv);
        tv.setOnClickListener(view -> stopAnimation());


        //Hide bottom bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    //Stop any animation or waiting
    private void stopAnimation() {
        animCancelled = true;
        if (handler != null) handler.removeCallbacksAndMessages(null);
        if (animatorFirst != null) animatorFirst.cancel();
        if (animatorUp != null) animatorUp.cancel();
        if (animatorDown != null) animatorDown.cancel();
    }


    //Handle screen touch
    private final View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                stopAnimation();

                //Set text color
                if (getResources().getConfiguration().getLocales().get(0).toString().equals("ru_RU"))
                    tv.setTextColor(Color.rgb(0, 0, 255));
                else tv.setTextColor(Color.rgb(255, 0, 0));
                ////////////////////////////////////////


                //Get the edges of the screen coordinates
                Display mdisp = getWindowManager().getDefaultDisplay();
                Point mdispSize = new Point();
                mdisp.getSize(mdispSize);
                float maxY = mdispSize.y - tv.getHeight() / 2f;
                float minY = -tv.getHeight() / 2f;
                ////////////////////////////////////////


                //Move the textview to the touch point
                float x = event.getX() - tv.getWidth() / 2f;
                float y = event.getY() - tv.getHeight() / 2f;
                tv.setX(x);
                tv.setY(y);
                ////////////////////////////////////////


                //Wait and start animation
                handler = new Handler();
                handler.postDelayed(() -> {

                    //Animator for our first descension
                    animatorFirst = createAnimator(x,y,maxY);
                    animatorFirst.addListener(adapter_down);

                    //Animator for moving up
                    animatorUp = createAnimator(x,maxY,minY);
                    animatorUp.addListener(adapter_up);

                    //Animator for moving down
                    animatorDown = createAnimator(x,minY,maxY);
                    animatorDown.addListener(adapter_down);

                    animCancelled = false;
                    runOnUiThread(() -> animatorFirst.start());
                }, 5000);
            }


            v.performClick();
            return true;
        }

    };


    //Create animator for moving from yBegin to yEnd
    private ObjectAnimator createAnimator(float x, float yBegin, float yEnd){
        ObjectAnimator animator;
        Path path = new Path();
        path.moveTo(x, yBegin);
        path.lineTo(x, yEnd);
        animator = ObjectAnimator.ofFloat(tv, "x", "y", path);
        animator.setDuration(1500);
        return animator;
    }


    //Animation listeners for changing direction
    private void setupAnimationListeners() {

        adapter_down = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animCancelled) animatorUp.start();
            }
        };

        adapter_up = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animCancelled) animatorDown.start();
            }

        };
    }

}