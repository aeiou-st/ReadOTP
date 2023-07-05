package com.test.testotp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApiService {
    @GET("getvehileotp.php")
    Call<Void> makeApiCall(@Query("otp") String otp);
}