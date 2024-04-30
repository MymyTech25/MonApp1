package com.example.monapp1;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class Inscription extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button btnRetourConnexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Spinner spinnerRole = findViewById(R.id.spinner);
        String[] roles = {"Client", "Planificateur", "Chauffeur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        btnRetourConnexion = findViewById(R.id.btnRetourConnexion);

        Button btnInscriptionPage = findViewById(R.id.btnInscriptionPage);
        btnInscriptionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
                EditText editTextPhone = findViewById(R.id.editTextPhone2);
                Spinner spinner = findViewById(R.id.spinner);
                EditText editTextImmatriculation = findViewById(R.id.editTextText2);
                EditText editTextPassword = findViewById(R.id.editTextTextPassword2);



                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();
                String role = (spinner.getSelectedItem().toString());
                String immatriculation = editTextImmatriculation.getText().toString();
                String password = editTextPassword.getText().toString();

                // Vérifier si les champs ne sont pas vides
                if (!email.isEmpty() && !phone.isEmpty() && !password.isEmpty()) {
                    // Vérifier le rôle pour déterminer si l'immatriculation est nécessaire
                    if (role.equals("Chauffeur")) {
                        // Si le rôle est "Chauffeur", vérifier si l'immatriculation est vide
                        if (!immatriculation.isEmpty()) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(Inscription.this, new OnCompleteListener<AuthResult>() {

                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                addUserToFirestore(email, Integer.valueOf(phone), 2, immatriculation);
                                                updateUI(user);
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(Inscription.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                                updateUI(null);
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(Inscription.this, "Veuillez entrer l'immatriculation pour le rôle Chauffeur", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Si le rôle n'est pas "Chauffeur", ajouter l'utilisateur sans vérifier l'immatriculation
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Inscription.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Integer roleInt = (role.equals("Client"))?3:1;
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            addUserToFirestore(email, Integer.valueOf(phone), roleInt,immatriculation);
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Inscription.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                           updateUI(null);
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(Inscription.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //bouton retour
        btnRetourConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }

    private void addUserToFirestore(String email, Integer phone, Integer role, String immatriculation) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("email", email);
        newUser.put("numeroPortable", phone);
        newUser.put("role", role);
        newUser.put("numeroImm", immatriculation);

        db.collection("utilisateurs").add(newUser)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(Inscription.this, "Utilisateur enregistré", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Log.e("Myl Delivery", "Erreur : " + e.toString()));
    }
}
