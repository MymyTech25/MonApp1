package com.example.monapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Planificateur extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<CommandeDTO> commande ;
    private RecyclerView recyclerViewPlanif;
    private PlanifAdapter planifAdapter;
    private List<CommandeDTO> commandeList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificateur);

        db = FirebaseFirestore.getInstance();
        loadProductsFromFirebase();
        loadChauffeursFromFirebase();
        commande = new ArrayList<>();
        commandeList = new ArrayList<>();

        //Initialiser la RecyclerView
        recyclerViewPlanif = findViewById(R.id.recyclerViewPlanif);
        recyclerViewPlanif.setLayoutManager(new LinearLayoutManager(this));


        //bouton bonhomme
        ImageButton btnBonhomme = findViewById(R.id.btnBonhomme2);
        // OnClickListener pour le bouton bonhomme
        btnBonhomme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonhommePopup(); // Méthode pour afficher le popup
            }
        });

        //barre de recherche
        SearchView searchView = findViewById(R.id.searchView2);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Cette méthode est appelée lorsque l'utilisateur soumet la requête de recherche
                // Vous pouvez effectuer la recherche ici en utilisant la date entrée dans le champ de recherche (query)
                // Appelez la méthode qui effectue la recherche avec la date spécifiée
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Cette méthode est appelée lorsque le texte de recherche change
                // Vous pouvez effectuer des actions en temps réel ici si nécessaire
                return false;
            }
        });
    }
    private void performSearch(String date) {
    }

//chargement des chauffeurs via la firebase
    private void loadChauffeursFromFirebase() {
        FirebaseFirestore.getInstance().collection("utilisateurs")
                .whereEqualTo("role", 2) // Filtrez les utilisateurs ayant le rôle de chauffeur
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserDTO> chauffeurs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserDTO utilisateur = document.toObject(UserDTO.class);
                            chauffeurs.add(utilisateur);
                        }
                        // Log pour vérifier la liste des chauffeurs
                        Log.d("Planificateur", "Chauffeurs récupérés : " + chauffeurs);

                        // Mettez à jour votre adaptateur avec la liste des chauffeurs
                       // planifAdapter.setChauffeurs(chauffeurs);
                    } else {
                        Toast.makeText(Planificateur.this, "Erreur lors du chargement des chauffeurs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//chargement des produits via la firebase

    private void loadProductsFromFirebase() {
        db.collection("livraison")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                      //commande.clear(); // Effacer la liste actuelle avant de la remplir à nouveau

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommandeDTO  comm = document.toObject(CommandeDTO.class);
                            commande.add(comm);

                        }
                        Log.d("PlanifAdapter", "taille: " + commande.size());

                        // Notifier l'adaptateur que les données ont changé
                        planifAdapter = new PlanifAdapter(this,commande);
                        recyclerViewPlanif.setAdapter(planifAdapter);
                        Toast.makeText(Planificateur.this, ""+commande.size(), Toast.LENGTH_SHORT).show();
                        planifAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Planificateur.this, "Erreur lors du chargement des commandes", Toast.LENGTH_SHORT).show();
                    }
                });
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
        Intent intent = new Intent(Planificateur.this, MesInformations.class);
        startActivity(intent);
    }
    private void deconnexion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Planificateur.this, Connexion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Terminer l'activité actuelle
    }
}