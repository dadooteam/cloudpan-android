package com.project_test.forsix.UploadRelated;

/**
 * Created by kun on 2017/2/9.
 */

public class UploadBean {
    private int status;//0是没上传,1是正在上传
    private int progress;
    private String fileName;

    public UploadBean(int status, int progress, String fileName) {
        this.status = status;
        this.progress = progress;
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
