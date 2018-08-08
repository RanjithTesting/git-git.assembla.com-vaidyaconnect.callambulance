package com.patientz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.patientz.webservices.WebServiceUrls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageLruCache extends LruCache<String, Bitmap> implements
        ImageCache {

    private Context context;

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public ImageLruCache() {
        this(getDefaultLruCacheSize());
    }

    public ImageLruCache(int maxSize) {
        super(maxSize);
        Log.d("ImageLruCache-", "ImageLruCache");
    }

    public ImageLruCache(int maxSize, Context context) {
        super(maxSize);
        this.context = context;
        Log.d("ImageLruCache-", "ImageLruCache");
    }

    @Override
    public Bitmap getBitmap(String arg0) {
        Log.d("ImageLruCache-", "getBitmap");
        return null;
    }

    @Override
    public void putBitmap(String arg0, Bitmap bitMap) {
        Log.d("ImageLruCache-", "putBitmap");
        savebitmap(bitMap);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        Log.d("ImageLruCache-", "sizeOf");
        return super.sizeOf(key, value);
    }

    private File savebitmap(Bitmap bitMap) {
        String filename = "orgIcon";
        File cnxDir = null;
        if (context != null) {
            cnxDir = new File(Environment.getExternalStorageDirectory(),
                    WebServiceUrls.NutrifiImages);
        } else {

            cnxDir = new File(Environment.getExternalStorageDirectory(),
                    WebServiceUrls.DoctrzImages);
        }
        OutputStream outStream = null;

        if (!cnxDir.exists()) {
            cnxDir.mkdirs();
        }
        File file = new File(cnxDir, filename + ".jpg");

        if (file.exists()) {
            file.delete();
            file = new File(cnxDir, filename + ".jpg");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

}
