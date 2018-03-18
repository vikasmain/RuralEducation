package com.foodprotect.model;

/**
 * Created by dell on 06-04-2017.
 */
public class PostUsers {
    private String Name;
    private String image;
    private Double latitude;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    private Double longitude;
    private String phone_no;
    public PostUsers(String Name, String image) {
        this.Name = Name;
        this.image = image;

    }
    public PostUsers(){

    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
