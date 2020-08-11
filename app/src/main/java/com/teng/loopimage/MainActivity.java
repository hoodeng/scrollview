package com.teng.loopimage;

import android.os.Bundle;

import com.teng.lib.ImageViewScrollView;
import com.teng.lib.TextViewScrollView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageViewScrollView imageViewScrollView;
    private TextViewScrollView textViewScrollView;

    private List<Integer> Res = Arrays.asList(R.mipmap.test1,
            R.mipmap.test2,
            R.mipmap.test3,
            R.mipmap.test4,
            R.mipmap.test5,
            R.mipmap.test6,
            R.mipmap.test7,
            R.mipmap.test8);

    private List<String> Week = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewScrollView = findViewById(R.id.image_scroll_view);
        textViewScrollView = findViewById(R.id.text_scroll_view);

        imageViewScrollView.shouldLoopPlayModels(Res);
        imageViewScrollView.startAnimator();

        textViewScrollView.shouldLoopPlayModels(Week);

        findViewById(R.id.play).setOnClickListener(v -> {
            if (textViewScrollView.isPlaying()) {
                textViewScrollView.stopAnimator();
                imageViewScrollView.stopAnimator();
            } else {
                textViewScrollView.startAnimator();
                imageViewScrollView.startAnimator();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
