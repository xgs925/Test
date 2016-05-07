package com.photostars.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private String TAG = "MainActivity";

    private float workspaceCenterX;//工作区终点x坐标
    private float workspaceCenterY;//工作区终点y坐标
    private RelativeLayout mainView;
    private RelativeLayout contentView;
    private Button addButton;
    //    private PopupWindow popupWindow;
    private TextView choosedView;//选中的view
    private TextView touchAddedView;//touch添加的view
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean singleFinger = true;//是否为单指操作
    private float preDegree = 0;
    private PopupWindow currentPopupWindow;
    //    private PopupWindow currentBottomPopupWindow;
    private PopupWindow editPopupWindow;
    private PopupWindow stylePopupWindow;
    //    private View currentButtonView;
    private float mainViewOffset;
    String[] colors = {"#000000", "#626262", "#a0a0a0", "#d2d2d2", "#ffffff", "#7d0100", "#e60111", "#ea6200", "#f39900", "#fff200",
            "#fff899", "#b4d565", "#8ec51f", "#21ae37", "#009a43", "#009f96", "#7dcef4", "#0069b7", "#01489d", "#1c2188",
            "#440163", "#601a87", "#8858a1", "#a98bbc", "#c391bf", "#f19fc2", "#ea6aa2", "#e4017f", "#a4005a", "#7d0121",
            "#e5014f", "#ea6a76"};
    private ScrollView vScroll;
    private int choosedTextalpha;//当前文字透明度（0-255）
    private ImageView mImageView;//操作图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(contentView);
        mImageView= (ImageView) findViewById(R.id.mImage);
        initView();
    }

    private void initView() {
        initShelter();
        initPopupWindow();
        vScroll = (ScrollView) findViewById(R.id.vScroll);
        vScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchView(v, event);
                return true;
            }
        });
        HorizontalScrollView hScroll = (HorizontalScrollView) findViewById(R.id.hScroll);
        hScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchView(v, event);
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int workHeight = (int) (screenHeight - getResources().getDimension(R.dimen.text_main_title_height) - getResources().getDimension(R.dimen.text_main_bottom_height));
        workspaceCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
        workspaceCenterY = workHeight / 2;


        mainView.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());
        mainView.setMinimumHeight(workHeight);
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
                addTextView.setGravity(Gravity.CENTER);
                addTextView.setTextColor(Color.parseColor("#ffffff"));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins((int) workspaceCenterX, (int) workspaceCenterY, 0, 0);
                addTextView.setLayoutParams(lp);

                mainView.addView(addTextView);
                if (choosedView != null) {
                    choosedView.setBackgroundDrawable(null);
                }
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
                addTextView.setBackgroundResource(R.drawable.btn_boader);
                choosedView = addTextView;
                currentPopupWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, (int) workspaceCenterX, (int) workspaceCenterY - 50);

            }


        });

    }

    private void initShelter() {
        ImageView l= (ImageView) findViewById(R.id.shelterLeft);
        ImageView r= (ImageView) findViewById(R.id.shelterRight);
        ImageView t= (ImageView) findViewById(R.id.shelterTop);
        ImageView b= (ImageView) findViewById(R.id.shelterBottom);
        Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.test1);
        int width = photo.getWidth(), hight = photo.getHeight();

    }

    private void initPopupWindow() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_view, null);
        currentPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, false);
        Button editBtn = (Button) popupView.findViewById(R.id.pop_btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
                showEditView();
            }
        });
        Button fontBtn = (Button) popupView.findViewById(R.id.pop_btn_font);
        fontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
                showStyleView(false);
            }
        });

        Button styleBtn = (Button) popupView.findViewById(R.id.pop_btn_style);
        styleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
                showStyleView(true);
            }
        });

        Button delBtn = (Button) popupView.findViewById(R.id.pop_btn_del);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPopupWindow.dismiss();
                mainView.removeView(choosedView);
                choosedView = null;
            }
        });
    }

    private void showEditView() {
        final View popupView = getLayoutInflater().inflate(R.layout.popup_edit, null);
        final EditText editText = (EditText) popupView.findViewById(R.id.editText);
        editText.setText(choosedView.getText());
        editText.requestFocus();
        View confirmBtn =  popupView.findViewById(R.id.confirmBtn);
        View toStyleBtn = popupView.findViewById(R.id.toStyleBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                final float x = choosedView.getX();
                final float y = choosedView.getY();
                choosedView.setText(text);

                editPopupWindow.dismiss();
                editPopupWindow = null;
//                exitEditView();
            }
        });
        toStyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                choosedView.setText(text);
