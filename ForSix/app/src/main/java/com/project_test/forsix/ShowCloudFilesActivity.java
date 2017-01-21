package com.project_test.forsix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowCloudFilesActivity extends AppCompatActivity {
    private ListView cloudFiles;
    private Button back, create, upload, changeUser;
    private List<FileListBean.DataBean> files;
    private String token;
    private StringBuffer currentPath = new StringBuffer("");
    private CloudFileAdapter adapter;
    private CloudItemClickLister lister;
    private SharedPreferences sp;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cloud_files);
        sp = getSharedPreferences("usrInfo", MODE_PRIVATE);
        init();
    }

    private void init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        token = UserInfo.getInstance().getToken();
        initList();
        initButtons();
        confirmConnectify();
    }

    private void initButtons() {
        changeUser = (Button) findViewById(R.id.goto_login);
        back = (Button) findViewById(R.id.back);
        create = (Button) findViewById(R.id.create);
        upload = (Button) findViewById(R.id.goto_upload);

        upload.setOnClickListener(new View.OnClickListener() {//上传按钮
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCloudFilesActivity.this, UploadActivity.class);
                intent.putExtra("currentPath", currentPath.toString());
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
                //TODO:创建文件夹的逻辑
            }
        });
    }

    private class CloudItemClickLister implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO:等服务器给path
            String pathtmp1 = files.get(position).getPath();
            int index = 0;
            String tmp = pathtmp1;
            while (index != 3) {
                tmp = tmp.substring(1, tmp.length());
                if (tmp.charAt(0) == '\\') {
                    index++;
                }
            }
            tmp = tmp.substring(1, tmp.length());
            Log.e("onItemClick: ", tmp);

            RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
            Call call = fileListRequest.getFileListRequest(token, tmp);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        FileListBean fileListBean = (FileListBean) response.body();
                        if (fileListBean.getStatus() == 200) {
                            files.clear();
                            files.addAll(fileListBean.getData());
                            Log.e("onResponse: ", fileListBean.getData().get(1).getPath());
                            Log.e("onResponse: ", files.size() + "*****");
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


    }

    private void initList() {
        files = new ArrayList();
        cloudFiles = (ListView) findViewById(R.id.cloud_files);
        adapter = new CloudFileAdapter(this, files);
        cloudFiles.setAdapter(adapter);
        lister = new CloudItemClickLister();
        cloudFiles.setOnItemClickListener(lister);

        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
        Call call = fileListRequest.getFileListRequest(token, null);
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


}
