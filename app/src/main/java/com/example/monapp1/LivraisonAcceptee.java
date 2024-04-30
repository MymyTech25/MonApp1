package com.example.monapp1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LivraisonAcceptee extends AppCompatActivity {

    private LinearLayout deliveryLayout;
    private FirebaseFirestore db;

    private Button btnRetourAccepted;
    private List<GeoPoint> geoPoints = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livraison_acceptee);

        deliveryLayout = findViewById(R.id.deliveryLayout);
        db = FirebaseFirestore.getInstance();

        loadAcceptedFromFirestore();


        btnRetourAccepted = findViewById(R.id.btnRetourAccepted);


        btnRetourAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });

    }

   /* private void loadAcceptedFromFirestore() {
        CollectionReference deliveriesRef = db.collection("livraisonChauffeur");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        deliveriesRef.whereEqualTo("driverEmail", user.getEmail() ).whereEqualTo("status","accepted").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        createAcceptedView(document);
                        Log.e("Firestore",document.getId());

                    }

                } else {

                }

            }
        });
    }*/

    private void loadAcceptedFromFirestore() {
        CollectionReference deliveriesRef = db.collection("livraisonChauffeur");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        deliveriesRef.whereEqualTo("driverEmail", user.getEmail()).whereEqualTo("status", "accepted").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Vérifiez également le statut avant de créer la vue
                                if ("accepted".equals(document.getString("status"))) {
                                    createAcceptedView(document);
                                    Log.e("Firestore", document.getId());
                                }
                            }
                        } else {
                            // Gérer les erreurs
                        }
                    }
                });
    }

    //historique
    private void moveDeliveryToHistory(DocumentSnapshot document) {
        // Obtenez une référence au document actuel
        DocumentReference deliveryReference = document.getReference();

        // Mettez à jour le statut de la livraison à "completed"
        deliveryReference.update("status", "completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Statut de la livraison mis à jour avec succès");
                        // Vous pouvez ajouter ici une logique supplémentaire si nécessaire
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Échec de la mise à jour du statut de la livraison", e);
                    }
                });
    }

    private void createAcceptedView(DocumentSnapshot document) {
        // Utilisez le layout inflator pour créer une vue de livraison depuis le layout de livraison
        View acceptedView = LayoutInflater.from(this).inflate(R.layout.delivery_accepted_item, deliveryLayout, false);

        // Récupérez les éléments de votre vue de livraison
        TextView dateTextView = acceptedView.findViewById(R.id.dateTextView1);
        TextView addressTextView = acceptedView.findViewById(R.id.addressTextView1);
        TextView productsTextView = acceptedView.findViewById(R.id.productsTextView1);
        Button todoButton = acceptedView.findViewById(R.id.todoButton);


        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeliveryToHistory(document);
                List<GeoPoint> listPoints = (List<GeoPoint>) document.get("points");
                List<String> geoPoints = new ArrayList<>();
                for (GeoPoint geoPoint : listPoints) {

                    String coordinates = geoPoint.getLatitude() + "," + geoPoint.getLongitude();
                    geoPoints.add(coordinates);
                    Log.d("Firestore", "coordinates" + coordinates);

                }



                // Populate your list with GeoPoint objects
                //geoPoints.add(new GeoPoint(49.41461,8.681495)); // San Francisco, CA geoPoints.add(new GeoPoint(34.0522, -118.2437)); // Los Angeles, CA
               // geoPoints.add(new GeoPoint(49.41943,8.686507));  // New York, NY
                //geoPoints.add(new GeoPoint(49.420318,8.687872));  // New York, NY
                // geoPoints.add(new GeoPoint(49.422318,8.707872));  // New York, NY
                // geoPoints.add(new GeoPoint(49.424318,8.787872));  // New York, NY
                // geoPoints.add(new GeoPoint(49.425318,8.887872));  // New York, NY

                Log.d("Firestore","taille" + listPoints.size());


                Intent intent = new Intent(LivraisonAcceptee.this, Itineraire.class);
                intent.putStringArrayListExtra("geoPoints", (ArrayList<String>) geoPoints);
               //intent.putParcelableArrayListExtra("geoPoints", (ArrayList<GeoPoint>) listPoints);

                startActivity(intent);
            }
            });




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
        deliveryLayout.addView(acceptedView);
}

}