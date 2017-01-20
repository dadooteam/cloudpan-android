package com.project_test.forsix;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment2 extends Fragment {
    private Button enter;
    private String token;
    private int direction;
    private Intent intent1;
    private Intent intent2;

    public SplashFragment2() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_splash_fragment2, container, false);
        enter = (Button) ret.findViewById(R.id.enter);
        SharedPreferences sp = getActivity().getSharedPreferences("usrInfo", MODE_PRIVATE);
        token = sp.getString("token", "0");
        UserInfo.getInstance().setToken(token);
        intent1 = new Intent(getActivity(), ShowCloudFilesActivity.class);
        intent2 = new Intent(getActivity(), LoginActivity.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
        Call call = fileListRequest.getFileListRequest(token);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    FileListBean fileListBean = (FileListBean) response.body();
                    if (fileListBean.getStatus() == 200) {
                        direction=1;
                    }
                } else {
                    direction=2;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().finish();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (direction==1){
                startActivity(intent1);}else {
                    startActivity(intent2);
                }
                thread.start();
            }
        });
        return ret;
    }
}
