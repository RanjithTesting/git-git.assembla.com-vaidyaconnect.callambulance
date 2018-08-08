package com.patientz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.PatientFileCollection;
import com.patientz.VO.UserUploadedMedia;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyUploadsAdapter extends ArrayAdapter {

    private int resource;
    private Context mContext;
    ArrayList<PatientFileCollection> patientFileCollections;
    SimpleDateFormat sdf;

    public MyUploadsAdapter(Context context, int resource, ArrayList<PatientFileCollection> patientFileCollections) {
        super(context, resource);
        this.resource = resource;
        this.mContext = context;
        this.patientFileCollections = patientFileCollections;
        sdf = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowview = convertView;
        listHolder listholder;
        if (rowview == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = inflater.inflate(resource, parent, false);
            listholder = new listHolder();
            listholder.createdby_name = (TextView) rowview.findViewById(R.id.createdby_name);
            listholder.updated_date = (TextView) rowview.findViewById(R.id.updated_date);
            listholder.iv_image = (ImageView) rowview.findViewById(R.id.iv_image);
            rowview.setTag(listholder);
        } else {
            listholder = (listHolder) rowview.getTag();
        }

        PatientFileCollection patientFile = patientFileCollections.get(position);
        if(!TextUtils.isEmpty(patientFile.getTags()))
        {
            listholder.createdby_name.setText(patientFile.getTags() + " By " + patientFile.getCreatedByUsername());
        }else
        {
            UserUploadedMedia userUploadedMedia=patientFile.getFile();
            listholder.createdby_name.setText(!TextUtils.isEmpty(userUploadedMedia.getDisplayName())?(userUploadedMedia.getDisplayName().toUpperCase()+ " By " + patientFile.getCreatedByUsername()):"Report".toUpperCase()+" By "+patientFile.getCreatedByUsername());
        }
        listholder.updated_date.setText(mContext.getString(R.string.updatedon) + sdf.format(patientFile.getDateCreated()));

        Log.e("getContentType", patientFile.getFile().getContentType());

        if (patientFile.getFile().getContentType().contains("image/jpeg")) {
            listholder.iv_image.setImageResource(R.drawable.png_icon);

            new DownloadFile(listholder.iv_image).execute(WebServiceUrls.serverUrl + WebServiceUrls.getImageThumbnail + patientFile.getFileId() + "&patientId=" + String.valueOf(AppUtil.getCurrentSelectedPatientId(mContext)), patientFile.getFileId() + ".jpg"+"&moduleType="+ Constant.IMAGE_MODULE_TYPE_REPORTS);

        } else if (patientFile.getFile().getContentType().contains("application/pdf")) {
            listholder.iv_image.setImageResource(R.drawable.pdf_icon);
        }
        return rowview;

    }

    public int getCount() {
        return patientFileCollections.size();
    }

    @Override
    public PatientFileCollection getItem(int position) {
        return patientFileCollections.get(position);
    }

// references to our captions


    static class listHolder {
        TextView updated_date, createdby_name;
        ImageView iv_image;
    }

    public void downloadFile(String fileUrl, File directory) {

        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();

            FileOutputStream fileOutput = new FileOutputStream(directory);

            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024 * 1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
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
        ImageView imageview;
        String fileName;

        public DownloadFile(ImageView imageview) {
            this.imageview = imageview;
        }

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
            File image_file = new File(Environment.getExternalStorageDirectory() + "/CallAMbulance_PDF/" + fileName);  // -> filename = maven.pdf
            Bitmap bitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            imageview.setImageBitmap(bitmap);
        }
    }

}