//                exitEditView();
                editPopupWindow.dismiss();
                editPopupWindow = null;
                showStyleView(false);

            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        popupView.setLayoutParams(lp);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        contentView.addView(popupView);
//        currentButtonView = popupView;


        PopupWindow buttomWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        buttomWindow.setOutsideTouchable(true);
        buttomWindow.setBackgroundDrawable(new BitmapDrawable());
        buttomWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止挡住系统键
        buttomWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        buttomWindow.setAnimationStyle(R.style.pop_button_anim);
        buttomWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);

        editPopupWindow = buttomWindow;

        float edit_pop_offset = getResources().getDimension(R.dimen.edit_pop_offset);
        offsetMainView(buttomWindow, edit_pop_offset);

    }

    private void offsetMainView(PopupWindow buttomWindow, float popHeight) {
        int[] choosedLocation = new int[2];
        choosedView.getLocationOnScreen(choosedLocation);
        int bottomY = choosedLocation[1] + choosedView.getHeight();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();


        float offset = bottomY - (screenHeight - popHeight);
        Log.d(TAG, offset + "offset");
        if (offset > 0) {
            mainView.setY(-offset);
            mainViewOffset = offset;
        }
        buttomWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mainViewOffset != 0) {
                    mainView.setY(0);
                    mainViewOffset = 0;
                }
            }
        });
    }

    private void showStyleView(boolean style) {
        RelativeLayout styleView = (RelativeLayout) getLayoutInflater().inflate(R.layout.popup_style, null);

        RadioGroup radioGroup = (RadioGroup) styleView.findViewById(R.id.styleBtnGroup);
        final LinearLayout btn2View = (LinearLayout) styleView.findViewById(R.id.btn2View);
        final RelativeLayout btn3View = (RelativeLayout) styleView.findViewById(R.id.btn3View);
        final Button btn1 = (Button) styleView.findViewById(R.id.btn1);
        final Button btn2 = (Button) styleView.findViewById(R.id.btn2);
        final Button btn3 = (Button) styleView.findViewById(R.id.btn3);
        if (style) {
            btn2View.setVisibility(View.INVISIBLE);
            radioGroup.check(btn3.getId());
        } else {
            btn3View.setVisibility(View.INVISIBLE);
            radioGroup.check(btn2.getId());
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == btn1.getId()) {
//                    currentBottomPopupWindow.dismiss();
                    exitStyleView();
                    showEditView();
                } else if (i == btn2.getId()) {
                    btn2View.setVisibility(View.VISIBLE);
                    btn3View.setVisibility(View.INVISIBLE);
                } else if (i == btn3.getId()) {
                    btn2View.setVisibility(View.INVISIBLE);
                    btn3View.setVisibility(View.VISIBLE);
                }
            }
        });
//字号
        TextView textSizeView = (TextView) styleView.findViewById(R.id.textSizeView);
        Log.d(TAG,"textSize"+choosedView.getTextSize());


//字体
        final ListView fontListView = (ListView) styleView.findViewById(R.id.fontList);
        final Typeface[] fonts = {Typeface.DEFAULT, Typeface.DEFAULT_BOLD, Typeface.MONOSPACE, Typeface.SANS_SERIF, Typeface.SERIF};

        fontListView.setAdapter(new FontListViewAdapter(this, fonts));
        fontListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choosedView.setTypeface(fonts[i]);
            }
        });
