package com.patientz.VO;

import java.io.Serializable;

/**
 * Created by sunil on 01/06/16.
 */
public class GcmMessageVO implements Serializable {
    String eventType;
    String title;
    String textShort;
    String textLong;
    String imageUrl;
    String onClickType;
    String onClickUrl;
    long gcId;

    public long getGcId() {
        return gcId;
    }

    public void setGcId(long gcId) {
        this.gcId = gcId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextShort() {
        return textShort;
    }

    public void setTextShort(String textShort) {
        this.textShort = textShort;
    }

    public String getTextLong() {
        return textLong;
    }

    public void setTextLong(String textLong) {
        this.textLong = textLong;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOnClickType() {
        return onClickType;
    }

    public void setOnClickType(String onClickType) {
        this.onClickType = onClickType;
    }

    public String getOnClickUrl() {
        return onClickUrl;
    }

    public void setOnClickUrl(String onClickUrl) {
        this.onClickUrl = onClickUrl;
    }
}
