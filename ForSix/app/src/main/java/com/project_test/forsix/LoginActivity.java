package com.project_test.forsix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project_test.forsix.RetrofitBeans.LoginBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private EditText phoneNumberEdit;
    private EditText passwordEdit;
    private Button login;
    private String phoneNumber;
    private String password;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        phoneNumberEdit = (EditText) findViewById(R.id.phone_number);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=phoneNumberEdit.getText().toString();
                password=passwordEdit.getText().toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URLs.BASEURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitUtil loginRequest=retrofit.create(RetrofitUtil.class);
                Call call=loginRequest.getLoginRequest(phoneNumber,password);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()){
                            LoginBean loginInfo=(LoginBean) response.body();
                            token=loginInfo.getData().getToken();
                            UserInfo.getInstance().setToken(token);
                            SharedPreferences sp=getSharedPreferences("usrInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("token",token);
                            editor.putInt("downloadlabel",0);
                            editor.putInt("snackBarLabel",0);
                            editor.commit();
                            Intent intent=new Intent(LoginActivity.this,ShowCloudFilesActivity.class);
                            startActivity(intent);
                            Thread thread=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            });
                            thread.start();
                        }else {
                            Toast.makeText(LoginActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
