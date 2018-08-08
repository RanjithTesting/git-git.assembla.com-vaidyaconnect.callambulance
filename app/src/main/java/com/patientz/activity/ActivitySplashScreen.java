package com.patientz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivitySplashScreen extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        TextView tvContinue = (TextView) findViewById(R.id.tv_continue);
        tvContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue:
                Intent intent = new Intent(ActivitySplashScreen.this,
                        ActivityTermsAndConditions.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
