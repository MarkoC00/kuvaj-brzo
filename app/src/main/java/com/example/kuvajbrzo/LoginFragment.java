package com.example.kuvajbrzo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    private FirebaseFirestore db;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(getActivity());
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.loginButton);
        registerLink = view.findViewById(R.id.registerLink);

        loginButton.setOnClickListener(v -> validateAndLogin());

        registerLink.setOnClickListener(v -> {
            ((LoginActivity) getActivity()).replaceFragment(new SignupFragment(), "SignupFragment");
        });

        return view;
    }

    private void validateAndLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || username.length() < 5) {
            Toast.makeText(getActivity(), "Korisničko ime mora imati najmanje 5 karaktera", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            Toast.makeText(getActivity(), "Lozinka zahteva 10 karaktera, veliko slovo i jedan broj", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform login check
        performLogin(username, password);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 10 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*");
    }

    private void performLogin(String username, String password) {
        // Query Firestore to check if user exists and password is correct
        db.collection("user")
                .whereEqualTo("KorisnickoIme", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // User found
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String storedPassword = document.getString("Lozinka");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                // Password matches
                                Toast.makeText(getActivity(), "Uspešna prijava", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("USER_NAME", username);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                // Incorrect password
                                Toast.makeText(getActivity(), "Lozinka nije tačna", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // User not found
                            Toast.makeText(getActivity(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Task failed
                        Toast.makeText(getActivity(), "Greška proveri kreditencijale", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
