package com.example.kuvajbrzo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    @GET("lookup.php")
    Call<MealDetailResponse> getMealDetails(@Query("i") String mealId);

    @GET("lookup.php")
    Call<MealResponse> getMealById(@Query("i") String mealId);
}