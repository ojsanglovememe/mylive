package com.src.isec.mvp.model;


import com.src.isec.base.BaseActivity;

import java.io.Serializable;

/**
 * Created by Tracy on 2017/11/11.
 */

public class MenuEntity implements Serializable {

    private String menuID;
    private String menuTitle;
    private String menuDesc;
    private int menuIcon;

    private int menuType;

    private Class<? extends BaseActivity> cls;

    private boolean isChecked = false;

    public MenuEntity() {
    }

    public MenuEntity(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public MenuEntity(String menuTitle, int menuIcon) {
        this.menuTitle = menuTitle;
        this.menuIcon = menuIcon;
    }

    public MenuEntity(String menuTitle, String menuDesc) {
        this.menuTitle = menuTitle;
        this.menuDesc = menuDesc;
    }

    public MenuEntity(String menuTitle, String menuDesc, int menuIcon) {
        this.menuTitle = menuTitle;
        this.menuDesc = menuDesc;
        this.menuIcon = menuIcon;
    }

    public MenuEntity(String menuTitle, int menuIcon, Class<? extends BaseActivity> cls) {
        this.menuTitle = menuTitle;
        this.menuIcon = menuIcon;
        this.cls = cls;
    }

    public MenuEntity(String menuTitle, int menuIcon, int menuType, Class<? extends BaseActivity> cls) {
        this.menuTitle = menuTitle;
        this.menuIcon = menuIcon;
        this.menuType = menuType;
        this.cls = cls;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Class<? extends BaseActivity> getCls() {
        return cls;
    }

    public void setCls(Class<? extends BaseActivity> cls) {
        this.cls = cls;
    }

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getMenuDesc() {
        return menuDesc;
    }

    public void setMenuDesc(String menuDesc) {
        this.menuDesc = menuDesc;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
