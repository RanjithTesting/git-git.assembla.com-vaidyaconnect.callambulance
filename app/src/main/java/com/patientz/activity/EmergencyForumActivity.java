package com.patientz.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.util.HashMap;


public class EmergencyForumActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private WebView webview;
    private ProgressBar progressBar;
    private ValueCallback<Uri[]> mFilePathCallback;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        setSupportActionBar(toolbar);
        webview = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.view_loader);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                progressBar.setProgress(progress * 1000);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
               
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                uploadNewImage();
                return true;
            }
        });

        webview.setWebViewClient(new AppWebViewClients(this, progressBar));
        Log.d("link---1", WebServiceUrls.serverUrl + WebServiceUrls.ca_forum_url + getString(R.string.forum_id));
        webview.loadUrl(WebServiceUrls.serverUrl + WebServiceUrls.ca_forum_url + getString(R.string.forum_id), AppUtil.addHeadersForApp(EmergencyForumActivity.this,new HashMap<String, String>()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setDescription("Download file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadNewImage() {

        // Filesystem.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("link---2", WebServiceUrls.serverUrl + WebServiceUrls.searchURL + "?subject=" + query + "&forumId=" + getString(R.string.forum_id));
        webview.loadUrl(WebServiceUrls.serverUrl + WebServiceUrls.searchURL + "?subject=" + query + "&forumId=" + getString(R.string.forum_id), AppUtil.addHeadersForApp(EmergencyForumActivity.this,new HashMap<String, String>()));
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && intent != null) {
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            Log.d(TAG, "onActivityResult result : " + result);
            // Display.Log.d("EmergencyForumActivity","onActivityResult data : "+intent.getStringExtra(MediaStore.EXTRA_OUTPUT));
            if (result != null) {
                Uri[] resultsArray = new Uri[1];
                resultsArray[0] = result;
                mFilePathCallback.onReceiveValue(resultsArray);
                mFilePathCallback = null;
            } else {
                //
            }
        }
    }

    public class AppWebViewClients extends WebViewClient {

        private ProgressBar progressBar;
        private Activity currentActivity;

        public AppWebViewClients(Activity currentActivity, ProgressBar progressBar) {
            this.progressBar = progressBar;
            this.currentActivity = currentActivity;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url, AppUtil.addHeadersForApp(EmergencyForumActivity.this,new HashMap<String, String>()));
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            currentActivity.setTitle(view.getTitle());
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }
    }
}
