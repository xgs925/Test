package com.photostars.test.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.photostars.test.AppContext;
import com.photostars.test.LocalImageHelper;
import com.photostars.test.view.SquareLayout;

import java.util.List;

/**
 * Created by Photostsrs on 2016/5/16.
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    Context context;
    List<LocalImageHelper.LocalFile> paths;
    DisplayImageOptions options;

    public AlbumGridViewAdapter(Context context, List<LocalImageHelper.LocalFile> paths) {
        this.context = context;
        this.paths = paths;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)

                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public LocalImageHelper.LocalFile getItem(int i) {
        return paths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SquareLayout squareLayout=new SquareLayout(context);
        ImageView imageView = new ImageView(context);
        squareLayout.addView(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LocalImageHelper.LocalFile localFile = paths.get(i);
        ImageLoader.getInstance().displayImage(localFile.getThumbnailUri(), new ImageViewAware(imageView), options);
        view = squareLayout;
        return view;
    }
}
