package com.photostars.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.photostars.test.Constant;
import com.photostars.test.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivityForResult(intent, Constant.ALBUM_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.ALBUM_REQUEST_CODE:
                    Intent intent = new Intent(MainActivity.this, TextEditActivity.class);
                    intent.putExtra("path", data.getStringExtra("path"));
                    startActivityForResult(intent, Constant.BG_EDIT_REQUEST_CODE);
                    break;
            }
        }
    }
}
