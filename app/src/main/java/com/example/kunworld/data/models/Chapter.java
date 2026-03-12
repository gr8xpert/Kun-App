package com.example.kunworld.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chapters")
public class Chapter {
    @PrimaryKey
    @NonNull
    private String id;
    private String parentId; // course or book id
    private String parentType; // "course" or "book"
    private String title;
    private String content; // HTML or Markdown content
    private int orderIndex;
    private int estimatedReadTime; // in minutes

    public Chapter() {
        this.id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getEstimatedReadTime() {
        return estimatedReadTime;
    }

    public void setEstimatedReadTime(int estimatedReadTime) {
        this.estimatedReadTime = estimatedReadTime;
    }
}
