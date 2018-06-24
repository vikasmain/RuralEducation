package com.foodprotect.model;

/**
 * Created by dell on 30-03-2018.
 */

public class Villages {

    public String name;
    public Integer id;

    public Villages(Integer id,String name) {
        this.name = name;
        this.id = id;
    }

    public Villages() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
