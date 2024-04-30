package com.example.monapp1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Produits extends AppCompatActivity {

    private RecyclerView recyclerViewProduits;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private SearchView searchView;
    private FirebaseFirestore db;
    private List<Product> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produits);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();

        // Initialiser la liste des produits
        productList = new ArrayList<>();
        cartList = new ArrayList<>();  // Initialiser la liste du panier
        loadProductsFromFirebase();

        // Initialiser la RecyclerView

        recyclerViewProduits = findViewById(R.id.recyclerViewProduits);
        recyclerViewProduits.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                addToCart(product);
            }
        });
        recyclerViewProduits.setAdapter(productAdapter);

        // Ajoutez le code pour le bouton panier ici
        ImageButton btnPanier = findViewById(R.id.btnPanier);
        btnPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrir l'activité du panier
                Intent intent = new Intent(Produits.this, PanierActivity.class);

                // Utilisation de Gson pour sérialiser la liste
                intent.putExtra("cartList", new Gson().toJson(cartList));

                startActivity(intent);
            }
        });

        // Ajoutez le code pour la barre de recherche ici
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Action à effectuer lorsque l'utilisateur soumet la recherche (vous pouvez ignorer cela si vous n'en avez pas besoin)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Action à effectuer lorsque le texte de la recherche change
                filterProducts(newText);
                return true;
            }
        });

        // Charger les produits depuis Firebase
        loadProductsFromFirebase();

        //bouton bonhomme
        ImageButton btnBonhomme = findViewById(R.id.btnBonhomme);
        // OnClickListener pour le bouton bonhomme
        btnBonhomme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBonhommePopup(); // Méthode pour afficher le popup
            }
        });

    }
    // Méthode pour filtrer la liste des produits en fonction du texte de recherche
    private void filterProducts(String searchText) {
        List<Product> filteredProducts = new ArrayList<>();

        if (TextUtils.isEmpty(searchText)) {
            // Si le texte de recherche est vide, affichez tous les produits
            filteredProducts.addAll(productList);
        } else {
            // Si le texte de recherche n'est pas vide, filtrez les produits en fonction du texte
            for (Product product : productList) {
                if (product.getNom().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }

        // Mettez à jour l'adaptateur avec la nouvelle liste filtrée
        productAdapter.filterList(filteredProducts);
    }

    private void addToCart(Product product) {
        // Ajoutez un log pour afficher le nom du produit sélectionné
        Log.d("addToCart", "Produit sélectionné : " + product.getNom());

        // Vérifier si le produit est déjà dans le panier
        boolean productExistsInCart = false;

        for (Product cartProduct : cartList) {
            // Ajoutez un log pour afficher le nom des produits dans le panier
            Log.d("addToCart", "Produit dans le panier : " + cartProduct.getNom());

            if (cartProduct.getNom().equals(product.getNom())) {
                // Le produit est déjà dans le panier, augmenter simplement la quantité
                cartProduct.increaseQuantity();
                productExistsInCart = true;
                break;
            }
        }

        if (!productExistsInCart) {
            // Le produit n'est pas encore dans le panier, l'ajouter
            //Product cartProduct = new Product(product.getNom(), product.getPrix());
            //cartProduct.setImage();
            product.setTotal(product.getPrix());
            cartList.add(product);
        }


        // Affichez les noms des produits dans le panier après chaque ajout
        Log.d("addToCart", "Produits dans le panier après l'ajout : " + cartList);

        // Utilisez le nom directement du produit pour le Toast
        Toast.makeText(this, "Ajouté au panier : " + product.getNom(), Toast.LENGTH_SHORT).show();
    }



    private void loadProductsFromFirebase() {
        db.collection("produits")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear(); // Effacer la liste actuelle avant de la remplir à nouveau

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);

                        }
                        Log.d("ProductAdapter", "taille: " + productList.size());

                        // Notifier l'adaptateur que les données ont changé
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Produits.this, "Erreur lors du chargement des produits", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Produits.this, MesInformations.class);
        startActivity(intent);
    }
    private void deconnexion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Produits.this, Connexion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Terminer l'activité actuelle
    }
}
