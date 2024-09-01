package com.example.kuvajbrzo;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;

    private FirebaseFirestore db;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(getActivity());
        db = FirebaseFirestore.getInstance();

        // Find views
        usernameEditText = view.findViewById(R.id.username);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);
        signupButton = view.findViewById(R.id.signupButton);
        TextView loginLink = view.findViewById(R.id.loginLink);

        // Set click listener for signup button
        signupButton.setOnClickListener(v -> performSignup());

        // Set click listener for login link
        loginLink.setOnClickListener(v -> {
            ((LoginActivity) getActivity()).replaceFragment(new LoginFragment(), "LoginFragment");
        });

        return view;
    }

    private void performSignup() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (!isUsernameValid(username)) {
            showToast("Korisničko ime mora imati barem 5 slova");
            return;
        }

        if (!isEmailValid(email)) {
            showToast("Unesite važeći email");
            return;
        }

        if (!isPasswordValid(password)) {
            showToast("Lozinka mora imati barem 10 karaktera, jedno veliko slovo i jedan broj");
            return;
        }

        if (!isPasswordConfirmed(password, confirmPassword)) {
            showToast("Lozinke se ne poklapaju");
            return;
        }

        // If all validations pass, add user to Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("KorisnickoIme", username);
        user.put("Lozinka", password);
        user.put("OmiljeniRecepti", Arrays.asList()); // Empty list

        // Add a new document with a generated ID
        db.collection("user")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SignupFragment", "DocumentSnapshot added with ID: " + documentReference.getId());
                    showToast("Registracija uspešna");
                    // Navigate to LoginFragment
                    ((LoginActivity) getActivity()).replaceFragment(new LoginFragment(), "LoginFragment");
                })
                .addOnFailureListener(e -> {
                    Log.w("SignupFragment", "Error adding document", e);
                    showToast("Greška prilikom registracije");
                });
    }

    private boolean isUsernameValid(String username) {
        return !username.isEmpty() && username.length() >= 5;
    }

    private boolean isEmailValid(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 10 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    private boolean isPasswordConfirmed(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
