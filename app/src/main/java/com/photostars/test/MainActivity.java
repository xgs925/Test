package com.photostars.test;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private RelativeLayout mainView;
    private Button addButton;
    private PopupWindow popupWindow;
    private View choosedView;//选中的view
    private List<View> allAddedView;//所有添加的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mainView= (RelativeLayout) findViewById(R.id.main_view);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickMainView();
            }
        });
        addButton= (Button) findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView addTextView=new TextView(MainActivity.this);
                addTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickTextView(view);
                    }
                });
                addTextView.setText("Test");
                addTextView.setTextSize(30);
                RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                addTextView.setLayoutParams(lp);
                mainView.addView(addTextView);

            }


        });

    }

    private void onClickMainView() {
        if(choosedView!=null) {
            choosedView.setBackgroundDrawable(null);
            choosedView=null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onClickTextView(View contentView) {
        choosedView=contentView;
        contentView.setBackgroundDrawable(getDrawable(R.drawable.btn_boader));

        popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View popupView=getLayoutInflater().inflate(R.layout.popup_view,null);
        popupWindow.setContentView(popupView);
        popupWindow.showAsDropDown(contentView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                System.out.print("aa");
                Log.d("gus","ACTION_MOVE");
                break;
        }
        return super.onTouchEvent(event);
    }

    
}