//颜色
        final LinearLayout colorLayout = (LinearLayout) styleView.findViewById(R.id.colorLayout);

        for (int i = 0; i < colors.length; i++) {
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colors[i])));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            imageView.setLayoutParams(lp);
            colorLayout.addView(imageView);
        }


        colorLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                     choosedTextalpha = Color.alpha(choosedView.getCurrentTextColor());
                    Log.d(TAG,"alpha"+choosedTextalpha);
                }
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                RectF rect = new RectF(location[0], location[1], location[0] + view.getWidth(),
                        location[1] + view.getHeight());
                float x = motionEvent.getRawX(); // 获取相对于屏幕左上角的 x 坐标值
                float y = motionEvent.getRawY(); // 获取相对于屏幕左上角的 y 坐标值
                boolean isInViewRect = rect.contains(x, y);

                if (isInViewRect) {
                    float w = view.getWidth() / colors.length;
                    int i = (int) Math.ceil((x - location[0]) / w);
                    if (i <= colors.length) {
                        int color = Color.parseColor(colors[i - 1]);
                        ColorStateList colorStateList=ColorStateList.valueOf(color);
                        choosedView.setTextColor(colorStateList.withAlpha(choosedTextalpha));
                    }
                } else {

                }
                return true;
            }
        });
//对齐方式
        RadioGroup alignGroup = (RadioGroup) styleView.findViewById(R.id.alignGroup);
        final Button alignLeft = (Button) styleView.findViewById(R.id.alignLeft);
        final Button alignCenter = (Button) styleView.findViewById(R.id.alignCenter);
        final Button alignRight = (Button) styleView.findViewById(R.id.alignRight);
        Log.d(TAG, "getGravity" + choosedView.getGravity());
        switch (choosedView.getGravity()) {
            case Gravity.LEFT | 51:
                alignGroup.check(alignLeft.getId());
                break;
            case Gravity.CENTER:
                alignGroup.check(alignCenter.getId());
                break;
            case Gravity.RIGHT | 53:
                alignGroup.check(alignRight.getId());
                break;
        }
        alignGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == alignLeft.getId()) {
                    choosedView.setGravity(Gravity.LEFT);
                } else if (i == alignCenter.getId()) {
                    choosedView.setGravity(Gravity.CENTER);
                } else if (i == alignRight.getId()) {
                    choosedView.setGravity(Gravity.RIGHT);
                }
            }
        });

        //透明度
        final TextView alphaTextView = (TextView) styleView.findViewById(R.id.alphaText);
        SeekBar alphaBar = (SeekBar) styleView.findViewById(R.id.alphaBar);
        int color = choosedView.getCurrentTextColor();
        int alpha = Color.alpha(color);
        alphaBar.setMax(255);
        alphaBar.setProgress(alpha);
        alphaTextView.setText(alpha * 100 / 255 + "");
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d(TAG, "alphaBar" + i);
                ColorStateList color = choosedView.getTextColors();
                choosedView.setTextColor(color.withAlpha(i));
                alphaTextView.setText(i * 100 / 255 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        int height = (int) getResources().getDimension(R.dimen.style_pop_height);
        PopupWindow bottomWindow = new PopupWindow(styleView, RelativeLayout.LayoutParams.MATCH_PARENT, height, false);//若为true将无法自动弹起键盘
        bottomWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止挡住系统键
        bottomWindow.setAnimationStyle(R.style.pop_button_anim);
        bottomWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);
        stylePopupWindow = bottomWindow;
        offsetMainView(bottomWindow, getResources().getDimension(R.dimen.style_pop_height));
    }

    private void exitStyleView() {
        if (stylePopupWindow != null) {
            stylePopupWindow.dismiss();
            stylePopupWindow = null;
            singleFinger = false;
        }
    }

    private void onKeyboardShow() {
        final RelativeLayout myLayout = (RelativeLayout) ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        myLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                myLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = myLayout.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                Log.d(TAG, "heightDifference" + heightDifference);
//                        buttonWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, heightDifference);

                //boolean visible = heightDiff > screenHeight / 3;

            }
        });
    }


