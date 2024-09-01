package com.example.kuvajbrzo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecepieActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private String mealId;
    private String userName;
    private TextView mealNameTextView;
    private ImageView mealImageView;
    private TextView instructionsTextView;
    private TextView ingredientsTextView;
    private ImageButton backButton;
    private ImageButton favouriteButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie);

        userName = getIntent().getStringExtra("USERNAME");

        mealNameTextView = findViewById(R.id.mealNameTextView);
        mealImageView = findViewById(R.id.mealImageView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        backButton = findViewById(R.id.backButton);
        favouriteButton = findViewById(R.id.favouriteButton);

        Intent intent = getIntent();
        mealId = intent.getStringExtra("MEAL_ID");

        db = FirebaseFirestore.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MealApiService apiService = retrofit.create(MealApiService.class);
        Call<MealDetailResponse> call = apiService.getMealDetails(mealId);

        call.enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MealDetail meal = response.body().getMeals().get(0);

                    mealNameTextView.setText(meal.getStrMeal());
                    Glide.with(RecepieActivity.this)
                            .load(meal.getStrMealThumb())
                            .into(mealImageView);
                    instructionsTextView.setText(meal.getStrInstructions());

                    StringBuilder ingredientsBuilder = new StringBuilder();
                    for (int i = 1; i <= 20; i++) {
                        try {
                            String ingredient = (String) MealDetail.class.getMethod("getStrIngredient" + i).invoke(meal);
                            String measure = (String) MealDetail.class.getMethod("getStrMeasure" + i).invoke(meal);
                            if (ingredient != null && !ingredient.isEmpty()) {
                                ingredientsBuilder.append(ingredient).append(" (").append(measure).append(")\n");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ingredientsTextView.setText(ingredientsBuilder.toString());
                }
            }

            @Override
            public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                // Handle failure
            }
        });

        // Set up the back button to finish the activity
        backButton.setOnClickListener(v -> onBackPressed());

        // Handle favourite button click
        favouriteButton.setOnClickListener(v -> addMealToFavorites());
    }

    private void addMealToFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the Firestore collection where KorisnickoIme matches the passed userName
        db.collection("user")
                .whereEqualTo("KorisnickoIme", userName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming there's only one user document with this username
                        DocumentReference userRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        userRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<String> favoriteRecipes = (List<String>) documentSnapshot.get("OmiljeniRecepti");
                                if (favoriteRecipes == null) {
                                    favoriteRecipes = new ArrayList<>();
                                }

                                if (!favoriteRecipes.contains(mealId)) {
                                    favoriteRecipes.add(mealId);
                                    userRef.update("OmiljeniRecepti", favoriteRecipes)
                                            .addOnSuccessListener(aVoid -> {
                                                // Show toast message
                                                Toast.makeText(RecepieActivity.this, "Meal added to favourites", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle error
                                                Toast.makeText(RecepieActivity.this, "Failed to add to favourites", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Meal is already in favorites
                                    Toast.makeText(RecepieActivity.this, "Meal already in favourites", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(e -> {
                            // Handle error
                            Toast.makeText(RecepieActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // No user found with the given username
                        Toast.makeText(RecepieActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(RecepieActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                });
    }
}
