package com.example.app1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface my_api {
    @GET("b/C08M") Call<List<Plot>> getVillage();
}
