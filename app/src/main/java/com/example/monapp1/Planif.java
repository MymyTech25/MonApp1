package com.example.monapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Planif extends AppCompatActivity {
    private FirebaseFirestore db;
    private Set<String> uniqueDatesSet = new HashSet<>();
    private LinearLayout linearLayout ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planif);

        db = FirebaseFirestore.getInstance();
        loadProductsFromFirebase();

        linearLayout = findViewById(R.id.linearlayout);

        //bouton bonhomme
        ImageButton btnBonhomme = findViewById(R.id.btnBonhomme2);
        // OnClickListener pour le bouton bonhomme
        btnBonhomme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonhommePopup(); // Méthode pour afficher le popup
            }
        });


    }

    private void loadProductsFromFirebase() {
        db.collection("livraison")
                .orderBy("date", Query.Direction.ASCENDING) // Tri par date
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CommandeDTO> commande = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommandeDTO comm = document.toObject(CommandeDTO.class);
                            comm.setId(document.getId());

                            commande.add(comm);
                           if (document.getLong("attribué") != null && document.getLong("attribué") != 1)
                             displayOrder(comm);

                        }


                        Log.d("PlanifAdapter", "taille: " + commande.size());

                    } else {
                    }
                });
    }

    private void displayOrder(CommandeDTO order) {
        // Create a TextView for each order
        String orderDate = formatDate(order.getDate());
        //String orderDate = formatDate(new Date(2024,01,10));
        if (!uniqueDatesSet.contains(orderDate)) {
            uniqueDatesSet.add(orderDate);
            TextView textView = new TextView(this);

            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(orderDate); // You can customize this to display specific details
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // You can adjust the size as needed

            linearLayout.addView(textView);
        }
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(order.toString()); // You can customize this to display specific details
        linearLayout.addView(textView);



        // Create a Spinner
        List<UserDTO> chauffeurs = new ArrayList<>();
        Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

            FirebaseFirestore.getInstance().collection("utilisateurs")
                    .whereEqualTo("role", 2) // Filtrez les utilisateurs ayant le rôle de chauffeur
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDTO utilisateur = document.toObject(UserDTO.class);
                                chauffeurs.add(utilisateur);
                            }
                            // Log pour vérifier la liste des chauffeurs
                            Log.d("Planificateur", "Chauffeurs récupérés : " + chauffeurs);
                            ArrayAdapter<UserDTO> adapter = new ArrayAdapter<>(Planif.this,
                                    android.R.layout.simple_spinner_item, chauffeurs);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
// Add the Spinner to the LinearLayout

                            // Mettez à jour votre adaptateur avec la liste des chauffeurs
                        } else {
                            Toast.makeText(Planif.this, "Erreur lors du chargement des chauffeurs", Toast.LENGTH_SHORT).show();
                        }
                    });



        // Set up the spinner adapter with Firestore data
      /*  FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("role", 2)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<User> userList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userId = document.getId();
                            String userName = document.getString("name");

                            User user = new User(userId, userName);
                            userList.add(user);
                        }

                        ArrayAdapter<User> adapter = new ArrayAdapter<>(PlanificateurPageActivity.this,
                                android.R.layout.simple_spinner_item, userList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to retrieve users
                    }
                });*/

// Add the Spinner to the LinearLayout
        linearLayout.addView(spinner);
        // Set up the item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item change here
                UserDTO selectedUser = chauffeurs.get(position);
                //String selectedUserId = selectedUser.getId();
                String selectedUserEmail = selectedUser.getEmail();
                //addOrUpdateDelivery(selectedUserEmail, orderDate, order.getPoint());
                // Do something with the selected user information
                // For example, log it or update your Order object
                //Log.d("Spinner", "Selected User ID: " + selectedUser);
                Log.d("Spinner", "Selected User Email: " + orderDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected
                Log.d("Spinner", "Nothing selected");
            }
        });

        // Create a Button dynamically
        Button myButton = new Button(this);
        myButton.setText("Envoyer"); // Set the text on the button

// Set LayoutParams to define the width and height of the button
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        myButton.setLayoutParams(params);

// Set an OnClickListener to handle button clicks
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click here
                // For example, you can show a toast message
                // or perform any other action
                UserDTO selectedUser = chauffeurs.get(spinner.getSelectedItemPosition());
                String selectedUserEmail = selectedUser.getEmail();
                DocumentReference documentRef = db.collection("livraison").document(order.getId());
                addOrUpdateDelivery(selectedUserEmail, orderDate, order.getPoint(), documentRef);


                // Add the button to your layout
                // order.setStatus("Attributed");
                Map<String, Object> updatedData = new HashMap<>();
               // updatedData.put("status", "Attributed"); // Update the status field
                updatedData.put("attribué", 1 );
                // Update the order in the Firestore database
              //  ordersCollection.document(order.getId())
                db.collection("livraison").document(order.getId())
                        .update(updatedData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Handle the success, e.g., show a toast or log a message
                                Log.d("Firestore", "Order status updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the failure, e.g., show an error message
                                Log.e("Firestore", "Error updating order status", e);
                            }
                        });
                Intent intent = getIntent();
                finish(); // Finish the current activity
                startActivity(intent);

            }
        });

        linearLayout.addView(myButton);
    }
    public void addOrUpdateDelivery(String email, String date, GeoPoint point, DocumentReference documentRef) {
        // Creation de la collection "livraisonChauffeur"
        CollectionReference deliveryCollection = db.collection("livraisonChauffeur");

        // Query to check if a document with the same driver and date exists
        deliveryCollection
                .whereEqualTo("driverEmail",email)
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Document with the same driver and date exists, update the points
                            String existingDocumentId = document.getId();
                            updatePoints(existingDocumentId, point, documentRef);
                            return; // Exit the method after updating points
                        }
                        // No document with the same driver and date, create a new document
                        createDelivery(email, date, point, documentRef);
                    } else {
                        // Handle errors
                        Log.e("Firestore", "Error checking for existing delivery", task.getException());
                    }
                });
    }
    private void updatePoints(String deliveryId, GeoPoint newPoint, DocumentReference documentRef) {
        Map<String, Object> deliveryData = new HashMap<>();
        deliveryData.put("points", FieldValue.arrayUnion(newPoint));
        deliveryData.put("commande", FieldValue.arrayUnion(documentRef));

        // Update the existing document by appending new points to the existing list
        db.collection("livraisonChauffeur").document(deliveryId)
                .update(deliveryData)
                .addOnSuccessListener(aVoid -> {
                    // Document updated successfully
                    Log.d("Firestore", "Points added to existing delivery");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore", "Error updating points", e);
                });
    }

    private void createDelivery(String email, String date,  GeoPoint point, DocumentReference documentRef ) {
        // Create a new document with the specified data
        Map<String, Object> deliveryData = new HashMap<>();
        deliveryData.put("driverEmail", email);
        deliveryData.put("date", date);
        deliveryData.put("status", "attribue");


        deliveryData.put("points", Collections.singletonList(point));
        deliveryData.put("commande", Collections.singletonList(documentRef));

        // Add the new document to the "livraisonChauffeur" collection
        db.collection("livraisonChauffeur")
                .add(deliveryData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Log.d("Firestore", "New delivery added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore", "Error adding new delivery", e);
                });
    }


    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);

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
        Intent intent = new Intent(Planif.this, MesInformations.class);
        startActivity(intent);
    }
    private void deconnexion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Planif.this, Connexion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Terminer l'activité actuelle
    }
        }