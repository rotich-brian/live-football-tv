package com.bottom.footballtv.models;

public class Eventcat {
    private String CatId;
    private String Category;
    private String Thumbnail;

    public String getCatId() {
        return CatId;
    }

    public void setCatId(String catId) {
        CatId = catId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }


    @Override
    public String toString() {
        return "Eventcat{" +
                "Category='" + Category + '\'' +
                '}';
    }
}
