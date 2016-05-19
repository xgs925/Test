package com.photostars.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.photostars.test.Constant;
import com.photostars.test.bean.MaterialList;
import com.photostars.test.utils.LocalAlbumUtil;
import com.photostars.test.R;
import com.photostars.test.adapter.AlbumListViewAdapter;
import com.photostars.test.adapter.AlbumViewPagerAdapter;
import com.photostars.test.fragment.LocalAlbumFragment;
import com.photostars.test.fragment.OnlineAlbumFragment;
import com.photostars.test.utils.OnlineMaterialUtil;
import com.photostars.test.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlbumActivity extends FragmentActivity implements LocalAlbumFragment.CallBack {
    String TAG = "AlbumActivity";
    private LocalAlbumUtil helper;
    private ViewPager viewPager;
    private View preView = null;
    private List<String> folderNames = new ArrayList<>();
    List<LocalAlbumUtil.LocalFile> localFiles;
    private PopupWindow buttomWindow;
    private LocalAlbumFragment localAlbumFragment;
    private OnlineAlbumFragment onlineAlbumFragment;
    private AlbumViewPagerAdapter pagerAdapter;
    private int choosed = 0;
    private ListView pathList;
    private View alertBar;
    private TextView material;
    private TextView pathName;
    float alertBarWidth;
    private ImageView shelter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initImage();
    }

    private void initView() {

        helper = LocalAlbumUtil.getInstance();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "positionOffset" + positionOffset);
                if (positionOffset != 0) {
                    alertBar.setX(alertBarWidth * positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                alertBar.setX(alertBarWidth * position);
                if (position == 1) {
                    loadMaterial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        Map<String, List<LocalAlbumUtil.LocalFile>> folders = helper.getFolderMap();
        Iterator iter = folders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.equals("所有图片")) {
                folderNames.add(0, key);
            } else {
                folderNames.add(key);
            }
        }
        localFiles = helper.getFolder(folderNames.get(0));
        pathName = (TextView) findViewById(R.id.pathName);
        pathName.setText(folderNames.get(0));

        View popupView = getLayoutInflater().inflate(R.layout.popup_album_choose, null);
        buttomWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, Util.dip2px(this, 320), true);
        buttomWindow.setOutsideTouchable(true);
        buttomWindow.setBackgroundDrawable(new BitmapDrawable());
        buttomWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止挡住系统键
        buttomWindow.setAnimationStyle(R.style.pop_button_anim);
        shelter = (ImageView) findViewById(R.id.shelter);
        buttomWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                shelter.setVisibility(View.INVISIBLE);
            }
        });

        FragmentManager fm = getSupportFragmentManager();

        List<Fragment> fragmentList = new ArrayList<>();
        localAlbumFragment = new LocalAlbumFragment(localFiles);
        onlineAlbumFragment = new OnlineAlbumFragment();
        fragmentList.add(localAlbumFragment);
        fragmentList.add(onlineAlbumFragment);
        pagerAdapter = new AlbumViewPagerAdapter(fm, fragmentList);
        viewPager.setAdapter(pagerAdapter);

        pathList = (ListView) popupView.findViewById(R.id.pathList);
        AlbumListViewAdapter listViewAdapter = new AlbumListViewAdapter(this, folderNames, choosed);
        pathList.setAdapter(listViewAdapter);

        pathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                buttomWindow.dismiss();
                if (i != choosed) {
                    choosed = i;
                    selectPath();
                    localAlbumFragment.update(helper.getFolder(folderNames.get(i)));
                    pathName.setText(folderNames.get(i));
                }
            }
        });
        View dismiss = popupView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttomWindow.dismiss();
            }
        });

        alertBar = findViewById(R.id.alert_bar);
        alertBarWidth = Util.getWindowWidth(this) / 2;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) alertBarWidth, Util.dip2px(this, 4));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        alertBar.setLayoutParams(lp);

        initBtn();
    }

    private void loadMaterial() {
        SharedPreferences preferences = getSharedPreferences(Constant.SharedPreferencesName, Activity.MODE_PRIVATE);
        String materialListStr = preferences.getString("material_list", "");
        if (materialListStr.equals("")) {
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();

                    String status = "";
                    String data = "";
                    JSONTokener jsonParser = new JSONTokener(res);
                    try {
                        JSONObject json = (JSONObject) jsonParser.nextValue();
                        status = json.getString("status");
                        data = json.getString("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Constant.STATUS_OK.equals(status) & !data.equals("")) {

                        Gson gson = new Gson();
                        final MaterialList materialList = gson.fromJson(res, MaterialList.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onlineAlbumFragment.update(materialList);
                                SharedPreferences preferences = getSharedPreferences(Constant.SharedPreferencesName, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("material_list", res);
                                editor.commit();
                            }
                        });
                    }
                }
            };
            OnlineMaterialUtil.getInstance().getMaterialList(callback);
        } else {
            Gson gson = new Gson();
            MaterialList materialList = gson.fromJson(materialListStr, MaterialList.class);
            Log.d(TAG, "size" + materialList.getData().size());
            onlineAlbumFragment.update(materialList);
        }
    }

    private void selectPath() {
        AlbumListViewAdapter listViewAdapter = new AlbumListViewAdapter(this, folderNames, choosed);
        pathList.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
    }


    private void initBtn() {
        View choose = findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                    alertBar.setX(0);
                } else {
                    shelter.setVisibility(View.VISIBLE);
                    buttomWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);
                }
            }
        });

        material = (TextView) findViewById(R.id.material);
        material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
                alertBar.setX(alertBarWidth);
            }
        });

        View back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void initImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
                LocalAlbumUtil.getInstance().initImage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClickGridView(String path) {
        Intent data = new Intent();
        data.putExtra("path", path);
        setResult(RESULT_OK, data);
        finish();
    }
}
