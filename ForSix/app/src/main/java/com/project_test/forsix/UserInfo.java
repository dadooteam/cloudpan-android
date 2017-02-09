package com.project_test.forsix;


import com.project_test.forsix.UploadRelated.UploadBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kun on 2017/1/18.
 */
public class UserInfo {
    private String token;

//    private ArrayList<String> filepathsToUpload;//文件路径

    private ArrayList<UploadBean> filesToUpload;//文件

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
//        filepathsToUpload=new ArrayList<>();
        filesToUpload=new ArrayList<>();
    }

//    public ArrayList<String> getFilepathsToUpload() {
//        return filepathsToUpload;
//    }

    public ArrayList<UploadBean> getFilesToUpload(){return filesToUpload;}

}
