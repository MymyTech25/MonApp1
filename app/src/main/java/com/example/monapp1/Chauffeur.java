package com.example.monapp1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class Chauffeur extends AppCompatActivity {

    private LinearLayout deliveryLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chauffeur_home_page);

        deliveryLayout = findViewById(R.id.deliveryLayout);
        db = FirebaseFirestore.getInstance();

        // Appel de la fonction pour charger les livraisons
        loadDeliveriesFromFirestore();

        // Ajout des boutons de navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            return true;
        });

        //bouton bonhomme
        ImageButton btnBonhomme = findViewById(R.id.btnBonhomme3);
        // OnClickListener pour le bouton bonhomme
        btnBonhomme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonhommePopup(); // Méthode pour afficher le popup
            }
        });

        // Ajoutez le code pour le bouton panier ici
        ImageButton btnAccepted = findViewById(R.id.btnAccepted);
        btnAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrir l'activité du panier
                Intent intent = new Intent(Chauffeur.this, LivraisonAcceptee.class);

                startActivity(intent);
            }
        });
        // Ajoutez le code pour le bouton panier ici
        ImageButton btnHistorique = findViewById(R.id.btnHistorique);
        btnHistorique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chauffeur.this, Historique.class);
                startActivity(intent);
            }
        });

    }

    private void loadDeliveriesFromFirestore() {
        CollectionReference deliveriesRef = db.collection("livraisonChauffeur");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        deliveriesRef.whereEqualTo("driverEmail", user.getEmail() ).whereEqualTo("status","attribue").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        createDeliveryView(document);
                        Log.e("Firestore",document.getId());

                    }

                } else {

                }

            }
        });
    }

    private void createDeliveryView(DocumentSnapshot document) {
        // Utilisez le layout inflator pour créer une vue de livraison depuis le layout de livraison
        View deliveryView = LayoutInflater.from(this).inflate(R.layout.delivery_item, deliveryLayout, false);

        // Récupérez les éléments de votre vue de livraison
        TextView dateTextView = deliveryView.findViewById(R.id.dateTextView1);
        TextView addressTextView = deliveryView.findViewById(R.id.addressTextView1);
        TextView productsTextView = deliveryView.findViewById(R.id.productsTextView1);
        Button acceptButton = deliveryView.findViewById(R.id.todoButton);
        Button rejectButton = deliveryView.findViewById(R.id.rejectButton);

        //acceptation de la commande par le chauffeur
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = db.collection("livraisonChauffeur").document(document.getId());
                documentReference.update("status", "accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully updated
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        // Add any additional logic or user feedback as needed
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle errors during the update process
                                Log.w(TAG, "Error updating document", e);
                                // Add any additional error handling or user feedback as needed
                            }
                        });
                Intent intent = getIntent();
                finish();
                startActivity(intent);

        }});


        //refus de la comande par le chauffeur
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = db.collection("livraisonChauffeur").document(document.getId());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Log.d("Firestore", documentSnapshot.getId());

                                if (documentSnapshot.exists()) {
                                    List<DocumentReference> commandesReferences = (List<DocumentReference>) documentSnapshot.get("commande");

                                    if (commandesReferences != null) {
                                        // Update the specific field in each referenced document
                                        for (DocumentReference commandesReference : commandesReferences) {
                                            commandesReference.update("attribué", 0)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            documentReference.delete()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            Log.d("Firestore", "This document has been delete");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.e("Firestore", "Error deleting failed", e);
                                                                        }
                                                                    });

                                                            Log.d("Firestore", "Field updated in referenced document");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("Firestore", "Error updating field in referenced document", e);
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    Log.d("Firestore", "Delivery document does not exist");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Error getting delivery document", e);
                            }
                        });

                Intent intent = getIntent();
                finish();
                startActivity(intent);


            }});


        // Récupérez les données du document Firestore
        String date = document.getString("date");
        List<String> points = (List<String>) document.get("points");
       // List<String> commande = (List<String>) document.get("commande");
        List<DocumentReference> referenceList = (List<DocumentReference>) document.get("commande");

        // Mettez à jour les éléments de la vue de livraison avec les données du document
        dateTextView.setText("Date: " + date);
        addressTextView.setText("Addresse: " + points);
        if (referenceList != null) {
            // Iterate through the list of references
            for (DocumentReference reference : referenceList) {
                // Fetch data from the referenced collection
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot referencedDocumentSnapshot) {
                        if (referencedDocumentSnapshot.exists()) {
                            // Access the data in the referenced document
                            Map<String, Object> data = referencedDocumentSnapshot.getData();

                            if (data != null) {
                                // Process the data
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    Log.d("Firestore", "Field: " + entry.getKey() + ", Value: " + entry.getValue());
                                }
                                productsTextView.setText("Produits: " + data.get("productsList") );
                            }
                        } else {
                            Log.d("Firestore", "Referenced document does not exist");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting referenced document", e);
                    }
                });
            }
        }



        // Ajoutez la vue de livraison au layout principal
        deliveryLayout.addView(deliveryView);
    }

    // Méthode pour afficher mon popup
    private void showBonhommePopup() {
        // boîte de dialogue (AlertDialog) personnalisée
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Que souhaitez-vous ? ");

        // Options du popup
        String[] options = {"Consulter mes informations", "Déconnexion"};

        // boutons du popup
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Gestion des clics sur les boutons du popup
                switch (which) {
                    case 0:
                        // Mes Informations
                        showMesInformations();
                        break;
                    case 1:
                        // Déconnexion
                        deconnexion(); // Méthode pour gérer la déconnexion
                        break;
                }
            }
        });

        // Afficher mon popup
        builder.show();
    }

    //méthodes pour que l'user ait accès ses infos
    private void showMesInformations() {
        Intent intent = new Intent(Chauffeur.this, MesInformations.class);
        startActivity(intent);
    }
    private void deconnexion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Chauffeur.this, Connexion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Terminer l'activité actuelle
    }
}
