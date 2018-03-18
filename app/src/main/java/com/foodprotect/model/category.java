package com.foodprotect.model;


public class category {
    private int id;
    private String title;

    private String image;

    public category(int id, String title, String image) {
        this.id = id;
        this.title = title;

        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }



    public String getImage() {
        return image;
    }
}