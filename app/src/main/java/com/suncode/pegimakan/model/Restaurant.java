package com.suncode.pegimakan.model;

import com.google.firebase.database.Exclude;

public class Restaurant {
    String name;
    String description;
    String openHours, closingHours;
    String key;

    public Restaurant() {
    }

    public Restaurant(String name, String description, String openHours, String closingHours) {
        this.name = name;
        this.description = description;
        this.openHours = openHours;
        this.closingHours = closingHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(String closingHours) {
        this.closingHours = closingHours;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
