package com.photostars.test;

import android.graphics.Point;

/**
 * Created by Photostsrs on 2016/5/11.
 */
public class Util {
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
