package com.photostars.test.utils;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.photostars.test.Constant;
import com.photostars.test.bean.MaterialList;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Photostsrs on 2016/5/19.
 */
public class OnlineMaterialUtil {
    String Tag = "OnlineMaterialUtil";
    private static OnlineMaterialUtil instance;

    private OnlineMaterialUtil() {
    }

    public static OnlineMaterialUtil getInstance() {
        if (instance == null) {
            instance = new OnlineMaterialUtil();
        }
        return instance;
    }


    public void getMaterialList(Callback callback) {
        String url = Constant.MATERIAL_URL;
        FormBody body = new FormBody.Builder()
                .add(Constant.AUTH_KEY, Constant.AUTH_VALUE)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        OkHttpUtil.enqueue(request, callback);
    }

    public void getMaterialNextList(Callback callback,String imageId) {
        String url = Constant.MATERIAL_NEXT_URL;
        FormBody body = new FormBody.Builder()
                .add(Constant.AUTH_KEY, Constant.AUTH_VALUE)
                .add(Constant.PARA_IMAGEID,imageId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpUtil.enqueue(request, callback);
    }
}
