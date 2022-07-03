package com.lumyuan.filebrowser.utils;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lumyuan.androidfilemanager.AndroidFileManagerApplication;
import com.lumyuan.filebrowser.FileBrowserApplication;
import com.lumyuan.filebrowser.pojo.GameBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AppInfoProvider {

    private final PackageManager packageManager;
    //获取一个包管理器
    public AppInfoProvider(){
        packageManager = Objects.requireNonNull(AndroidFileManagerApplication.INSTANCE.getContext()).getPackageManager();
    }

    @SuppressLint("QueryPermissionsNeeded")
    public ArrayList<GameBean> getAllApps(){

        ArrayList<GameBean> list = new ArrayList<>();
        for(PackageInfo info : packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES)){
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = info.packageName;
            Drawable icon = appInfo.loadIcon(packageManager);
            String appName = appInfo.loadLabel(packageManager).toString();
            GameBean myAppInfo = new GameBean();
            myAppInfo.setPackageName(packageName);
            myAppInfo.setName(appName);
            myAppInfo.setIcon(icon);
            myAppInfo.setvCode(info.versionCode);
            myAppInfo.setvName(info.versionName);
            list.add(myAppInfo);
        }

        return list;

    }

    @SuppressLint("QueryPermissionsNeeded")
    public Map<String, GameBean> getAllAppsMap(){
        Map<String, GameBean> list = new HashMap<>();
        for(PackageInfo info : packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)){
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = info.packageName;
            Drawable icon = appInfo.loadIcon(packageManager);
            String appName = appInfo.loadLabel(packageManager).toString();
            GameBean myAppInfo = new GameBean();
            myAppInfo.setPackageName(packageName);
            myAppInfo.setName(appName);
            myAppInfo.setIcon(icon);
            myAppInfo.setvCode(info.versionCode);
            myAppInfo.setvName(info.versionName);
            list.put(myAppInfo.getPackageName(), myAppInfo);
        }
        System.out.println("list = " + list);
        return list;

    }

    public boolean filterApp(ApplicationInfo info){
        //有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，它还是系统应用，这个就是判断这种情况的
        //判断是不是系统应用
        if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            return true;
        }else return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }
}