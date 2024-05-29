package com.example.app1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface my_api {
    @GET("village-maps/") Call<List<Plot>> getVillage(@Query("village_name") String village_name);
}
