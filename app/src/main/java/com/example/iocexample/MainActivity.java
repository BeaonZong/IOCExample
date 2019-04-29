package com.example.iocexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.iocexample.base.BaseActivity;
import com.example.library.annotation.ContentView;
import com.example.library.annotation.InjectView;
import com.example.library.annotation.onClick;

//setContentView(R.layout.activity_main);
@ContentView(R.layout.activity_main)

public class MainActivity extends BaseActivity {

    @InjectView(R.id.tv)
    private TextView tv;

    @InjectView(R.id.btn)
    private Button btn;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @onClick({R.id.btn, R.id.tv})
    public void ssdfs(View view) {
        switch (view.getId()) {
            case R.id.btn:
                break;
            case R.id.tv:
                break;
        }
    }

    private void abc() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }
}
