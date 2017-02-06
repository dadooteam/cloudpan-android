package com.project_test.forsix;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kun on 2017/1/18.
 */
public class UserInfo {
    private String token;

    private ArrayList<String> filesToUpload;

    private String currentPath;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private static UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    private UserInfo() {
        filesToUpload=new ArrayList<>();
    }

    public ArrayList<String> getFilesToUpload() {
        return filesToUpload;
    }

    public void setFilesToUpload(ArrayList<String> filesToUpload) {
        this.filesToUpload = filesToUpload;
    }
}
