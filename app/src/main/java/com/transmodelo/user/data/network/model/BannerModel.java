package com.transmodelo.user.data.network.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerModel {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("banner_url")
    @Expose
    private String banner_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanner_url() {
        return banner_url;
    }

    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }
}
