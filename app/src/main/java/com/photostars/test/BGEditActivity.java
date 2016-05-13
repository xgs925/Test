package com.photostars.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bm.library.PhotoView;


public class BGEditActivity extends Activity {
    String Tag = "BGEditActivity";
    private ImageView targetImage;
    private ImageView visibleView;//裁剪图片的框
    private int workWidth;
    private int workHeight;
    PhotoView photoView;
    private float visibleWidth;
    private float visibleHeight;
    private float currentDegree = 0;
    private int photoViewWidth;
    private int photoViewHeight;
    private int direction = 0;//view方向（下为正）0:下 1：左 2：上 3：右
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_edit);
        initView();
    }

    @Override
    protected void onResume() {
        RelativeLayout workView = (RelativeLayout) findViewById(R.id.workView);
        workView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        final MyHScrollView hScrollView = (MyHScrollView) findViewById(R.id.hScroll);
        hScrollView.post(new Runnable() {
            @Override
            public void run() {
                hScrollView.scrollTo(Util.dip2px(getBaseContext(), 500) - workWidth / 2, 0);
            }
        });
        final MyVScrollView vScrollView = (MyVScrollView) findViewById(R.id.vScroll);
        vScrollView.post(new Runnable() {
            @Override
            public void run() {
                vScrollView.scrollTo(0, Util.dip2px(getBaseContext(), 500) - workHeight / 2);
            }
        });
        vScrollView.setScrollViewListener(new MyVScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyVScrollView scrollView, int x, int y, int oldx, int oldy) {
                vScrollView.scrollTo(0, Util.dip2px(getBaseContext(), 500) - workHeight / 2);
            }
        });
        super.onResume();
    }

    private void initView() {
        initBtn();
        photoView = (PhotoView) findViewById(R.id.photoview);
        photoView.enable();
//        workView= (ScrollView) findViewById(R.id.workView);
//        targetImage= (ImageView) findViewById(R.id.targetImage);
//        targetImage = (ImageView) findViewById(R.id.targetImage);
        visibleView = (ImageView) findViewById(R.id.visibleView);
        workWidth = getWindowManager().getDefaultDisplay().getWidth();
        workHeight = getWindowManager().getDefaultDisplay().getHeight() - Util.dip2px(this, 223);
        initShelter(getIntent().getIntExtra("width", 0), getIntent().getIntExtra("height", 0));

          photo = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
        photoView.setImageBitmap(photo);
        Log.d(Tag, "photo.getWidth()" + photo.getWidth());
        Log.d(Tag, "photo.getHeight()" + photo.getHeight());
        Matrix matrix = photoView.getImageMatrix();
        float[] values = new float[9];
        matrix.getValues(values);
        Log.d(Tag, "matrix" + values[0]);
        Log.d(Tag, "visibleWidth" + visibleWidth);
        Log.d(Tag, "visibleHeight" + visibleHeight);

        SeekBar rotateBar = (SeekBar) findViewById(R.id.rotateBar);
        rotateBar.setMax(90);
        rotateBar.setProgress(45);
        rotateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentDegree = -i + 45;
                if (direction == 0 | direction == 2) {
                    updateSize(currentDegree);
                } else {
                    updateSize(90 - Math.abs(currentDegree));

                }
                photoView.setRotation(currentDegree + direction * 90);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                photoView.onUp();
            }
        });

        final SeekBar blurBar= (SeekBar) findViewById(R.id.blurBar);
        blurBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Matrix matrix;
            int blur=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blur=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                matrix=new Matrix(photoView.getImageMatrix());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Bitmap blurBitmap=Util.blurBitmap(getBaseContext(),photo,blur);
