package com.project_test.forsix;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class ShowCloudFilesActivity extends AppCompatActivity {
    private ListView cloudFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cloud_files);
        init();

        //TODO:retrofit请求框架，请求列表，显示；
    }

    private void init() {
        confirmConnectify();
        cloudFiles= (ListView) findViewById(R.id.cloud_files);

    }

    private void confirmConnectify() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected()) {
            Toast.makeText(ShowCloudFilesActivity.this, "友情提示:当前不处于Wifi环境", Toast.LENGTH_SHORT).show();
        }
    }
}
