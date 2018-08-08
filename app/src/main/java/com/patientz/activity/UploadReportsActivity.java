package com.patientz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.patientz.VO.FileUploadImagesVO;
import com.patientz.adapters.AdapterUploadImages;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadReportsActivity extends BaseActivity {
    private static final String TAG = "UploadReportsActivity";
    GridView gridView;
    DatabaseHandler dh;
    String path = "";
    String actiontype = "add";
    int selid = 0;
    private static final int PICK_FROM_CAMERA = 1;
    Bitmap bitmap = null;
    private static final int PICK_FROM_FILE = 2;
    AlertDialog.Builder builder;
    private Uri mImageCaptureUri;
    SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_upload_reports);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        gridView = (GridView) findViewById(R.id.grid_view_emergency_types);

        dh = DatabaseHandler.dbInit(UploadReportsActivity.this);

        if (dh.getFileUploadImages().size() > 0) {
            dh.deleteFileUploadImages();
        }

        ////

        try {
            dh.insertFileUploadImage(-1, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /////
        captureDialog();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");

                ArrayList<FileUploadImagesVO> uploadimages2 = dh.getFileUploadImages();
                Log.d(TAG, "***********" + uploadimages2.get(position).getImage_path());

                if (dh.getFileUploadImages().get(position).getPage_number() == -1) {

                    actiontype = "add";
                    AlertDialog dialogimg = builder.create();
                    dialogimg.show();
                } else {
                    actiontype = "edit";
                    AlertDialog dialogimg = builder.create();
                    dialogimg.show();
                    selid = uploadimages2.get(position).get_id();
                }
            }
        });
    }

    private void captureDialog() {

        final String[] items = new String[]{"From Camera", "From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogb, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "image_" + String.valueOf(System.currentTimeMillis()) + ".png");
                    mImageCaptureUri = Uri.fromFile(file);
                    try {
                        if(mImageCaptureUri!=null) {
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        }else
                        {
                            AppUtil.showToast(getApplicationContext(),getString(R.string.capture_pic_failed));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialogb.cancel();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getPath(mImageCaptureUri); // from Gallery

            if (path == null)
                path = mImageCaptureUri.getPath(); // from File Manager

            if (path != null)
                bitmap = BitmapFactory.decodeFile(path);
        } else {
            path = mImageCaptureUri.getPath();
            bitmap = BitmapFactory.decodeFile(path);
        }

        path = AppUtil.decodeFile(path, 480, 600);

        if (actiontype.contains("add")) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.REPORT_ADDED, true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.REPORT_ADDED);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            Log.e("add", "add");
            try {
                int filename = (int) (System.currentTimeMillis() % 10000);
                dh.insertFileUploadImage(filename, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("edit", "edit");
            dh.updateFileUploadImages(selid, path);
        }

        Log.e("path->", path);

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<FileUploadImagesVO> uploadimages2 = dh.getFileUploadImages();
        Log.e("uploadimages - > ", uploadimages2.size() + "");
        gridView.setAdapter(new AdapterUploadImages(getApplicationContext(), R.layout.adapter_upload_report_image, uploadimages2));

        if (defaultSharedPreferences.getBoolean("file_upload_status", false) == true) {
            Log.e("file_upload_status", "-> " + defaultSharedPreferences.getBoolean("file_upload_status", false));
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fileupload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_upload:
                Log.d(TAG, "Upload");
                ArrayList<FileUploadImagesVO> uploadimages2 = dh.getFileUploadImages();
                for (int i = 0; i < uploadimages2.size(); i++) {
                    Log.e("id - > ", uploadimages2.get(i).get_id() + "");
                    Log.e("imagepath - > ", uploadimages2.get(i).getImage_path() + "");
                    Log.e("pagenumber - > ", uploadimages2.get(i).getPage_number() + "");
                }
                if (dh.getFileUploadImagesToServer().size() > 0) {
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.ADD_REPORTS_PAGE_UPLOAD_CLICKED, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ADD_REPORTS_PAGE_UPLOAD_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    startActivity(new Intent(UploadReportsActivity.this, UploadReportsTagActivity.class));
                } else {
                    Toast.makeText(UploadReportsActivity.this, getString(R.string.choose_atleast_one_file), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
