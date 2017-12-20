package com.think.bitmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button btnBitmap;
    protected Button btnBecome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_start);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_bitmap) {
            startActivity(new Intent(this,MainActivity.class));
        } else if (view.getId() == R.id.btn_become) {
            startActivity(new Intent(this,BecomeActivity.class));
        }
    }

    private void initView() {
        btnBitmap = (Button) findViewById(R.id.btn_bitmap);
        btnBitmap.setOnClickListener(StartActivity.this);
        btnBecome = (Button) findViewById(R.id.btn_become);
        btnBecome.setOnClickListener(StartActivity.this);
    }
}
