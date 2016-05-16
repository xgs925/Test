package com.photostars.test.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.photostars.test.R;

import java.util.List;

/**
 * Created by Photostsrs on 2016/5/6.
 */
public class AlbumListViewAdapter extends BaseAdapter {
    Context context;
    List<String> paths;

    public AlbumListViewAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=LayoutInflater.from(context).inflate(R.layout.item_album_list,null);
        TextView pathName= (TextView) view.findViewById(R.id.pathName);
        pathName.setText(paths.get(i));
        return view;
    }
}
