package com.photostars.test.bean;

import java.util.List;

/**
 * Created by Photostsrs on 2016/5/19.
 */
public class MaterialList {
    String status;
    String imageUrl;
    String showUrl;
    List<Material> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public List<Material> getData() {
        return data;
    }

    public void setData(List<Material> data) {
        this.data = data;
    }
}
