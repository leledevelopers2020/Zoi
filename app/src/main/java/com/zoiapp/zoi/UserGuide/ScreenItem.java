package com.zoiapp.zoi.UserGuide;

public class ScreenItem {

    String Title,Description;
    int Img;
    public ScreenItem(String title,String description,int img){
        Title=title;
        Description=description;
        Img=img;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getImg() {
        return Img;
    }

    public void setImg(int img) {
        Img = img;
    }
}