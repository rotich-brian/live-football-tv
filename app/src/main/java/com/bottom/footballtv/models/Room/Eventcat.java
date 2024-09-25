package com.bottom.footballtv.models.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Eventcat implements Serializable {
    @PrimaryKey
    @NonNull
    private String CatId;
    private String Category;
    private String Thumbnail;
    private Long priority;

    @NonNull
    public String getCatId() {
        return CatId;
    }

    public void setCatId(@NonNull String catId) {
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

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}
