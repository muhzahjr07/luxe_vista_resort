package com.example.luxevistaresort;

public class DashboardItem {
    private String icon;
    private String title;
    private String subtitle;

    public DashboardItem(String icon, String title, String subtitle) {
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
