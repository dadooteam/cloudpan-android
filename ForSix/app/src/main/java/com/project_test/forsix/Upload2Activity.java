package com.project_test.forsix;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;
import com.project_test.forsix.UploadRelated.UploadBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Upload2Activity extends AppCompatActivity {
    public static Upload2Activity instance = null;
    private TextView showtv;
    private ListView lv;//本地文件列表
    private ListView showFilesToUpload;
    private ArrayList<File> data = new ArrayList<>();//本地文件列表的文件集合
    private File[] files;
    private LocalFileAdapter fileAdapter;
    private Button backToCloud;
    private Button pickPhoto;
    //    private Button upload;
    public UploadAdapter uploadAdapter;
    private String rootpath;
    private Stack<String> nowPathStack;
    private String currentPath;
    private Retrofit retrofit;
    private RelativeLayout frame;
    private int snackBarLabel;

    public String getCurrentPath() {
        return currentPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);
        instance = this;
        SharedPreferences sp = getSharedPreferences("usrInfo", MODE_PRIVATE);
        snackBarLabel = sp.getInt("snackBarLabel", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("snackBarLabel", 1);
        editor.commit();
        frame = (RelativeLayout) findViewById(R.id.centeral_local_file);
        if (snackBarLabel == 0) {
            Snackbar.make(frame, "右滑显示上传列表", Snackbar.LENGTH_SHORT)
                    .setAction("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
        lv.setOnItemClickListener(new Upload2Activity.FileItemClickListener());
        uploadAdapter = new UploadAdapter(this, UserInfo.getInstance().getFilesToUpload());
        showFilesToUpload.setAdapter(uploadAdapter);

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
            ArrayList<UploadBean> tmp = UserInfo.getInstance().getFilesToUpload();
            boolean addAvailable = true;//true是可以添加
            for (UploadBean bean : tmp) {
                if (bean.getFileName().equals(fileName)) {
                    addAvailable = false;
                }
            }
            if (addAvailable) {
                Intent intent = new Intent(Upload2Activity.this, UploadService.class);
                checkRepeatFiles(tmp, fileName, intent);
            } else {
                Toast.makeText(this, "文件已存在于上传列表中", Toast.LENGTH_SHORT).show();
            }
            if (uploadAdapter != null) {
                uploadAdapter.notifyDataSetChanged();
            }
            c.close();
        }
    }

    private void checkRepeatFiles(final ArrayList<UploadBean> tmp, final String fileName, final Intent intent) {
        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
        Call call = fileListRequest.getFileListRequest(UserInfo.getInstance().getToken(), currentPath);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    FileListBean fileListBean = (FileListBean) response.body();
                    if (fileListBean.getStatus() == 200) {
                        List<FileListBean.DataBean> data = fileListBean.getData();
                        List<String> filesOnline = new ArrayList<String>();
                        File file = new File(fileName);
                        String fileNameTmp = file.getName();
                        for (FileListBean.DataBean beantmp : data) {
                            filesOnline.add(beantmp.getName());
                        }
                        if (filesOnline.contains(fileNameTmp)) {
                            Toast.makeText(Upload2Activity.this, fileNameTmp + "已存在，不能再次上传", Toast.LENGTH_LONG).show();
                        } else {
                            tmp.add(new UploadBean(0, 0, fileName));
                            Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                            startService(intent);
                        }

                    } else {
                        Toast.makeText(Upload2Activity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Upload2Activity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(Upload2Activity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
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
        if (nowPathStack.peek() != rootpath) {
            nowPathStack.pop();
            showChangge(getPathString());
        } else {
            super.onBackPressed();
        }
    }
}
