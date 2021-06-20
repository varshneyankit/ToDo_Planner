package com.assignment.todoplanner.pojos;


import com.google.firebase.Timestamp;

import java.util.Date;

public class Task {

    private String id;
    private String title;
    private String description;
    private Date creationTime;
    private Date completionTime;
    private Boolean taskStatus;
    private Integer color;

    public Task() {
    }

    public Task(String title, String description, Integer color, Boolean taskStatus) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.taskStatus = taskStatus;
        this.creationTime = Timestamp.now().toDate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public Boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Boolean taskStatus) {
        this.taskStatus = taskStatus;
    }
}
