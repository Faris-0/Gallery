package com.yuuna.gallery;

public class GalleryData {

    private String date;
    private PhotoData photoData;

    public GalleryData(String date, PhotoData photoData) {
        this.date = date;
        this.photoData = photoData;
    }

    public String getDate() {
        return date;
    }

    public PhotoData getPhotoData() {
        return photoData;
    }
}
