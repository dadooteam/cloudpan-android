package com.project_test.forsix;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class UploadActivity extends AppCompatActivity {
    public static UploadActivity instance = null;
    private TextView showtv;
    private ListView lv;//本地文件列表
    private ListView showFilesToUpload;
    private ArrayList<File> data = new ArrayList<>();//本地文件列表的文件集合
    private File[] files;
    private LocalFileAdapter fileAdapter;
    private Button backToCloud;
    private Button pickPhoto;
    public FilesToUploadAdapter filesToUploadAdapter;
    private String rootpath;
    private Stack<String> nowPathStack;
    private String currentPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        instance = this;
        currentPath = getIntent().getStringExtra("currentPath");
        if (currentPath.equals("")) {
            currentPath = null;
        }
        initView();
    }

    private void initView() {
        initBackButton();
        rootpath = Environment.getExternalStorageDirectory().toString();
        nowPathStack = new Stack<>();
        lv = (ListView) findViewById(R.id.lv);
        showtv = (TextView) findViewById(R.id.showtv);
        files = Environment.getExternalStorageDirectory()
                .listFiles();
        showFilesToUpload = (ListView) findViewById(R.id.files_to_upload);
        nowPathStack.push(rootpath);
        for (File f : files) {
            data.add(f);
        }
        showtv.setText(getPathString());
        fileAdapter = new LocalFileAdapter(this, data);
        lv.setAdapter(fileAdapter);
        lv.setOnItemClickListener(new FileItemClickListener());
        filesToUploadAdapter = new FilesToUploadAdapter(this, UserInfo.getInstance().getFilesToUpload());
        showFilesToUpload.setAdapter(filesToUploadAdapter);

    }

    private void initBackButton() {
        backToCloud = (Button) findViewById(R.id.goto_cloud_files);
        backToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pickPhoto = (Button) findViewById(R.id.pick_photo);
        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String fileName = c.getString(columnIndex);
            ArrayList<String> tmp = UserInfo.getInstance().getFilesToUpload();
            if (tmp.size() <= 4) {
                if (!tmp.contains(fileName)) {
                    tmp.add(fileName);
                } else {
                    Toast.makeText(this, "文件已存在于上传列表中", Toast.LENGTH_SHORT).show();
                }
                if (UploadActivity.instance.filesToUploadAdapter != null) {
                    UploadActivity.instance.filesToUploadAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "上传队列文件数量过多，请立即开始上传", Toast.LENGTH_SHORT).show();
            }
            c.close();
        }
    }

    class FileItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(
                AdapterView<?> parent,
                View view,
                int position,
                long id) {

            File file = files[position];
            if (file.isFile()) {
            } else {
                nowPathStack.push("/" + file.getName());
                showChangge(getPathString());
            }
        }
    }

    private void showChangge(String path) {
        showtv.setText(path);
        files = new File(path).listFiles();
        data.clear();
        for (File f : files) {
            data.add(f);
        }
        files = fileAdapter.setfiledata(data);
    }

    private String getPathString() {
        Stack<String> temp = new Stack<>();
        temp.addAll(nowPathStack);
        String result = "";
        while (temp.size() != 0) {
            result = temp.pop() + result;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (nowPathStack.peek() != rootpath) {
            nowPathStack.pop();
            showChangge(getPathString());
        }
    }

    public FilesToUploadAdapter getFilesToUploadAdapter() {
        return filesToUploadAdapter;
    }
}

