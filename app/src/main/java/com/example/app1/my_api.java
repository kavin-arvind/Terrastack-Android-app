package com.example.app1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface my_api {
    @GET("b/PU1U") Call<List<Plot>> getVillage();
}