//    private void exitEditView() {
//        if (currentButtonView != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            currentButtonView.setVisibility(View.GONE);
//            currentButtonView = null;
//        }
//    }

    private void onTouchView(View v, MotionEvent event) {
        Log.d(TAG, "onTouchView");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

//            exitEditView();
            exitStyleView();
//            if (currentPopupWindow.isShowing()) {
//                currentPopupWindow.dismiss();
//            }
//            if (choosedView != null) {
//                choosedView.setBackgroundDrawable(null);
//            }
        }
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            if (singleFinger)
                gestureDetector.onTouchEvent(event);
        } else {
            if (currentPopupWindow.isShowing()) currentPopupWindow.dismiss();
            if (choosedView != null) {
                singleFinger = false;//一旦多指操作，后续的事件将不交给gestureDetector处理
                scaleGestureDetector.onTouchEvent(event);
                rotateEvent(event);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "ACTION_UP");
//            if (touchAddedView != null) {
//                if (touchAddedView != choosedView) {
//                    touchAddedView.setBackgroundDrawable(null);
//                }
//            }
            if (touchAddedView != null & choosedView != touchAddedView) {
                choosedView = touchAddedView;
            }
            if(choosedView!=null) choosedView.setBackgroundResource(R.drawable.btn_boader);
            singleFinger = true;
            touchAddedView = null;
            preDegree = 0;
//            if (choosedView != null) {
//                chooseAddedView(choosedView);
//            }
        }
    }


    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        float originalX;
        float originalY;

        @Override
        public boolean onDown(MotionEvent e) {
            if (touchAddedView != null & choosedView != touchAddedView) {
                if (choosedView != null) choosedView.setBackgroundDrawable(null);
                originalX = touchAddedView.getX();
                originalY = touchAddedView.getY();
            } else {
                if (choosedView != null) {
                    originalX = choosedView.getX();
                    originalY = choosedView.getY();
                }
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("gus", "onScroll");
            if (currentPopupWindow.isShowing()) currentPopupWindow.dismiss();

            if (touchAddedView != null) {
                if (touchAddedView.getBackground() == null) {
                    touchAddedView.setBackgroundResource(R.drawable.btn_boader);
                }
                touchAddedView.setX(originalX - (e1.getX() - e2.getX()));
                touchAddedView.setY(originalY - (e1.getY() - e2.getY()));
            } else {
                if (choosedView != null) {
                    if (choosedView.getBackground() == null) {
                        choosedView.setBackgroundResource(R.drawable.btn_boader);
                    }
                    choosedView.setX(originalX - (e1.getX() - e2.getX()));
                    choosedView.setY(originalY - (e1.getY() - e2.getY()));
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTap");
            if (touchAddedView != null & choosedView != touchAddedView) {//touch未选中的子view
                Log.d(TAG, "onSingleTap1");
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
                if (choosedView != null) choosedView.setBackgroundDrawable(null);
                choosedView = touchAddedView;
                choosedView.setBackgroundResource(R.drawable.btn_boader);
                currentPopupWindow.showAtLocation(vScroll, Gravity.NO_GRAVITY, (int) e.getX(), (int) e.getY() - 50);
//                currentPopupWindow.showAsDropDown(choosedView);
            } else if (touchAddedView != null & choosedView == touchAddedView) {//touch选中的子view
                Log.d(TAG, "onSingleTap2");
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                } else {
                    currentPopupWindow.showAtLocation(vScroll, Gravity.NO_GRAVITY, (int) e.getX(), (int) e.getY() - 50);
//                    currentPopupWindow.showAsDropDown(touchAddedView);
                }
            } else if (touchAddedView == null) {//touch mainView
                Log.d(TAG, "onSingleTap3");
                if (currentPopupWindow.isShowing()) {
                    currentPopupWindow.dismiss();
                }
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
            float size = (choosedView.getTextSize() + increment);
            choosedView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size < 20 ? 20 : size);
            return true;
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


}