//                photoView.init();
//                if(blur==0){
//                    photoView.setImageBitmap(photo);
//                }else {
//                    photoView.setImageBitmap(blurBitmap);
//                }
            }
        });
    }


    private void initBtn() {
        Button done= (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo=Util.blurBitmap(getBaseContext(),photo,50);
                photoView.refreshDrawableState();
            }
        });
        ImageView leftRotate = (ImageView) findViewById(R.id.leftRotate);
        ImageView rightRotate = (ImageView) findViewById(R.id.rightRotate);
        ImageView invertH = (ImageView) findViewById(R.id.invertH);
        ImageView invertV = (ImageView) findViewById(R.id.invertV);

        leftRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction = (direction - 1) % 4;
                if (direction < 0) direction += 4;
                if (direction == 0 | direction == 2) {
                    updateSize(Math.abs(currentDegree));
                } else {
                    updateSize(90 - Math.abs(currentDegree));
                }
                photoView.setRotation(currentDegree + direction * 90);
                photoView.onUp();
            }
        });
        rightRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction = (direction + 1) % 4;
                if (direction == 0 | direction == 2) {
                    updateSize(currentDegree);
                } else {
                    updateSize(90 - Math.abs(currentDegree));

                }
                photoView.setRotation(currentDegree + direction * 90);
                photoView.onUp();
            }
        });
        invertH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (direction == 0 | direction == 2) {
                    photoView.changeInverH();
                } else {//view旋转后，视觉上的水平方向相当于view的垂直方向
                    photoView.changeInverV();
                }
                photoView.invalidate();
            }
        });
        invertV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (direction == 0 | direction == 2) {
                    photoView.changeInverV();
                } else {//view旋转后，视觉上的垂直方向相当于view的水平方向
                    photoView.changeInverH();
                }
                photoView.invalidate();
            }
        });

    }

    private void updateSize(float degree) {
        double c = Math.sqrt(visibleWidth * visibleWidth + visibleHeight * visibleHeight);

        double sina_w = visibleWidth / c;
        double sina_h = visibleHeight / c;
        double cRadians_w = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_w);
        double sinc_w = Math.sin(cRadians_w);
        double a_w = sina_w * (c / sinc_w);
        double scale_w = c / a_w;
        photoViewWidth = (int) Math.ceil(visibleWidth * scale_w);

        double cRadians_h = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_h);
        double sinc_h = Math.sin(cRadians_h);
        double a_h = sina_h * (c / sinc_h);
        double scale_h = c / a_h;
        photoViewHeight = (int) Math.ceil(visibleHeight * scale_h);


        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(photoViewWidth, photoViewHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        photoView.setMLayoutParams(lp);
        float currentMinScaleW = photoViewWidth / visibleWidth;
        float currentMinScaleH = photoViewHeight / visibleHeight;
        photoView.setCurrentMinScale(currentMinScaleW < currentMinScaleH ? currentMinScaleW : currentMinScaleH);
    }

    private void initShelter(int width, int height) {

        int margin = Util.dip2px(this, 15);//留15dp
        float scale = 1;


        if (width * 1.0 / height > workWidth * 1.0 / workHeight) {//水平

            scale = (float) ((workWidth - margin * 2.0) / width);
            visibleWidth = workWidth - margin * 2;
            visibleHeight = (float) (visibleWidth * (height * 1.0 / width));
        } else {
            scale = (float) ((workHeight - margin * 2.0) / height);
            visibleHeight = workHeight - margin * 2;
            visibleWidth = (float) (visibleHeight * (width * 1.0 / height));
        }
        photoViewWidth = (int) visibleWidth;
        photoViewHeight = (int) visibleHeight;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) visibleWidth, (int) visibleHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        photoView.setLayoutParams(lp);
        visibleView.setLayoutParams(new RelativeLayout.LayoutParams((int) visibleWidth, (int) visibleHeight));

        int x = (int) ((workWidth - visibleWidth) / 2.0);
        int y = (int) ((workHeight - visibleHeight) / 2.0);
        visibleView.setX(x);
        visibleView.setY(y);
        drawShelter(x, y, visibleWidth, visibleHeight);
    }

    private void drawShelter(int x, int y, float visibleWidth, float visibleHeight) {
        Bitmap shielterBitmap = Bitmap.createBitmap(workWidth, workHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shielterBitmap);
        canvas.drawARGB(100, 0, 0, 0);

        Paint clearPaint = new Paint();
        clearPaint.setAntiAlias(true);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        canvas.drawRect(x, y, visibleWidth + x, visibleHeight + y, clearPaint);

        RelativeLayout shelter = (RelativeLayout) findViewById(R.id.shelter);
        shelter.setBackgroundDrawable(new BitmapDrawable(shielterBitmap));
    }
}
