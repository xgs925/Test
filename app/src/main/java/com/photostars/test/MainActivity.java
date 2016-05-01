package com.photostars.test;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private RelativeLayout mainView;
    private Button addButton;
    private PopupWindow popupWindow;
    private TextView choosedView;//选中的view
    private TextView touchAddedView;//touch添加的view
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean singleFinger = true;//是否为单指操作
    private float preDegree = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        gestureDetector = new GestureDetector(this, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchView(v, event);
                return true;
            }
        });
        addButton = (Button) findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView addTextView = new TextView(MainActivity.this);
                addTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        touchAddedView = (TextView) v;
                        onTouchView(v, event);
                        return false;
                    }
                });
                addTextView.setText("Test");
                addTextView.setTextSize(30);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                addTextView.setLayoutParams(lp);
                mainView.addView(addTextView);

            }


        });

    }

    private void onTouchView(View v, MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            if (singleFinger)
                gestureDetector.onTouchEvent(event);
        } else {
            if (choosedView != null) {
                singleFinger = false;//一旦多指操作，后续的事件将不交给gestureDetector处理
                scaleGestureDetector.onTouchEvent(event);
                rotateEvent(event);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "ACTION_UP");
            singleFinger = true;
            touchAddedView = null;
            preDegree = 0;
        }
    }

    private void rotateEvent(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        float degree = (float) Math.toDegrees(radians);
        if (preDegree != 0) {
            choosedView.setRotation(choosedView.getRotation() + (degree - preDegree));
        }
        preDegree = degree;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onSingleTapUpAddedView(TextView contentView) {
        choosedView = contentView;
        contentView.setBackgroundDrawable(getDrawable(R.drawable.btn_boader));

        popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View popupView = getLayoutInflater().inflate(R.layout.popup_view, null);
        popupWindow.setContentView(popupView);
        popupWindow.showAsDropDown(contentView);
    }


    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        float originalX;
        float originalY;

        @Override
        public boolean onDown(MotionEvent e) {
            if (choosedView != null) {
                originalX = choosedView.getX();
                originalY = choosedView.getY();
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("gus", "onScroll");
            if (choosedView != null) {
                choosedView.setX(originalX - (e1.getX() - e2.getX()));
                choosedView.setY(originalY - (e1.getY() - e2.getY()));
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (touchAddedView != null) {
                if (touchAddedView != choosedView & choosedView != null) {
                    choosedView.setBackgroundDrawable(null);
                    choosedView = null;
                }
                onSingleTapUpAddedView(touchAddedView);
            } else {
                if (choosedView != null) {
                    choosedView.setBackgroundDrawable(null);
                    choosedView = null;
                }
            }
            return super.onSingleTapUp(e);
        }

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float increment = detector.getCurrentSpan() - detector.getPreviousSpan();
            Log.d(TAG, "increment" + increment);
            choosedView.setTextSize(TypedValue.COMPLEX_UNIT_PX, choosedView.getTextSize() + increment);
            return true;
        }


    }
}
