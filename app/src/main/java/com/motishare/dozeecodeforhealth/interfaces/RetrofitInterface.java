package com.motishare.dozeecodeforhealth.interfaces;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    //Authentication Apis

    @GET("user/data")
    Call<ResponseBody> userdata();

    @GET("user/details")
    Call<ResponseBody> userdetails();


}
