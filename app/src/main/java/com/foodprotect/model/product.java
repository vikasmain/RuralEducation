package com.foodprotect.model;

/**
 * Created by dell on 06-04-2017.
 */
public class product {
    private String category;
    private String title;
    private String desc;
    private String price;
    private String image;
    private String uid;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public product(String title, String price, String image, String uid) {
        this.title = title;
        this.price = price;
        this.image = image;
        this.uid = uid;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTitle() {
        return title;
    }
    public product(){

    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
