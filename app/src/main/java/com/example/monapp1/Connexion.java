package com.example.monapp1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

public class Connexion extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
        mAuth = FirebaseAuth.getInstance();


        // Initialisation des vues
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);

        // Bouton de connexion
        Button btnConnexion = findViewById(R.id.btnConnexion);
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Méthode appelée lors du clic sur le bouton "Se Connecter"
                seConnecter();
            }
        });

        // Bouton d'inscription
        Button btnInscription = findViewById(R.id.btnInscription);
        btnInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Méthode appelée lors du clic sur le bouton "Inscrivez-vous"
                Intent intent = new Intent(Connexion.this, Inscription.class);
                startActivity(intent);
            }
        });


    }

    //gestion du bouton de connexion
    private void seConnecter() {
        // Récupérer les informations saisies par l'utilisateur
        String email = editTextEmail.getText().toString();
        String motDePasse = editTextPassword.getText().toString();
        final boolean[] connexionReussie = {false};

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //CollectionReference utilisateurRef = db.collection("utilisateurs");

      //  Query query = utilisateurRef.whereEqualTo("email", email).whereEqualTo("motDePasse", motDePasse);

        /*query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                // Les informations de connexion sont correctes
                connexionReussie[0] = true;
            } else {
                // Les informations de connexion sont incorrectes
                connexionReussie[0] = false;
            }

            // Afficher un message Toast en fonction du résultat de la connexion
            if (connexionReussie[0]) {
                afficherToast("Connexion réussie");


            } else {
                afficherToast("Connexion échouée");
            }
        });*/

        mAuth.signInWithEmailAndPassword(email, motDePasse)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Connexion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Page planificateur
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference utilisateurRef = db.collection("utilisateurs");

            Query query = utilisateurRef.whereEqualTo("email",user.getEmail());

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    if (document.getLong("role") == 1) {
                        Intent intent = new Intent(Connexion.this, Planif.class);
                        startActivity(intent);

                    } else if(document.getLong("role") == 2) {
                        Intent intent = new Intent(Connexion.this, Chauffeur.class);
                        startActivity(intent);

                    } else {
                        // Rediriger vers la page des produits(client)
                        Intent intent = new Intent(Connexion.this, Produits.class);
                        startActivity(intent);
                    }


                } else {
                    // Les informations de connexion sont incorrectes
                    // connexionReussie[0] = false;
                }


        });
    }


}
    //affichage des toasts
    private void afficherToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}