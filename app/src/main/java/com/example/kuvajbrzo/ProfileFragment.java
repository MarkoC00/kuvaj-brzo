package com.example.kuvajbrzo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private TextView emailTextView;
    private LinearLayout recepieContainer;

    private FirebaseFirestore db;

    private String username;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String username, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        args.putString("PARAM2", param2); // if you need it
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        recepieContainer = view.findViewById(R.id.recepie_container);

        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(getActivity());
        db = FirebaseFirestore.getInstance();

        // Get the username from arguments
        if (getArguments() != null) {
            username = getArguments().getString("USERNAME");
        }

        // Fetch user information from Firestore
        fetchUserInfo();

        return view;
    }

    private void fetchUserInfo() {
        db.collection("user")
                .whereEqualTo("KorisnickoIme", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String email = document.getString("Email");
                            String korisnickoIme = document.getString("KorisnickoIme");

                            // Update the UI
                            usernameTextView.setText(korisnickoIme);
                            emailTextView.setText(email);

                            // Fetch and display favorite recipes
                            List<String> favoriteRecipes = (List<String>) document.get("OmiljeniRecepti");
                            if (favoriteRecipes != null) {
                                displayFavoriteRecipes(favoriteRecipes);
                            }
                        }
                    }
                });
    }

    private void displayFavoriteRecipes(List<String> favoriteRecipes) {
        for (String mealId : favoriteRecipes) {
            FavouriteRecepieFragment fragment = FavouriteRecepieFragment.newInstance(mealId, username);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.recepie_container, fragment)
                    .commit();
        }
    }
}
