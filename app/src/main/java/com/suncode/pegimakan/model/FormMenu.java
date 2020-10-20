package com.suncode.pegimakan.model;

import java.io.Serializable;

public class FormMenu implements Serializable {
    private String restoId;
    private String menuId;

    public FormMenu(String restoId, String menuId) {
        this.restoId = restoId;
        this.menuId = menuId;
    }

    public String getRestoId() {
        return restoId;
    }

    public void setRestoId(String restoId) {
        this.restoId = restoId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}