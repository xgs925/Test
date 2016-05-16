package com.photostars.test.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.photostars.test.R;

/**
 * Created by Photostsrs on 2016/5/6.
 */
public class FontListViewAdapter extends BaseAdapter {
    Context context;
    Typeface[] fonts;

    public FontListViewAdapter(Context context, Typeface[] fonts) {
        this.context = context;
        this.fonts = fonts;
    }

    @Override
    public int getCount() {
        return fonts.length;
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
        view=LayoutInflater.from(context).inflate(R.layout.font_list_item,null);
        TextView fontTextView= (TextView) view.findViewById(R.id.fontText);
        fontTextView.setTypeface(fonts[i]);
        return view;
    }
}
