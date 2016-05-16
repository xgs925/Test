package com.photostars.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bm.library.PhotoView;
import com.photostars.test.view.MyHScrollView;
import com.photostars.test.view.MyVScrollView;
import com.photostars.test.R;
import com.photostars.test.Util;

import java.io.ByteArrayOutputStream;


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
    private Bitmap orPhoto;
    RelativeLayout workView;
    private boolean invertH = false;
    private boolean invertV = false;
    private int percentage;

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
        workView = (RelativeLayout) findViewById(R.id.workView);
//        photoView = (PhotoView) findViewById(R.id.photoview);

//        workView= (ScrollView) findViewById(R.id.workView);
//        targetImage= (ImageView) findViewById(R.id.targetImage);
//        targetImage = (ImageView) findViewById(R.id.targetImage);
        visibleView = (ImageView) findViewById(R.id.visibleView);
        workWidth = getWindowManager().getDefaultDisplay().getWidth();
        workHeight = getWindowManager().getDefaultDisplay().getHeight() - Util.dip2px(this, 223);
        initShelter(getIntent().getIntExtra("width", 0), getIntent().getIntExtra("height", 0));

        orPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
        Bitmap blurBitmap = Util.blurBitmap(getBaseContext(), orPhoto, 0);
        initPhotoView(blurBitmap);

        Matrix matrix = photoView.getImageMatrix();
        float[] values = new float[9];
        matrix.getValues(values);
        Log.d(Tag, "matrix" + values[0]);
        Log.d(Tag, "visibleWidth" + visibleWidth);
        Log.d(Tag, "visibleHeight" + visibleHeight);

        final SeekBar rotateBar = (SeekBar) findViewById(R.id.rotateBar);
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

        final SeekBar blurBar = (SeekBar) findViewById(R.id.blurBar);
        blurBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Matrix matrix;
            float[] values = new float[9];

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                percentage = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                matrix = new Matrix(photoView.getImageMatrix());
                matrix.getValues(values);
                Log.d(Tag, values[2] + "|" + values[5]);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)Math.ceil(visibleWidth), (int)Math.ceil(visibleHeight));
//                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//                photoView.setMLayoutParams(lp);
//               int currentDegree1=0;
//                if (direction == 0 | direction == 2) {
//                    updateSize(currentDegree1);
//                } else {
//                    updateSize(90 - Math.abs(currentDegree1));
//
//                }
//                photoView.setRotation(currentDegree1 + direction * 90);
                Bitmap blurBitmap = Util.blurBitmap(getBaseContext(), orPhoto, percentage);
                photoView.setImageBitmap(blurBitmap);
//                initPhotoView(blurBitmap);
//                currentDegree=0;
//                if (direction == 0 | direction == 2) {
//                    updateSize(currentDegree);
//                } else {
//                    updateSize(90 - Math.abs(currentDegree));
//
//                }
//                photoView.setRotation(currentDegree + direction * 90);


//                photoView.setVisibility(View.INVISIBLE);
//                Handler handler = new Handler();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (direction == 0 | direction == 2) {
//                            updateSize(currentDegree);
//                        } else {
//                            updateSize(90 - Math.abs(currentDegree));
//
//                        }
//                        photoView.setRotation(currentDegree + direction * 90);
//                        photoView.setImageMatrix(matrix);
//                        photoView.setVisibility(View.VISIBLE);
//                    }
//                });


