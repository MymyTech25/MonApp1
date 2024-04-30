package com.example.monapp1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MesInformations extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;
    private Button btnModifierInformations;
    private Button btnModifierPhone;
    private Button btnModifierMdp;
    private Button btnRetourInfo;




    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_informations);
        // Initialiser Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextTextEmailAddressInfo);
        editTextPassword = findViewById(R.id.editTextTextPasswordInfo);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneInfo);
        btnModifierInformations = findViewById(R.id.btnModifEmail);
        btnModifierPhone = findViewById(R.id.btnModifPhone);
        btnModifierMdp = findViewById(R.id.btnModifMdp);
        btnRetourInfo = findViewById(R.id.btnRetourInfo);


        // Récupérer l'utilisateur actuellement connecté
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference utilisateurRef = db.collection("utilisateurs");

        Query query = utilisateurRef.whereEqualTo("email",user.getEmail());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                editTextEmail.setText(document.getString("email"));
                editTextPhoneNumber.setText(String.valueOf(document.getLong("numeroPortable")));

            } else {
                // Les informations de connexion sont incorrectes
               // connexionReussie[0] = false;
            }

            // Afficher un message Toast en fonction du résultat de la connexion
           /* if (connexionReussie[0]) {
                afficherToast("Connexion réussie");


            } else {
                afficherToast("Connexion échouée");
            }*/
        });

        // Vérifier si l'utilisateur est connecté
       if (user != null) {
            // Remplir les champs d'édition avec les informations de l'utilisateur
           // editTextEmail.setText(user.getEmail());
            editTextPassword.setText("");

       }

        //logique pour mettre à jour l'email
        btnModifierInformations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appeler la méthode de mise à jour des informations ici
                updateUserInfo();
            }
        });

        //logique pour mettre à jour le numéro de tel
        btnModifierPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appeler la méthode de mise à jour des informations ici
                updateUserPhone();
            }
        });

        //logique pour mettre à jour le mdp
        btnModifierMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Appeler la méthode de mise à jour des informations ici
                updateUserMdp();
            }
        });
        btnRetourInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });



    }



    private void updateUserInfo() {
        // Récupérer l'utilisateur actuellement connecté
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user.verifyBeforeUpdateEmail(editTextEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("TAG", "Email verification sent");
                            Toast.makeText(MesInformations.this, "Email de verification envoyé", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Error updating email address", task.getException());
                            Toast.makeText(MesInformations.this, "Erreur de la modification", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

       /* user.updateEmail(editTextEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("TAG", "Email verification sent");
                            Toast.makeText(MesInformations.this, "Email de verification envoyé", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Error updating email address", task.getException());
                            Toast.makeText(MesInformations.this, "Erreur de la modification", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

    }

    private void afficherToast(String message) {
        Toast.makeText(MesInformations.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserPhone() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference utilisateurRef = db.collection("utilisateurs");

        Query query = utilisateurRef.whereEqualTo("email", user.getEmail());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                // Mettre à jour le numéro de téléphone dans le document Firestore
                document.getReference().update("numeroPortable", Long.parseLong(editTextPhoneNumber.getText().toString()))
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Log.d(TAG, "Phone number updated successfully");
                                Toast.makeText(MesInformations.this, "Numéro de téléphone modifié avec succès", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.e(TAG, "Failed to update phone number", updateTask.getException());
                                Toast.makeText(MesInformations.this, "Échec de la modification du numéro de téléphone", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                //erreurs
            }
        });
    }


    private void updateUserMdp() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.updatePassword(editTextPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Password updated successfully");
                        Toast.makeText(MesInformations.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Password update failed", task.getException());
                        Toast.makeText(MesInformations.this, "Failed to change password", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    }

