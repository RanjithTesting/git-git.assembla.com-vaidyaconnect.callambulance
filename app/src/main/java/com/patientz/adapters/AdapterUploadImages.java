package com.patientz.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.FileUploadImagesVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class AdapterUploadImages extends ArrayAdapter {
    DatabaseHandler dh;
    private int resource;
    private Context context;
    ArrayList<FileUploadImagesVO> uploadimages;

    public AdapterUploadImages(Context context, int resource, ArrayList<FileUploadImagesVO> uploadimages) {
        super(context, resource);
        this.resource = resource;
        this.context = context;
        this.uploadimages = uploadimages;

        dh = DatabaseHandler.dbInit(context);

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowview = convertView;
        listHolder listholder;
        if (rowview == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = inflater.inflate(resource, parent, false);
            listholder = new listHolder();
            listholder.iv_image = (ImageView) rowview.findViewById(R.id.iv_image);
            listholder.iv_cancel = (ImageView) rowview.findViewById(R.id.iv_cancel);
            listholder.smallLabel = (TextView) rowview.findViewById(R.id.smallLabel);
            rowview.setTag(listholder);
        } else {
            listholder = (listHolder) rowview.getTag();
        }

        final FileUploadImagesVO fileUploadImagesVO = uploadimages.get(position);

        if (fileUploadImagesVO.getImage_path() != null && fileUploadImagesVO.getImage_path().length() > 0) {
            try {
                //Bitmap myBitmap = BitmapFactory.decodeFile(fileUploadImagesVO.getImage_path());
                listholder.iv_image.setImageURI(Uri.parse(fileUploadImagesVO.getImage_path()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("getPage_number", "page - " + fileUploadImagesVO.getPage_number());
        if (fileUploadImagesVO.getPage_number() > 0) {
            listholder.smallLabel.setText(context.getString(R.string.page) + " " + (position + 1));
        } else {

            listholder.iv_image.setImageResource(R.drawable.reports_addreport);
            listholder.smallLabel.setText(context.getString(R.string.addpage));
            listholder.iv_cancel.setVisibility(View.GONE);
            //listholder.iv_image.setVisibility(View.GONE);

        }

        listholder.iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileUploadImagesVO.getPage_number() > 0) {
                    dh.removeFileUploadImages(fileUploadImagesVO.get_id());
                    uploadimages.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

        return rowview;

    }

    public int getCount() {
        return uploadimages.size();
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    static class listHolder {
        ImageView iv_image;
        ImageView iv_cancel;
        TextView smallLabel;
    }

}
