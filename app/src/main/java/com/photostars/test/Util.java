package com.photostars.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Created by Photostsrs on 2016/5/11.
 */
public class Util {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Context context,Bitmap bitmap,int percentage){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        if(percentage>0&percentage<=100)
        blurScript.setRadius((float) (25.f*(percentage*1.0/100)));

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
//        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;


    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Point measure(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        return new Point(width, height);
    }

    //sampQ:
// 1: low;  2: standard;  3: high;  4:super;
//staFun:
// 1: crop;  2: cut;  3: combine
    public static Point ImageSampleFun(int orW, int orH, int sampQ, int staFun) {
        int cutStar = 360;
        if (sampQ == 2) cutStar = 540;
        else if (sampQ == 3) cutStar = 720;
        else if (sampQ == 4) cutStar = 1080;

        int paraBig = 0;//width > height
        int maxLF = orW;
        int minLF = orH;
        if (orH > orW) {
            paraBig = 1;
            maxLF = orH;
            minLF = orW;
        }

        int minLS = minLF;
        int maxLS = maxLF;
        if (minLF > cutStar) {
            minLS = cutStar;
            maxLS = cutStar * maxLF / minLF;
        }


        int minLT = minLS;
        int maxLT = maxLS;

        if (maxLS < 4 * cutStar / 3) {
            maxLT = 4 * cutStar / 3;
            minLT = 4 * cutStar * minLF / (3 * maxLF);
        } else if (maxLS > 16 * cutStar / 9) {
            maxLT = 16 * cutStar / 9;
            minLT = 16 * cutStar * minLF / (9 * maxLF);
        }

        int minLL = minLT;
        int maxLL = maxLT;

        if (staFun == 1) {//crop
            if (maxLF > 1.618 * maxLT) {
                maxLL = (int) 1.618 * maxLT;
                minLL = (int) 1.618 * minLT;
            } else {
                maxLL = maxLF;
                minLL = minLF;
            }
        } else if (staFun == 2) {//cut
            if (maxLF > cutStar) {
                maxLL = cutStar;
                minLL = cutStar * minLF / maxLF;
            } else {
                maxLL = maxLF;
                minLL = minLF;
            }
        } else if (staFun == 3) {//combine
            maxLL = maxLT;
            minLL = minLT;
            //
            if (maxLL < 4 * minLL / 3) {
                maxLL = cutStar * 4 / 3;
                minLL = maxLL * minLF / maxLF;
            } else if (maxLL < 16 * minLL / 9) {
                minLL = cutStar;
                maxLL = minLL * maxLF / minLF;
            } else {
                maxLL = 16 * cutStar / 9;
                minLL = maxLL * minLF / maxLF;
            }
        }

        //
        int sampW = 0;
        int sampH = 0;
        if (paraBig == 0) {
            sampW = maxLL;
            sampH = minLL;
        } else {
            sampW = minLL;
            sampH = maxLL;
        }


        Point sizeSample = new Point(sampW, sampH);
        return sizeSample;
    }

}
