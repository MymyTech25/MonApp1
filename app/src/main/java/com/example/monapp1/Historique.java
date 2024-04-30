package com.example.monapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class Historique extends AppCompatActivity {

    private LinearLayout historiqueLayout;
    private FirebaseFirestore db;
    private Button btnRetourHistorique;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        historiqueLayout = findViewById(R.id.historiqueLayout);
        btnRetourHistorique = findViewById(R.id.btnRetourHistorique);

        db = FirebaseFirestore.getInstance();


        // Charger les livraisons depuis Firestore
        loadDeliveriesFromFirestore();

        //bouton retour
        btnRetourHistorique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });

    }

    private void loadDeliveriesFromFirestore() {
        CollectionReference deliveriesRef = db.collection("livraisonChauffeur");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Chargez les livraisons avec le statut "completed"
        deliveriesRef.whereEqualTo("driverEmail", user.getEmail()).whereEqualTo("status", "completed").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Créez la vue pour chaque livraison terminée
                                createHistoriqueView(document);
                                Log.e("Firestore", document.getId());
                            }
                        } else {
                            // Gérer les erreurs
                        }
                    }
                });
    }

    private void createHistoriqueView(DocumentSnapshot document) {
        // Utilisez le layout inflator pour créer une vue d'historique depuis le layout d'historique
        View historiqueView = LayoutInflater.from(this).inflate(R.layout.item_historique, historiqueLayout, false);

        // Récupérez les éléments de votre vue d'historique
        TextView dateTextView = historiqueView.findViewById(R.id.dateTextView1);
        TextView addressTextView = historiqueView.findViewById(R.id.addressTextView1);
        TextView productsTextView = historiqueView.findViewById(R.id.productsTextView1);

        // Récupérez les données du document Firestore
        String date = document.getString("date");
        List<String> points = (List<String>) document.get("points");

        // Mettez à jour les éléments de la vue d'historique avec les données du document
        dateTextView.setText("Date: " + date);
        addressTextView.setText("Adresse: " + points);
        productsTextView.setText("Produits: " + document.get("productsList"));

        // Ajoutez la vue d'historique au layout principal
        historiqueLayout.addView(historiqueView);
    }
}
