package com.project_test.forsix;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class FileUploadActivity extends AppCompatActivity {
    private TextView showtv;
    private ListView lv;
    private ArrayList<File> data = new ArrayList<>();
    private File[] files;
    private LocalFileAdapter fileAdapter;
    private Button backToCloud;
    private String rootpath;
    private Stack<String> nowPathStack;
    private String currentPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        currentPath = getIntent().getStringExtra("currentPath");
        if (currentPath.equals("")){
            currentPath=null;
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
        nowPathStack.push(rootpath);
        for (File f : files) {
            data.add(f);
        }
        showtv.setText(getPathString());
        fileAdapter = new LocalFileAdapter(this, data);
        lv.setAdapter(fileAdapter);
        lv.setOnItemClickListener(new FileItemClickListener());
    }

    private void initBackButton() {
        backToCloud = (Button) findViewById(R.id.goto_cloud_files);
        backToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        if (nowPathStack.peek() == rootpath) {
            super.onBackPressed();
        } else {
            nowPathStack.pop();
            showChangge(getPathString());
        }
    }
}

