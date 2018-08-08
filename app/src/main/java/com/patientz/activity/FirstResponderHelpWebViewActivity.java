package com.patientz.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.patientz.utils.CommonUtils;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

public class FirstResponderHelpWebViewActivity extends BaseActivity {

    private String TAG="FirstResponderHelpWebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_responder_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.title_activity_first_responder_web_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(WebServiceUrls.emthelp);
        webView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
                Log.d(TAG, "event"+event.getButtonState());
                Log.d(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            ProgressDialog progressDialog = null;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress < 100) {
                    if (progressDialog == null) {
                        progressDialog = CommonUtils.showProgressDialog(FirstResponderHelpWebViewActivity.this);
                    }
                } else {
                    CommonUtils.dismissProgressDialog(progressDialog);
                }
            }
        });
    }


}
