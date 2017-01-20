package com.project_test.forsix.Retrofits;

import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.RetrofitBeans.LoginBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kun on 2017/1/20.
 */

public interface RetrofitUtil {
    @POST("login")
    Call<LoginBean> getLoginRequest(
            @Query("phone") String phoneNumber, @Query("password") String password);

    @GET("list")
    Call<FileListBean> getFileListRequest(
            @Header("Authorization") String token);


}
