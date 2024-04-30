package com.example.monapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Livraison extends AppCompatActivity {
    private CalendarView calendarView;
    private EditText editTextRue;
    private EditText editTextCodePostal;
    private EditText editTextVille;
    private MapView mapView;
    private Button btnPaiement;
    private Button btnFinalPaiement;
    private CommandeDTO commande;
    private Button btnRetourCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livraison);
        // Récupérer la liste de produits depuis l'intention
        Intent intent = getIntent();
        List<Product> listeProduits = (List<Product>) intent.getSerializableExtra("listeProduits");
        commande = new CommandeDTO();
        Toast.makeText(Livraison.this, "produit"+ listeProduits.get(0).getNom(), Toast.LENGTH_LONG).show();
        commande.setProductsList(listeProduits);
        // Utiliser la liste de produits comme nécessaire
        if (listeProduits != null) {
            // Faites quelque chose avec la liste de produits
        }

        calendarView = findViewById(R.id.calendarView2);
        editTextRue = findViewById(R.id.editTextRue);
        editTextCodePostal = findViewById(R.id.editTextCodePostal);
        editTextVille = findViewById(R.id.editTextVille);
        mapView = findViewById(R.id.mapView2);
        btnPaiement = findViewById(R.id.btnPaiement);
        btnFinalPaiement = findViewById(R.id.btnFinalPaiement);
        btnRetourCart = findViewById(R.id.btnRetourCart);





        Context ctx = getApplicationContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // Initialisation OSMDroid
        org.osmdroid.config.Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));

        // Définissez le TileSource de la carte
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Définissez les coordonnées de l'emplacement que vous souhaitez pointer
        double latitude = 37.7749; // Exemple : latitude de San Francisco
        double longitude = -122.4194; // Exemple : longitude de San Francisco

        IMapController mapController = mapView.getController();
        mapController.setZoom(20.0);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);


        /*calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Utilisez la date sélectionnée (year, month, dayOfMonth) comme nécessaire
               // String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                Date d = new Date(year,month,dayOfMonth);
                commande.setDate(d);
                commande.setAttribué(0);

                //Toast.makeText(Livraison.this, "selected date :"+ d, Toast.LENGTH_LONG).show();
            }
        });*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // instance de Calendar pour manipuler la date
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //  date correcte de l'instance de Calendar
                Date selectedDate = selectedCalendar.getTime();

                //la date actuelle
                Date currentDate = Calendar.getInstance().getTime();

                // pour vérifier que la date sélectionnée n'est pas antérieure à la date actuelle
                if (selectedDate.before(currentDate)) {
                    // La date sélectionnée est antérieure à la date actuelle
                    // Affiche un message d'erreur
                    Toast.makeText(Livraison.this, "Veuillez sélectionner une date à partir d'aujourd'hui.", Toast.LENGTH_LONG).show();
                } else {
                    // La date sélectionnée est valide, mettez à jour la date dans votre objet "commande"
                    Toast.makeText(Livraison.this, "selectedDate " + selectedDate, Toast.LENGTH_LONG).show();
                    commande.setDate(selectedDate);
                    commande.setAttribué(0);



                }
            }
        });




        btnFinalPaiement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //  déboguage
                Log.d("Livraison", "Date sélectionnée : " + commande.getDate());

                // pour s'assurer que la date sélectionnée n'est pas antérieure à la date actuelle
                if (commande.getDate() != null && commande.getDate().before(Calendar.getInstance().getTime())) {
                    // La date sélectionnée est antérieure à la date actuelle
                    // Ajoutez un log pour déboguer
                    Log.d("Livraison", "Date antérieure à aujourd'hui");

                    // message d'erreur/d'attention
                    Toast.makeText(Livraison.this, "Veuillez sélectionner une date à partir d'aujourd'hui.", Toast.LENGTH_LONG).show();
                } else {
                    // La date sélectionnée est valide: ajout de la commande à la Firebase
                    db.collection("livraison")
                            .add(commande)
                            .addOnSuccessListener(documentReference -> {

                                Log.d("Livraison", "Commande ajoutée avec succès");

                                // Successfully added the new order
                                Toast.makeText(Livraison.this,
                                        "Nouvelle commande ajoutée !",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {

                                Log.e("Livraison", "Échec de l'ajout de commande", e);

                                // Failed to add the new order
                                Toast.makeText(Livraison.this,
                                        "Échec de l'ajout de commande !",
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        btnPaiement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adresse = editTextRue.getText().toString() + ", " +
                        editTextCodePostal.getText().toString() + " " +
                        editTextVille.getText().toString();

                // Utilisez un Geocoder pour obtenir les coordonnées de l'adresse
                Geocoder geocoder = new Geocoder(Livraison.this);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(adresse, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        // Ajoutez un marqueur sur la carte
                        Marker marker = new Marker(mapView);
                        GeoPoint startPoint = new GeoPoint(latitude, longitude);
                        marker.setPosition(startPoint);

                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapView.getOverlays().add(marker);
                        commande.setPoint(latitude, longitude);
                        commande.setAdresse(adresse);


                        // Centrez la carte sur les coordonnées obtenues
                        mapView.getController().setCenter((IGeoPoint) new GeoPoint(latitude, longitude));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        btnRetourCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });


    }


}

