package com.example.kotlin_dayone.bean;

import java.io.Serializable;

public class SelectImgEntity implements Serializable {
    public String name;
    public String time;
    public String path;

    @Override
    public String toString() {
        return "SelectImgEntity{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
