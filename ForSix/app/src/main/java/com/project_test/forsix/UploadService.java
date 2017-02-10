package com.project_test.forsix;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.project_test.forsix.UploadRelated.UploadBean;
import com.project_test.forsix.UploadRelated.UploadResponseEntity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

public class UploadService extends Service {
    private String token;
    private String currentPath;
    private ArrayList<UploadBean> filePahtsToUpload;
    private ArrayList<File> fileToUpload;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        token = UserInfo.getInstance().getToken();
        filePahtsToUpload=UserInfo.getInstance().getFilesToUpload();
        fileToUpload=new ArrayList<>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        currentPath=intent.getStringExtra("currentPath");
        fileToUpload.clear();
        for (UploadBean beantmp:filePahtsToUpload){
            if (beantmp.getStatus()==0){
            File filetmp=new File(beantmp.getFileName());
            fileToUpload.add(filetmp);}
        }
        for (final File file:fileToUpload) {
            final RequestParams uploadEntity = new RequestParams(URLs.BASEURL + "upload");
            uploadEntity.addHeader("Authorization", token);
            uploadEntity.addBodyParameter("path", currentPath);
            uploadEntity.addBodyParameter("file", file);
            x.http().post(uploadEntity, new Callback.ProgressCallback<UploadResponseEntity>() {
                @Override
                public void onSuccess(UploadResponseEntity result) {
                    ArrayList<UploadBean> tmp=UserInfo.getInstance().getFilesToUpload();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (tmp.get(i).getFileName().equals(file.getAbsolutePath()))
                        {
                            tmp.remove(i);
                            if (tmp.size()==0){
                                onDestroy();
                            }
                        }
                    }

                    if (Upload2Activity.instance.uploadAdapter != null) {
                        Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(UploadService.this, file.getName() + "上传完成", Toast.LENGTH_SHORT).show();
                }


                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ArrayList<UploadBean> tmp=UserInfo.getInstance().getFilesToUpload();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (tmp.get(i).getFileName().equals(file.getAbsolutePath()))
                        {
                            tmp.remove(i);
                        }
                    }
                    if (Upload2Activity.instance.uploadAdapter != null) {
                        Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(UploadService.this, file.getName() + "上传失败，请重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    ArrayList<UploadBean> tmp=UserInfo.getInstance().getFilesToUpload();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (tmp.get(i).getFileName().equals(file.getAbsolutePath()))
                        {
                            tmp.remove(i);
                        }
                    }
                    if (Upload2Activity.instance.uploadAdapter != null) {
                        Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    ArrayList<UploadBean> tmp=UserInfo.getInstance().getFilesToUpload();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (tmp.get(i).getFileName().equals(file.getAbsolutePath())){
                            tmp.get(i).setStatus(1);
                        }
                    }
                    if (Upload2Activity.instance.uploadAdapter != null) {
                        Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    ArrayList<UploadBean> tmp=UserInfo.getInstance().getFilesToUpload();
                    for (int i = 0; i < tmp.size(); i++) {
                        if (tmp.get(i).getFileName().equals(file.getAbsolutePath())){
                            int progress =
                                    (int) (100 * current / total);
                            tmp.get(i).setProgress(progress);
                        }
                    }
                    if (Upload2Activity.instance.uploadAdapter != null) {
                        Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
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
