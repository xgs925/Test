package com.photostars.test.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.photostars.test.AppContext;
import com.photostars.test.LocalImageHelper;
import com.photostars.test.R;
import com.photostars.test.adapter.AlbumGridViewAdapter;
import com.photostars.test.adapter.AlbumListViewAdapter;
import com.photostars.test.adapter.AlbumViewPagerAdapter;
import com.photostars.test.fragment.LocalAlbumFragment;
import com.photostars.test.fragment.OnlineAlbumFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlbumActivity extends FragmentActivity {
    String TAG = "AlbumActivity";
    private LocalImageHelper helper;
    private ViewPager viewPager;

    private List<String> folderNames = new ArrayList<>();
    List<LocalImageHelper.LocalFile> localFiles;
    private PopupWindow buttomWindow;
    private LocalAlbumFragment localAlbumFragment;
    private AlbumViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
//        initView();
        initImage();
    }

    private void initView() {
        initBtn();
        helper = LocalImageHelper.getInstance();
        viewPager = (ViewPager) findViewById(R.id.viewPager);


        Map<String, List<LocalImageHelper.LocalFile>> folders = helper.getFolderMap();
        Iterator iter = folders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            folderNames.add(key);
        }
        localFiles = helper.getFolder(folderNames.get(1));
        for (String s : folderNames
                ) {
            Log.d(TAG, s);
        }

        View popupView = getLayoutInflater().inflate(R.layout.popup_album_choose, null);
        buttomWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        buttomWindow.setOutsideTouchable(true);
        buttomWindow.setBackgroundDrawable(new BitmapDrawable());
        buttomWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止挡住系统键
        buttomWindow.setAnimationStyle(R.style.pop_button_anim);

        FragmentManager fm = getSupportFragmentManager();

        List<Fragment> fragmentList = new ArrayList<>();
        localAlbumFragment = new LocalAlbumFragment(localFiles);
        fragmentList.add(localAlbumFragment);
        fragmentList.add(new OnlineAlbumFragment());
        pagerAdapter = new AlbumViewPagerAdapter(fm, fragmentList);
        viewPager.setAdapter(pagerAdapter);

        ListView pathList = (ListView) popupView.findViewById(R.id.pathList);
        pathList.setAdapter(new AlbumListViewAdapter(this, folderNames));
        pathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                buttomWindow.dismiss();
//                String fragmentTag = localAlbumFragment.getTag();
//                localFiles=helper.getFolder(folderNames.get(i));
//                buttomWindow.dismiss();
//                FragmentTransaction ft =fm.beginTransaction();
//                ft.remove(localAlbumFragment);
//                ft.add(new LocalAlbumFragment(localFiles),fragmentTag);
//                ft.commit();
                localFiles = helper.getFolder(folderNames.get(i));
                localAlbumFragment.update(localFiles);

            }
        });
    }


    private void initBtn() {
        Button choose = (Button) findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttomWindow.showAtLocation(((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0), Gravity.BOTTOM, 0, 0);
            }
        });
    }

    private void initImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
                LocalImageHelper.getInstance().initImage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();

                    }
                });
            }
        }).start();
    }
}
