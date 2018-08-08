package com.patientz.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.patientz.utils.CommonUtils;
import com.patientz.webservices.WebServiceUrls;

public class OffersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.title_activity_first_responder_web_view);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(WebServiceUrls.offers);

        webView.setWebChromeClient(new WebChromeClient() {
            ProgressDialog progressDialog = null;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress < 100) {
                    if (progressDialog == null) {
                        progressDialog = CommonUtils.showProgressDialog(OffersActivity.this);
                    }
                } else {
                    CommonUtils.dismissProgressDialog(progressDialog);
                }
            }
        });
    }
}
