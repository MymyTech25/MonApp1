package com.example.monapp1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnFirstConnexion = findViewById(R.id.btnFirstConnexion);

        // Bouton qui renvoie à la connexion
        btnFirstConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent pour démarrer l'activité Connexion
                Intent intent = new Intent(MainActivity2.this, Connexion.class);
                startActivity(intent);
            }
        });
    }
}