package com.project_test.forsix;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by kun on 2017/1/25.
 */

public class DownloadService extends Service {
    private String token;
    private NotificationManager manager;
    private int label;
    private ArrayList<String> downloadPaths;

    public DownloadService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        token = UserInfo.getInstance().getToken();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        label = 1;
        downloadPaths = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        label++;

        String path = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        //TODO:Notification得换成自定义的，带cancel按钮的
        RequestParams entity = new RequestParams(URLs.BASEURL + "download");
        entity.addHeader("Authorization", token);
        entity.addBodyParameter("path", path);
        entity.setAutoResume(true);
        String filePath = "/sdcard/CloudPan/" + name;
        entity.setSaveFilePath(filePath);

        if (downloadPaths.contains(path)) {
            Toast.makeText(this, "所选文件正在下载中，请稍后", Toast.LENGTH_SHORT).show();
        } else {
            startDownloadAndShowNotification(new DownloadParam(
                    path, name, entity, label, builder
            ));
        }

        return START_REDELIVER_INTENT;
    }

    private void startDownloadAndShowNotification(final DownloadParam paramtmp) {
        x.http().get(paramtmp.getEntity(), new Callback.ProgressCallback<File>() {

            @Override
            public void onStarted() {
                downloadPaths.add(paramtmp.getPath());
                paramtmp.getBuilder().setSmallIcon(R.mipmap.ic_launcher);
                paramtmp.getBuilder().setContentTitle(paramtmp.getName() + " 正在下载");
                paramtmp.getBuilder().setContentText("准备下载");
                paramtmp.getBuilder().setProgress(100, 0, true);
                manager.notify(paramtmp.getLabel(), paramtmp.getBuilder().build());
            }


            @Override
            public void onSuccess(final File result) {
                downloadPaths.remove(paramtmp.getPath());
                paramtmp.getBuilder().setContentText("下载完成");
                paramtmp.getBuilder().setDefaults(Notification.DEFAULT_SOUND);
                manager.notify(paramtmp.getLabel(), paramtmp.getBuilder().build());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        manager.cancelAll();
                    }
                }).start();
                if (downloadPaths.size() == 0) {
                    onDestroy();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading) {
                    int progress =
                            (int) (100 * current / total);
                    DecimalFormat df = new DecimalFormat("#");
                    String numberToShow = df.format((((double) progress) / 100.0) * 100);
                    paramtmp.getBuilder().setContentText("正在下载中" + numberToShow + "%");
                    paramtmp.getBuilder().setProgress(100, progress, false);
                    manager.notify(paramtmp.getLabel(), paramtmp.getBuilder().build());
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class DownloadParam {
    String path;
    String name;
    RequestParams entity;
    int label;
    NotificationCompat.Builder builder;

    public DownloadParam(String path, String name, RequestParams entity, int label, NotificationCompat.Builder builder) {
        this.path = path;
        this.name = name;
        this.entity = entity;
        this.label = label;
        this.builder = builder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestParams getEntity() {
        return entity;
    }

    public void setEntity(RequestParams entity) {
        this.entity = entity;
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(NotificationCompat.Builder builder) {
        this.builder = builder;
    }
}
