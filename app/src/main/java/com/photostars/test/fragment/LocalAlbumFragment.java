package com.photostars.test.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.photostars.test.utils.LocalAlbumUtil;
import com.photostars.test.R;
import com.photostars.test.adapter.AlbumGridViewAdapter;

import java.util.List;

public class LocalAlbumFragment extends Fragment {
    List<LocalAlbumUtil.LocalFile> localFiles;
    private AlbumGridViewAdapter gridViewAdapter;
    private GridView gridView;
    CallBack callBack;
    public interface CallBack{void onClickGridView(String path);}


    public LocalAlbumFragment(List<LocalAlbumUtil.LocalFile> localFiles) {
        this.localFiles = localFiles;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBack=(CallBack)getActivity();
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
                String path=localFiles.get(i).getPath();
                callBack.onClickGridView(path);
            }
        });
        return view;
    }

    public void update(List<LocalAlbumUtil.LocalFile> localFiles) {
        this.localFiles = localFiles;
        gridViewAdapter = new AlbumGridViewAdapter(getContext(), localFiles);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();
    }

}
