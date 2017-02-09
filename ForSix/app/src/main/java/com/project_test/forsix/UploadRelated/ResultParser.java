package com.project_test.forsix.UploadRelated;

import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;

/**
 * Created by kun on 2017/2/9.
 */

public class ResultParser implements ResponseParser {
    @Override
    public void checkResponse(UriRequest request) throws Throwable {
    }

    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {
        UploadResponseEntity responseEntity = new UploadResponseEntity();
        responseEntity.setResult(result);
        return responseEntity;
    }
}
