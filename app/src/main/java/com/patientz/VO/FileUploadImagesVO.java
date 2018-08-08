package com.patientz.VO;

/**
 * Created by windows.7 on 11/18/2016.
 */

public class FileUploadImagesVO {
    int _id, page_number;
    String image_path;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
