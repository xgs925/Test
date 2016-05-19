package com.photostars.test.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.photostars.test.Constant;
import com.photostars.test.R;
import com.photostars.test.adapter.AlbumGridViewAdapter;
import com.photostars.test.adapter.MaterialGridViewAdapter;
import com.photostars.test.bean.Material;
import com.photostars.test.bean.MaterialList;
import com.photostars.test.utils.ImageLoaderUtil;
import com.photostars.test.utils.OnlineMaterialUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineAlbumFragment extends Fragment {
    String Tag="OnlineAlbumFragment";
    private MaterialList allMaterialList;

    private GridView gridView;
    private MaterialGridViewAdapter gridViewAdapter;
    private boolean loadAvailable=true;

    public OnlineAlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_album, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridViewAdapter=new MaterialGridViewAdapter(getContext(),null,null,null);
        gridView.setAdapter(gridViewAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = allMaterialList.getData().get(i).getFile();
                final String url=allMaterialList.getImageUrl()+name.replace("fs","fo");
                ImageLoader.getInstance().loadImage(url, ImageLoaderUtil.getOptions(), new ImageLoadingListener() {
                    Dialog dialog=new ProgressDialog(getContext());
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        dialog .show();
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        Log.d(Tag,"onLoadingFailed");
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        Log.d(Tag,"onLoadingComplete");
                        dialog.dismiss();
                        Intent data = new Intent();
                        data.putExtra("path", url);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        getActivity().finish();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }
                });
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d(Tag,"onScroll"+i+"|"+i1+"|"+i2);
                if(i+i1>=i2&i2!=0){
                    if(loadAvailable) {
                        loadAvailable=false;
                        Callback callback = new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String status = "";
                                String data = "";
                                String res = response.body().string();
                                JSONTokener jsonParser = new JSONTokener(res);
                                try {
                                    JSONObject json = (JSONObject) jsonParser.nextValue();
                                    status = json.getString("status");
                                    data = json.getString("data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (Constant.STATUS_OK.equals(status)&!data.equals("")) {

                                    Gson gson = new Gson();
                                    final MaterialList materialList = gson.fromJson(res, MaterialList.class);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                                allMaterialList.setShowUrl(materialList.getShowUrl());
                                                allMaterialList.setImageUrl(materialList.getImageUrl());
                                                for (int i = 0; i < materialList.getData().size(); i++) {
                                                    allMaterialList.getData().add(materialList.getData().get(i));
                                                }

                                                update(allMaterialList);
                                                SharedPreferences preferences = getActivity().getSharedPreferences(Constant.SharedPreferencesName, Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("material_list", new Gson().toJson(allMaterialList));
                                                editor.commit();

                                        }
                                    });
                                }
                            }
                        };
                        OnlineMaterialUtil.getInstance().getMaterialNextList(callback, allMaterialList.getData().get(allMaterialList.getData().size() - 1).getPkid());
                    }

                }
            }
        });
        return view;
    }

    public void update(MaterialList materialList){
        this.allMaterialList=materialList;
        gridViewAdapter.setImageUrl(allMaterialList.getImageUrl());
        gridViewAdapter.setShowUrl(allMaterialList.getShowUrl());
        gridViewAdapter.setMaterials(allMaterialList.getData());
        gridViewAdapter.notifyDataSetChanged();
        loadAvailable=true;
    }
}
