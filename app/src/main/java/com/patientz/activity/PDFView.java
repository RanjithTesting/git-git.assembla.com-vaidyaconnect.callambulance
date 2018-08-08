package com.patientz.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by windows.7 on 11/22/2016.
 */

public class PDFView extends BaseActivity {
    long fileid = 0;
    String content_type = "";
    private int moduleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        fileid = getIntent().getLongExtra("fileid", 0);
        if(getIntent().getIntExtra("moduleType", 0)!=0)
        {
            moduleType = getIntent().getIntExtra("moduleType", 0);
        }
        Log.e("fileid--->", "" + fileid);
        content_type = getIntent().getStringExtra("content_type");
        Log.e("content_type--->", "" + content_type);

        if (content_type.contains("pdf")) {
            new DownloadFile().execute(WebServiceUrls.serverUrl + WebServiceUrls.getPatientAttachment + fileid + "&patientId=" + String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())+"&moduleType="+moduleType), fileid + ".pdf");
            Log.e("url", WebServiceUrls.serverUrl + WebServiceUrls.getPatientAttachment + fileid + "&patientId=" + String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())));
        } else {
            new DownloadFile().execute(WebServiceUrls.serverUrl + WebServiceUrls.getPatientAttachment + fileid + "&patientId=" + String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())+"&moduleType="+moduleType), fileid + ".jpeg");
            Log.e("url", WebServiceUrls.serverUrl + WebServiceUrls.getPatientAttachment + fileid + "&patientId=" + String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())));
        }

    }

    public void downloadFile(String fileUrl, File directory) {

        try {
            Log.d("fileUrl",fileUrl);
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();

            FileOutputStream fileOutput = new FileOutputStream(directory);
            Log.d("response_code=",urlConnection.getResponseCode()+"");

            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getErrorStream();
            if (inputStream == null) {
                inputStream = urlConnection.getInputStream();
            }
            Log.d("response_code=",urlConnection.getResponseCode()+"");
            Log.d("1","1");
            byte[] buffer = new byte[1024 * 1024];
            int bufferLength = 0;
            Log.d("1","1");


            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            Log.d("1","1");

            // close the output stream when complete //
            fileOutput.close();
            Log.e("done", "done");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
        String fileName;

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "CallAMbulance_PDF");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            File pdfFile;
            if (content_type.contains("pdf")) {
                pdfFile = new File(Environment.getExternalStorageDirectory() + "/CallAMbulance_PDF/" + fileName);  // -> filename = maven.pdf
            } else {
                pdfFile = new File(Environment.getExternalStorageDirectory() + "/CallAMbulance_PDF/" + fileName);  // -> filename = maven.pdf
            }

            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, content_type);
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(pdfIntent);
                finish();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PDFView.this, getString(R.string.no_view_pdf), Toast.LENGTH_SHORT).show();
            }
        }
    }

}