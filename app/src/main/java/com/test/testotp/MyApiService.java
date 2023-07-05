package com.test.testotp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyApiService {
    @FormUrlEncoded
    @POST("getvehileotp.php")
    Call<ApiResponse> makeApiCall(@Field("method") String method, @Field("otp") String otp);
}