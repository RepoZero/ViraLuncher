package com.smrahmadi.viraluncher.models;

import android.graphics.drawable.Drawable;

/**
 * Created by lincoln on 3/23/18.
 */

public class Application {

    private String name;
    private String packageName;
    private String version;
    private Drawable icon ;
    private boolean isSysytem ;



    public Application(String name, String packageName, String version, Drawable icon,boolean isSysytem ) {
        this.name = name;
        this.packageName = packageName;
        this.version = version;
        this.icon = icon;
        this.isSysytem = isSysytem;

    }

    public Application() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSysytem() {
        return isSysytem;
    }

    public void setSysytem(boolean sysytem) {
        isSysytem = sysytem;
    }
}
