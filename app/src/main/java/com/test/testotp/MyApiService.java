package com.test.testotp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApiService {
    @FormUrlEncoded
    @POST("getvehileotp.php")
    Call<ApiResponse> makeApiCall(@Field("method") String method, @Field("otp") String otp);
}