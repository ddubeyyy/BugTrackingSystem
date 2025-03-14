package com.bugtracker.bugtracker;

public class Bug {
    private int id;
    private String title;
    private String description;
    private String status;
    private String priority;

    // Constructor
    public Bug(int id, String title, String description, String status, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}
