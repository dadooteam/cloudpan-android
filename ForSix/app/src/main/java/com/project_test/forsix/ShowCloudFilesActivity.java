package com.project_test.forsix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.RetrofitBeans.MkDirBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowCloudFilesActivity extends AppCompatActivity {
    private ListView cloudFiles;
    private Button create, upload, changeUser;
    private List<FileListBean.DataBean> files;
    private String token;
    private String currentPath = null;
    private CloudFileAdapter adapter;
    private CloudItemClickLister lister;
    private SharedPreferences sp;
    private Retrofit retrofit;
    private AlertDialog mydialog;
    private EditText newfolder_name;
    private int currentDirLevel = 0;//back有没有效果,为0时在根目录，无上一级
    private ArrayList<String> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cloud_files);
        sp = getSharedPreferences("usrInfo", MODE_PRIVATE);
        init();
    }

    private void init() {
        makeRootDir();
        paths = new ArrayList<>();
        paths.add(currentPath);
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        token = UserInfo.getInstance().getToken();
        initList();
        initButtons();
        confirmConnectify();
    }

    private void makeRootDir() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            String path = "/sdcard/CloudPan/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private void initButtons() {
        changeUser = (Button) findViewById(R.id.goto_login);
        create = (Button) findViewById(R.id.create);
        upload = (Button) findViewById(R.id.goto_upload);

        upload.setOnClickListener(new View.OnClickListener() {//上传按钮
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCloudFilesActivity.this, UploadActivity.class);
                if (currentPath == null) {
                    intent.putExtra("currentPath", "");
                } else {
                    intent.putExtra("currentPath", currentPath);
                }
                startActivity(intent);
            }
        });

        changeUser.setOnClickListener(new View.OnClickListener() {//更换账户按钮
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("token", "0");
                editor.commit();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
                Intent intent = new Intent(ShowCloudFilesActivity.this, LoginActivity.class);
                startActivity(intent);
                thread.start();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {//创建文件夹按钮
            @Override
            public void onClick(View v) {
                createNewFolder();
            }
        });
    }

    private void goBack() {
        currentPath = paths.get(paths.size() - 2);
        paths.remove(paths.get(paths.size() - 1));
        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
        Call call2 = fileListRequest.getFileListRequest(token, currentPath);
        call2.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    FileListBean fileListBean = (FileListBean) response.body();
                    if (fileListBean.getStatus() == 200) {
                        files.clear();
                        files.addAll(fileListBean.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewFolder() {
        mydialog = new AlertDialog.Builder(ShowCloudFilesActivity.this).create();
        mydialog.show();
        mydialog.getWindow().setContentView(R.layout.newfolder_dialog);
        mydialog.setView(new EditText(ShowCloudFilesActivity.this));
        mydialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        newfolder_name = (EditText) mydialog.getWindow().findViewById(R.id.newfolder_name);

        mydialog.getWindow()
                .findViewById(R.id.newfolder_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mydialog.dismiss();
                    }
                });

        mydialog.getWindow().findViewById(R.id.newfolder_create)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newfolder_name.getText().toString() == null) {
                            Toast.makeText(ShowCloudFilesActivity.this, "文件名不能为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            String name = newfolder_name.getText().toString();
                            if (name.length() == 0) {
                                Toast.makeText(ShowCloudFilesActivity.this, "文件名不能为空！", Toast.LENGTH_SHORT).show();
                            } else {
                                boolean legal = true;
                                for (int i = 0; i < name.length(); i++) {
                                    legal = (legal) && (name.charAt(i) != '/');
                                }
                                if (!legal) {
                                    Toast.makeText(ShowCloudFilesActivity.this, "文件夹名不能包含“/”", Toast.LENGTH_SHORT).show();
                                } else {
                                    boolean repeat = true;
                                    for (int i = 0; i < files.size(); i++) {
                                        repeat = (repeat) && (!(files.get(i).getName().equals(name)));
                                    }
                                    if (!repeat) {
                                        Toast.makeText(ShowCloudFilesActivity.this, "此文件夹已存在", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mydialog.dismiss();
                                        RetrofitUtil mkDirRequest = retrofit.create(RetrofitUtil.class);
                                        Call call = mkDirRequest.getMkDirRequest(token, currentPath, name);
                                        call.enqueue(new Callback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                if (response.isSuccessful()) {
                                                    MkDirBean mkdirBean = (MkDirBean) response.body();
                                                    if (mkdirBean.getStatus() == 200) {
                                                        Toast.makeText(ShowCloudFilesActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                                        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
                                                        Call call1 = fileListRequest.getFileListRequest(token, currentPath);
                                                        call1.enqueue(new Callback() {
                                                            @Override
                                                            public void onResponse(Call call, Response response) {
                                                                if (response.isSuccessful()) {
                                                                    FileListBean fileListBean = (FileListBean) response.body();
                                                                    if (fileListBean.getStatus() == 200) {
                                                                        files.clear();
                                                                        files.addAll(fileListBean.getData());
                                                                        adapter.notifyDataSetChanged();
                                                                    } else {
                                                                        Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call call, Throwable t) {
                                                                Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(ShowCloudFilesActivity.this, "创建失败，请重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(ShowCloudFilesActivity.this, "创建失败，请重试", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call call, Throwable t) {
                                                Toast.makeText(ShowCloudFilesActivity.this, "创建失败，请重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private class CloudItemClickLister implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (files.get(position).getType() == 2) {//2是文件夹
                currentDirLevel++;
                currentPath = files.get(position).getPath();
                paths.add(currentPath);
                RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
                Call call = fileListRequest.getFileListRequest(token, currentPath);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            FileListBean fileListBean = (FileListBean) response.body();
                            if (fileListBean.getStatus() == 200) {
                                files.clear();
                                files.addAll(fileListBean.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void initList() {
        files = new ArrayList();
        cloudFiles = (ListView) findViewById(R.id.cloud_files);
        adapter = new CloudFileAdapter(this, files);
        cloudFiles.setAdapter(adapter);
        lister = new CloudItemClickLister();
        cloudFiles.setOnItemClickListener(lister);
        initListData();
    }

    private void initListData() {
        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
        Call call = fileListRequest.getFileListRequest(token, currentPath);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    FileListBean fileListBean = (FileListBean) response.body();
                    if (fileListBean.getStatus() == 200) {
                        files.addAll(fileListBean.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShowCloudFilesActivity.this, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    private void confirmConnectify() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected()) {
            Toast.makeText(ShowCloudFilesActivity.this, "当前不处于Wifi环境", Toast.LENGTH_SHORT).show();
        }
    }

    long lastBackPressed = 0;

    @Override
    public void onBackPressed() {
        if (currentDirLevel > 0) {
            goBack();
            currentDirLevel--;
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBackPressed < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
            lastBackPressed = currentTime;
        }
    }
}
