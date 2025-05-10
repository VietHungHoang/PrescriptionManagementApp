package com.mad.prescriptionmanagementapp.data;

public class DrugSection {
    private String title;
    private String content;
    private boolean isExpanded;

    public DrugSection(String title, String content) {
        this.title = title;
        this.content = content;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}