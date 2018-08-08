package com.patientz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.patientz.webservices.WebServiceUrls.serverUrl;


public class AskExpertActivity extends BaseActivity {

    WebView webView;
    LinearLayout progress;
    TextView progressText;
    private String mainUrl;
    private static String TAG = "AskExpertActivity";
    //ImageButton homeButton;
    String questionId;
    String favStatus;
    //TextView heading;
    private DownloadFile startDownload;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final String REQUEST_DIALOG_CODE = "Alert_Message";
    private final static int timeoutCode = 99;
    Menu menuQnA;
    MenuInflater inflaterQnA;

    MenuItem favButton;

    private BasicHttpContext mHttpContext;
    private ActionBar actionBar;

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qna);
        progress = (LinearLayout) findViewById(R.id.qna_progressBar);
        progressText = (TextView) findViewById(R.id.qna_progressText);
        webView = (WebView) findViewById(R.id.qnaWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            //fix for keyboard launch
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setDownloadListener(new CustomDownloadListener());
        webView.addJavascriptInterface(new WebAppInterface(this), "Android"); //binds interface(WebAppInterface class) to javascript
        webView.setWebChromeClient(new CustomWebChromeClient());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ask Expert");
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.loadUrl(mainUrl, AppUtil.addHeadersForApp(AskExpertActivity.this,new HashMap<String, String>()));//loadUrl-->loads the web page into WebView

    }

    @Override
    public void onResume() {
        if (webView.canGoBack()) {
        } else {

        }
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(
            MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fav:
                finish();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qna, menu);
        favButton = menu.findItem(R.id.menu_fav);
        this.inflaterQnA = getMenuInflater();
        this.menuQnA = menu;
        return true;
    }


    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void status(String id, String status) {
        }

        @JavascriptInterface
        public void setHeading(String value) {
            final String headerValue = decode(value);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    actionBar.setTitle(headerValue);
                }
            });
        }

        @JavascriptInterface
        public void login(String errorCode) {
            onResume();
        }
    }

    private String decode(String value) {
        return value.replace("&amp;", "&");
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case timeoutCode:
                Intent intent = this.getIntent();
                this.finish();
                startActivity(intent);
                break;
            case FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage)
                    break;
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                        : data.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
            default:
                break;
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressText.setText("   " + newProgress + " %");
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d(TAG, cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Log.d(TAG, "upload message: " + uploadMsg + " ,accept type: " + acceptType + " capture: " + capture);
            openFileChooser(uploadMsg, acceptType);

        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            Log.d(TAG, "upload message: " + uploadMsg + " <with string>: " + acceptType);
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

    }

    private class CustomWebViewClient extends WebViewClient {


        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d(TAG, "On LoadResource..");
            super.onLoadResource(view, url);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "On PageStarted..");
            progress.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            if ("/rest/webservices/questionsDetails".equals(Uri.parse(url).getEncodedPath().trim())) {
                //changeButtonToFav();
            } else {
                //changeButtonToHome(view);
            }
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            Log.d(TAG, "errorcode=" + errorCode + " Description=" + description + " failing url=" + failingUrl);

            super.onReceivedError(view, errorCode, description, failingUrl);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(Uri.parse(serverUrl).getHost())) {
                view.loadUrl(url);
                Log.d("sending url ..", url);
                Log.d("baseurl: ", Uri.parse(url).getHost().toString());
                Log.d(TAG, "encoded path: " + Uri.parse(url).getEncodedPath());
                Log.d(TAG, "encoded query: " + Uri.parse(url).getEncodedQuery());
                Log.d(TAG, "encoded query parameter: " + Uri.parse(url).getQueryParameter("="));
                return false;
            } else {
                return true;
            }
        }
    }

    public class DownloadFile extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        Context context;
        String fileUrl;
        long contentLength;
        String fileName;
        int responseCode;
        String type;
        Handler mProgressHandler;

        public DownloadFile(Context context, String url, long size, String filename, String mimeType) {
            this.context = context;
            this.fileUrl = url;
            this.contentLength = size;
            this.fileName = filename;
            this.type = mimeType;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AskExpertActivity.this);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("Downloading File");

            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax((int) contentLength);
            dialog.setProgress(0);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "hide", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            final String STORAGE_PATH = context.getResources().getString(
                    R.string.profileImagePath);

            final int BUFFER_SIZE = 1024 * 23;
            HttpResponse loginResp = null;
            try {
                loginResp = downloadAttachment(context, fileUrl, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File cnxDir = new File(Environment.getExternalStorageDirectory(),
                    STORAGE_PATH);
            if (!cnxDir.exists()) {
                cnxDir.mkdirs();
            }
            File file = new File(cnxDir, fileName);

            if (loginResp != null
                    && !(loginResp.toString().equalsIgnoreCase("noImage"))) {

                InputStream con;
                try {
                    con = loginResp.getEntity().getContent();
                    BufferedInputStream bis = new BufferedInputStream(con,
                            BUFFER_SIZE);

                    FileOutputStream fos;
                    fos = new FileOutputStream(file);

                    byte[] bArray = new byte[BUFFER_SIZE];
                    int current = 0;
                    long read = 0;
                    while (current != -1) {
                        fos.write(bArray, 0, current);
                        current = bis.read(bArray, 0, BUFFER_SIZE);
                        read = read + current;
                        dialog.incrementProgressBy(current);

                    }
                    fos.flush();
                    fos.close();
                    bis.close();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            dialog.setMessage(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            startDownload = null;
            dialog.dismiss();
            super.onPostExecute(result);

            final String STORAGE_PATH = context.getResources().getString(
                    R.string.profileImagePath);
            File cnxDir = new File(Environment.getExternalStorageDirectory(),
                    STORAGE_PATH);
            if (cnxDir.exists()) {
                File file = new File(cnxDir, fileName);

                String ext = fileName.substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

                openDownloadedFile(Uri.fromFile(file), type);

            }
        }

        @Override
        protected void onCancelled() {
            startDownload = null;
            dialog.dismiss();
            Log.d(TAG, "cancelled called..for async.. ");
            super.onCancelled();
        }
    }


    private void openDownloadedFile(final Uri uri, final String type) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("File Downloaded ");
        alert.setPositiveButton("Open File", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.d(TAG, "" + e.getMessage());

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                }
            }
        });

        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    public class CustomDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype, long contentLength) {
            String fileName = null;
            Log.d(TAG, "File url: " + url + " user agent: " + userAgent + " content disposition: " + contentDisposition + " mimetype: " + mimetype + " content length: " + contentLength);
            BasicHeader header = new BasicHeader("Content-Disposition", contentDisposition);
            HeaderElement[] helelms = header.getElements();
            if (helelms.length > 0) {
                HeaderElement helem = helelms[0];
                if (helem.getName().equalsIgnoreCase("attachment")) {
                    NameValuePair nmv = helem.getParameterByName("filename");
                    if (nmv != null) {
                        startDownload = (DownloadFile) new DownloadFile(AskExpertActivity.this, url, contentLength, nmv.getValue().trim(), mimetype.trim()).execute();
                        //System.out.println(nmv.getValue());
                    } else {
                        Toast.makeText(AskExpertActivity.this, "file not found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    public void backOnClick(View v) {

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            this.finish();
        }
    }

    public HttpResponse downloadAttachment(final Context context,
                                           final String fileUrl, final String fileName) throws Exception {
        final String url = fileUrl;
        Log.d(TAG, "attachment file url : " + fileUrl);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = AppUtil.CustomHttpGet(url, context);
            return httpClient.execute(request, mHttpContext);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e);
        }

    }
}
