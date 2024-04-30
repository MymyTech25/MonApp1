// PanierActivity.java

package com.example.monapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PanierActivity extends AppCompatActivity implements CartAdapter.CartAdapterListener {

    private List<Product> cartList;
    private CartAdapter cartAdapter;
    private TextView textViewTotalAPayer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        //liste du panier depuis l'intent
        Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
        cartList = new Gson().fromJson(getIntent().getStringExtra("cartList"), listType);

        // Bouton Retour à la page précédente
        Button btnRetourProduits = findViewById(R.id.btnRetourProduits);
        btnRetourProduits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });


        // Initialiser la RecyclerView pour afficher le panier
        RecyclerView recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList,this);
        recyclerViewCart.setAdapter(cartAdapter);

        // Récupérer le TextView pour afficher le total à payer

        textViewTotalAPayer = findViewById(R.id.textViewTotalAPayer);
        Button btnFinaliserCommande = findViewById(R.id.button2);
        btnFinaliserCommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrir la nouvelle activité Livraison
                Intent intent = new Intent(PanierActivity.this, Livraison.class);
                // Ajouter la liste de produits à l'intention
                intent.putExtra("listeProduits", (Serializable) cartList);

                startActivity(intent);
            }
        });
        // vider le panier
        Button btnViderPanier = findViewById(R.id.btnViderPanier);
        btnViderPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartList.clear();
                cartAdapter.notifyDataSetChanged();
                Toast.makeText(PanierActivity.this, "Le panier a été vidé", Toast.LENGTH_SHORT).show();

                // MAJ du total après le chargement initial
                updateTotal();
            }
        });

        }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }


    public void updateTotal() {
        // Calcul de la somme des totaux des produits dans le panier
        double totalAPayer = 0;
        for (Product product : cartList) {
            totalAPayer += product.getTotal();
        }

        // MAJ du TextView avec le total calculé
        textViewTotalAPayer.setText("TOTAL À PAYER : " + totalAPayer);
    }

}
