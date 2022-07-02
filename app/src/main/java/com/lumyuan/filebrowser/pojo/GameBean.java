package com.lumyuan.filebrowser.pojo;

import android.graphics.drawable.Drawable;

public class GameBean {
    private String name;
    private int vCode;
    private String vName;
    private Drawable icon;
    private String packageName;

    public GameBean() {
    }

    public GameBean(String name, int vCode, String vName, Drawable icon) {
        this.name = name;
        this.vCode = vCode;
        this.vName = vName;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getvCode() {
        return vCode;
    }

    public void setvCode(int vCode) {
        this.vCode = vCode;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "GameBean{" +
                "name='" + name + '\'' +
                ", vCode=" + vCode +
                ", vName='" + vName + '\'' +
                ", icon=" + icon +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
