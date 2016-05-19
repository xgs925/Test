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
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.photostars.test.bean.Material;
import com.photostars.test.utils.LocalAlbumUtil;
import com.photostars.test.view.SquareLayout;

import java.util.List;

/**
 * Created by Photostsrs on 2016/5/19.
 */
public class MaterialGridViewAdapter extends BaseAdapter {
    private Context context;
    private String imageUrl;
    private String showUrl;
    private List<Material> materials;
    DisplayImageOptions options;


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public MaterialGridViewAdapter(Context context, String imageUrl, String showUrl, List<Material> materials) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.showUrl = showUrl;
        this.materials = materials;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    @Override
    public int getCount() {
        if(materials==null){
            return 0;
        }
        return materials.size();
    }

    @Override
    public Object getItem(int i) {
        return materials.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SquareLayout squareLayout = new SquareLayout(context);
        ImageView imageView = new ImageView(context);
        squareLayout.addView(imageView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String uri = showUrl + materials.get(i).getFile();
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(imageView), options);
        view = squareLayout;
        return view;
    }
}
