package com.project_test.forsix;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 */
public class FileUploadFragment extends Fragment {
    private TextView showtv;//showtv是路径框
    private ListView lv;
    private ArrayList<File> data = new ArrayList<>();
    private File[] files;
    private LocalFileAdapter fileAdapter;
    private String rootpath;
    private Stack<String> nowPathStack;
    private String currentPath;

    public FileUploadFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        rootpath = Environment.getExternalStorageDirectory().toString();
        nowPathStack = new Stack<>();
        files = Environment.getExternalStorageDirectory()
                .listFiles();
        nowPathStack.push(rootpath);
        for (File f : files) {
            data.add(f);
        }
        fileAdapter = new LocalFileAdapter(getContext(), data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_file_upload, container, false);
        lv = (ListView) ret.findViewById(R.id.lv);
        lv.setAdapter(fileAdapter);
        showtv = (TextView) ret.findViewById(R.id.showtv);
        showtv.setText(getPathString());
        lv.setOnItemClickListener(new FileItemClickListener());
        return ret;
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

    public void goBack() {
        if (nowPathStack.peek() != rootpath) {
            nowPathStack.pop();
            showChangge(getPathString());
        }
    }

}
