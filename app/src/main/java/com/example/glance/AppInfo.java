package com.example.glance;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private Bitmap icon=null;
    private String category;
    private String name;
    private String title;
    private String text;
    private String datetime;
    private String num;
    private int numicon;

    public int getNumicon() {
        return numicon;
    }

    public void setNumicon(int numicon) {
        this.numicon = numicon;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
