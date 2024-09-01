package com.example.kuvajbrzo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userName, String someOtherData) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the LinearLayout where the fragments will be added
        LinearLayout recepieContainer = view.findViewById(R.id.recepie_container);

        // Get the passed userName from arguments
        String userName = getArguments().getString("USERNAME");

        // Create and add 10 RecipeFragments to the LinearLayout
        for (int i = 0; i < 10; i++) {
            // Create a new instance of RecipeFragment, passing the userName and some recipe-specific data
            RecepieFragment recipeFragment = RecepieFragment.newInstance(userName, "recipe_id_" + i);

            // Create a new FragmentTransaction
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            // Add the RecipeFragment to the LinearLayout
            fragmentTransaction.add(recepieContainer.getId(), recipeFragment);

            // Commit the transaction
            fragmentTransaction.commitNow(); // Use commitNow to add fragments immediately
        }

        return view;
    }
}
