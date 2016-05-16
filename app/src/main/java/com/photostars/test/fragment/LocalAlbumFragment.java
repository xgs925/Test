package com.photostars.test.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.photostars.test.LocalImageHelper;
import com.photostars.test.R;
import com.photostars.test.activity.BGEditActivity;
import com.photostars.test.adapter.AlbumGridViewAdapter;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class LocalAlbumFragment extends Fragment {
    List<LocalImageHelper.LocalFile> localFiles;
    private AlbumGridViewAdapter gridViewAdapter;
    private GridView gridView;

    public LocalAlbumFragment(List<LocalImageHelper.LocalFile> localFiles) {
        this.localFiles = localFiles;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_album, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridViewAdapter = new AlbumGridViewAdapter(getContext(), localFiles);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uri=localFiles.get(i).getThumbnailUri();
                Bitmap photo= BitmapFactory.decodeFile(uri);
                Intent data = new Intent(getActivity(), BGEditActivity.class);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();
                data.putExtra("bg", bitmapByte);
                startActivity(data);
            }
        });
        return view;
    }

    public void update(List<LocalImageHelper.LocalFile> localFiles) {
        this.localFiles = localFiles;
        gridViewAdapter = new AlbumGridViewAdapter(getContext(), localFiles);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();
    }

}
