package com.project_test.forsix;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class UploadActivity extends AppCompatActivity {
    private FrameLayout frame;
    private Button fileUpload, photoUpload, backToCloud;
    private int fragmentLabel;
    private PhotoUploadFragment photoFragment;
    private FileUploadFragment fileFragment;
    private FragmentManager manager;
    FragmentTransaction transaction;
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);
        currentPath = getIntent().getStringExtra("currentPath");
        if (currentPath.equals("")) {
            currentPath = null;
        }
        UserInfo.getInstance().setCurrentPath(currentPath);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        photoFragment = new PhotoUploadFragment();
        fileFragment = new FileUploadFragment();
        init();
        //TODO:传照片和传文件这块的逻辑有问题，完全重新安排
    }

    private void init() {
        fragmentLabel = 2;//1传文件，2传照片
        frame = (FrameLayout) findViewById(R.id.frame);
        photoUpload = (Button) findViewById(R.id.photo_upload);
        fileUpload = (Button) findViewById(R.id.file_upload);
        backToCloud = (Button) findViewById(R.id.goto_cloud_files);

        final Intent intent = new Intent(UploadActivity.this, ShowCloudFilesActivity.class);
        backToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });

        transaction.add(R.id.frame, fileFragment, "1");
        transaction.add(R.id.frame, photoFragment, "2");
        transaction.commit();

        transaction = null;
        transaction = manager.beginTransaction();
        transaction.hide(fileFragment);
        transaction.show(photoFragment);
        transaction.commit();

        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentLabel == 2) {
                    transaction = null;
                    transaction = manager.beginTransaction();
                    transaction.show(fileFragment);
                    transaction.hide(photoFragment);
                    transaction.commit();
                    fragmentLabel = 1;
                }
            }
        });

        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentLabel == 1) {
                    transaction = null;
                    transaction = manager.beginTransaction();
                    transaction.hide(fileFragment);
                    transaction.show(photoFragment);
                    transaction.commit();
                    fragmentLabel = 2;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentLabel == 1) {
            fileFragment.goBack();
        }
    }
}
