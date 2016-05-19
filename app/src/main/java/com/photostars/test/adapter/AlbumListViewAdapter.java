package com.photostars.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photostars.test.R;
import com.photostars.test.utils.LocalAlbumUtil;

import java.util.List;

/**
 * Created by Photostsrs on 2016/5/6.
 */
public class AlbumListViewAdapter extends BaseAdapter {
    Context context;
    List<String> paths;
    int choosed;
    public AlbumListViewAdapter(Context context, List<String> paths,int choosed) {
        this.context = context;
        this.paths = paths;
        this.choosed=choosed;
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
        TextView num= (TextView) view.findViewById(R.id.num);
        num.setText(LocalAlbumUtil.getInstance().getFolder(paths.get(i)).size()+"");
        if(i==choosed){
            view.setBackgroundColor(Color.parseColor("#313131"));
            View choosed_cur = view.findViewById(R.id.choosed_item);
            choosed_cur.setVisibility(View.VISIBLE);
            pathName.setTextColor(context.getResources().getColor(R.color.white));
            num.setTextColor(context.getResources().getColor(R.color.white));
        }
        return view;
    }
}
