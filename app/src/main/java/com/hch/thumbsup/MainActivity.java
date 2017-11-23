package com.hch.thumbsup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ThumbsUpCountView thumbsUpCountView = (ThumbsUpCountView) findViewById(R.id.thumbs_view);
        final ThumbsUpCountView thumbsUpCountView2 = (ThumbsUpCountView) findViewById(R.id.thumbs_view2);
        final ThumbsUpCountView thumbsUpCountView3 = (ThumbsUpCountView) findViewById(R.id.thumbs_view3);
        final ThumbsUpCountView thumbsUpCountView4 = (ThumbsUpCountView) findViewById(R.id.thumbs_view4);
        thumbsUpCountView.initData(true, 168);
        thumbsUpCountView2.initData(true, 168);
        thumbsUpCountView3.initData(true, 168);
        thumbsUpCountView4.initData(true, 168);
        thumbsUpCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsUpCountView.priseChange();
            }
        });
        thumbsUpCountView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsUpCountView2.priseChange();
            }
        });
        thumbsUpCountView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsUpCountView3.priseChange();
            }
        });
        thumbsUpCountView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsUpCountView4.priseChange();
            }
        });
        final EditText editText = (EditText) findViewById(R.id.current_count);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = String.valueOf(editText.getText());
                if (!TextUtils.isEmpty(str)) {
                    try {
                        thumbsUpCountView.initData(false, Integer.parseInt(str));
                        thumbsUpCountView2.initData(false, Integer.parseInt(str));
                        thumbsUpCountView3.initData(false, Integer.parseInt(str));
                        thumbsUpCountView4.initData(false, Integer.parseInt(str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