//                photoView.setImageBitmap(blurBitmap);
//                photoView.setImageMatrix(matrix);
//                Log.d(Tag,blurBitmap.getWidth()+"blurBitmap.getWidth()");
            }
        });
    }

    private void initPhotoView(Bitmap bm) {
        photoView = new PhotoView(this);
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photoView.enable();
        workView.removeAllViews();
        workView.addView(photoView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) visibleWidth, (int) visibleHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        photoView.setLayoutParams(lp);
        photoView.setImageBitmap(bm);
    }


    private void initBtn() {
        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int photoWidth = orPhoto.getWidth();
//                int photoHeight = orPhoto.getHeight();

//                Matrix m = new Matrix();
//                m.postRotate(currentDegree, photoWidth / 2, photoHeight / 2);
//                Bitmap rotatePhoto = Bitmap.createBitmap(orPhoto, 0, 0, photoWidth, photoHeight, m, true);

                float[] values = new float[9];
                photoView.getImageMatrix().getValues(values);
                int x = (int) Math.floor(Math.abs(values[2] / values[0]));
                int y=(int) Math.floor(Math.abs(values[5] / values[0]));
                int width=(int) Math.floor(photoViewWidth / values[0]);
                int height= (int) Math.floor(photoViewHeight / values[0]);
                if(x+width>orPhoto.getWidth()) width=orPhoto.getWidth()-x;
                if(y+height>orPhoto.getHeight()) height=orPhoto.getHeight()-y;
                Bitmap cropBtimap = Bitmap.createBitmap(orPhoto, x ,y ,width ,height);
                Matrix m = new Matrix();
                if (invertH) m.postScale(-1, 1);
                if (invertV) m.postScale(1, -1);
                m.postRotate(direction * 90 + currentDegree, cropBtimap.getWidth() / 2, cropBtimap.getHeight() / 2);
                Bitmap rotatePhoto = Bitmap.createBitmap(cropBtimap, 0, 0, cropBtimap.getWidth(), cropBtimap.getHeight(), m, true);

                float visibleWidthOnOr = visibleWidth / values[0];//裁剪区在原图中的宽度
                float visibleHeightOnOr = visibleHeight / values[0];

                float l = (rotatePhoto.getWidth() - visibleWidthOnOr) / 2;
                float t = (rotatePhoto.getHeight() - visibleHeightOnOr) / 2;
                RectF rectF = new RectF(l < 0 ? 0 : l, t < 0 ? 0 : t, (rotatePhoto.getWidth() + visibleWidthOnOr) / 2, (rotatePhoto.getHeight() + visibleHeightOnOr) / 2);
                Bitmap newPhoto = Bitmap.createBitmap(rotatePhoto, (int) rectF.left, (int) rectF.top, (int) Math.floor(rectF.width()), (int) Math.floor(rectF.height()));
                final Bitmap newBlurPhoto = Util.blurBitmap(BGEditActivity.this, newPhoto, percentage);

//                Util.saveMyBitmap(BGEditActivity.this, newBlurPhoto, "test");
//                Handler handler=new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent data=new Intent();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        newBlurPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                        byte [] bitmapByte =baos.toByteArray();
//                        data.putExtra("bg", bitmapByte);
//                        setResult(RESULT_OK,data);
//                        finish();
//                    }
//                },1000);
                Intent data = new Intent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newBlurPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();
                data.putExtra("bg", bitmapByte);
                setResult(RESULT_OK, data);
                finish();
//                float photoViewCenX = Math.abs(values[2] / values[0]) + (photoViewWidth / values[0] / 2);//photoView中点在原图中的坐标
//                float photoViewCenY = Math.abs(values[5] / values[0]) + (photoViewHeight / values[0] / 2);
//
//                double sin = Math.sin(-currentDegree);
//                double cos = Math.cos(-currentDegree);
//
//                double orVisiblePoint_lt_x=Math.abs(values[2] / values[0])-orPhoto.getWidth()/2.0;//orPhoto以中心为原点裁剪区左上角坐标
//                double orVisiblePoint_lt_y=Math.abs(values[5] / values[0])-orPhoto.getHeight()/2.0;
//
//                double orVisiblePoint_lt_rotate_x=orVisiblePoint_lt_x*cos+orVisiblePoint_lt_y*sin;//orPhoto以中心为原点旋转后裁剪区左上角坐标
//                double orVisiblePoint_lt_rotate_y=-orVisiblePoint_lt_x*sin+orVisiblePoint_lt_y*cos;
//
//                double orVisiblePoint_lt_rotate_trans_x=orVisiblePoint_lt_rotate_x+rotatePhoto.getWidth()/2.0;//orPhoto以中心为原点旋转+平移后裁剪区左上角坐标
//                double orVisiblePoint_lt_rotate_trans_y=-orVisiblePoint_lt_rotate_y+rotatePhoto.getHeight()/2.0;
//
//
//                RectF rectF = new RectF((float) orVisiblePoint_lt_rotate_trans_x, (float) orVisiblePoint_lt_rotate_trans_y, (float) (orVisiblePoint_lt_rotate_trans_x + visibleWidthOnOr / 2), (float) (orVisiblePoint_lt_rotate_trans_y + visibleHeightOnOr / 2));
//
//                Canvas canvas = new Canvas(rotatePhoto);
//                Paint paint = new TextPaint();
//                paint.setColor(Color.WHITE);
//                canvas.drawRect(rectF, paint);

//                Bitmap cropBtimap = Bitmap.createBitmap(rotatePhoto, (int) (photoViewCenX + (rotatePhoto.getWidth()-photoWidth)/2-visibleWidthOnOr / 2), (int) (photoViewCenY + (rotatePhoto.getHeight()-photoHeight)/2-visibleHeightOnOr / 2), (int) visibleWidthOnOr, (int) visibleHeightOnOr);

//                Bitmap cropBtimap = Bitmap.createBitmap(orPhoto, (int) Math.abs(values[2] / values[0]), (int) Math.abs(values[5] / values[0]), (int) (photoViewWidth / values[0]), (int) (photoViewHeight / values[0]));

//                float cx = cropBtimap.getWidth() / 2;
//                float cy = cropBtimap.getHeight() / 2;
//                RectF rectF = new RectF(cx - visibleWidth / values[0] / 2, cy - visibleHeight / values[0] / 2, cx + visibleWidth / values[0] / 2, cy + visibleHeight / values[0] / 2);

//                Matrix m=new Matrix();
//                m.postRotate(currentDegree,cx,cy);
//                Bitmap b = Bitmap.createBitmap(cropBtimap, 0, 0, (int) rectF.width(), (int) rectF.height(), m, true);

//                Bitmap b=Bitmap.createBitmap(cropBtimap.getWidth(),cropBtimap.getHeight(),Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(b);
//                canvas.rotate(direction * 90 + currentDegree, cx, cy);
//                Paint paint = new Paint();
//                paint.setColor(Color.BLACK);
//                canvas.drawRect(rectF,paint);
//                canvas.clipRect(rectF);
//                canvas.drawBitmap(cropBtimap,new Matrix(),paint);

            }
        });
        ImageView leftRotate = (ImageView) findViewById(R.id.leftRotate);
        ImageView rightRotate = (ImageView) findViewById(R.id.rightRotate);
        final ImageView invertH = (ImageView) findViewById(R.id.invertH);
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
                changeInvertH();
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
                changeInvertV();
                photoView.invalidate();
            }
        });

    }

    public void changeInvertH() {
        if (invertH) {
            invertH = false;
        } else {
            invertH = true;
        }
    }

    public void changeInvertV() {
        if (invertV) {
            invertV = false;
        } else {
            invertV = true;
        }
    }

    private void updateSize(float degree) {
//        double c = Math.sqrt(visibleWidth * visibleWidth + visibleHeight * visibleHeight);
//
//        double sina_w = visibleWidth / c;
//        double sina_h = visibleHeight / c;
//        double cRadians_w = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_w);
//        double sinc_w = Math.sin(cRadians_w);
//        double a_w = sina_w * (c / sinc_w);
//        double scale_w = c / a_w;
//        photoViewWidth = (int) Math.ceil(visibleWidth * scale_w);
//
//        double cRadians_h = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_h);
//        double sinc_h = Math.sin(cRadians_h);
//        double a_h = sina_h * (c / sinc_h);
//        double scale_h = c / a_h;
//        photoViewHeight = (int) Math.ceil(visibleHeight * scale_h);
        Point point = getBoundRect(visibleWidth, visibleHeight, degree);
        photoViewWidth = point.x;
        photoViewHeight = point.y;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(photoViewWidth, photoViewHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        photoView.setMLayoutParams(lp);
        float currentMinScaleW = photoViewWidth / visibleWidth;
        float currentMinScaleH = photoViewHeight / visibleHeight;
        photoView.setCurrentMinScale(currentMinScaleW < currentMinScaleH ? currentMinScaleW : currentMinScaleH);
    }

    /**
     * 获取外接矩形长宽
     *
     * @param width
     * @param height
     * @param degree
     * @return
     */
    public Point getBoundRect(float width, float height, float degree) {
        double c = Math.sqrt(width * width + height * height);

        double sina_w = width / c;
        double sina_h = height / c;
        double cRadians_w = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_w);
        double sinc_w = Math.sin(cRadians_w);
        double a_w = sina_w * (c / sinc_w);
        double scale_w = c / a_w;
        int boundRectWidth = (int) Math.ceil(width * scale_w);

        double cRadians_h = Math.PI - Math.toRadians(Math.abs(degree)) - Math.asin(sina_h);
        double sinc_h = Math.sin(cRadians_h);
        double a_h = sina_h * (c / sinc_h);
        double scale_h = c / a_h;
        int boundRectHeight = (int) Math.ceil(height * scale_h);
        return new Point(boundRectWidth, boundRectHeight);
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
