package com.project_test.forsix;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service {
    private String token;
//TODO:写上传

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        token = UserInfo.getInstance().getToken();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
