package com.example.kuvajbrzo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouriteRecepieFragment extends Fragment {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    private String mealId;
    private String userName;
    private TextView mealNameTextView;
    private ImageView mealImageView;
    private TextView countryTextView;
    private TextView categoryTextView;
    private Button showRecipeButton;

    public FavouriteRecepieFragment() {
        // Required empty public constructor
    }

    public static FavouriteRecepieFragment newInstance(String mealId, String userName) {
        FavouriteRecepieFragment fragment = new FavouriteRecepieFragment();
        Bundle args = new Bundle();
        args.putString("MEAL_ID", mealId);
        args.putString("USERNAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepie, container, false);

        mealNameTextView = view.findViewById(R.id.mealName);
        mealImageView = view.findViewById(R.id.mealImage);
        countryTextView = view.findViewById(R.id.mealCountry);
        categoryTextView = view.findViewById(R.id.mealCategory);
        showRecipeButton = view.findViewById(R.id.showRecipeButton);

        // Get the userName and mealId passed from ProfileFragment
        if (getArguments() != null) {
            userName = getArguments().getString("USERNAME");
            mealId = getArguments().getString("MEAL_ID");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MealApiService apiService = retrofit.create(MealApiService.class);

        // Use the lookup endpoint with mealId
        Call<MealResponse> call = apiService.getMealById(mealId);

        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Meal meal = response.body().getMeals().get(0);
                    mealNameTextView.setText(meal.getStrMeal());
                    countryTextView.setText(meal.getStrArea());
                    categoryTextView.setText(meal.getStrCategory());
                    Glide.with(getContext())
                            .load(meal.getStrMealThumb())
                            .into(mealImageView);
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                // Handle failure
            }
        });

        showRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecepieActivity.class);
            intent.putExtra("MEAL_ID", mealId);
            intent.putExtra("USERNAME", userName); // Pass the userName to the RecepieActivity
            startActivity(intent);
        });

        return view;
    }
}
