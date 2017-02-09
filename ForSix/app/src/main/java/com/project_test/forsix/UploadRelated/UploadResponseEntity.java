package com.project_test.forsix.UploadRelated;

import org.xutils.http.annotation.HttpResponse;

/**
 * Created by kun on 2017/2/9.
 */

@HttpResponse(parser = ResultParser.class)
public class UploadResponseEntity {
    private String result;
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}



