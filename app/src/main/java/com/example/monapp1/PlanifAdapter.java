package com.example.monapp1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monapp1.CommandeDTO;
import com.example.monapp1.R;

import java.util.List;

public class PlanifAdapter extends RecyclerView.Adapter<PlanifAdapter.ViewHolder> {

    private final Context context;
    private final List<CommandeDTO> commandeList;
    private List<UserDTO> chauffeurs;



    public PlanifAdapter(Context context, List<CommandeDTO> commandeList) {
        this.context = context;
        this.commandeList = commandeList;
    }
    public void setChauffeurs(List<UserDTO> chauffeurs) {
        this.chauffeurs = chauffeurs;
        // Log pour vérifier la liste des chauffeurs dans l'adaptateur
        Log.d("PlanifAdapter", "Chauffeurs dans l'adaptateur : " + chauffeurs);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("PlanifAdapter", "onBindViewHolder called for position: " + position);
        CommandeDTO commande = commandeList.get(position);

        // Mettez à jour les vues avec les données de la commande
        holder.textViewDateLivraison.setText( String.valueOf(commande.getDate()));
        holder.textViewPointLivraison.setText(String.valueOf( commande.getAdresse()));
        holder.textViewPrixLivraison.setText(String.valueOf(commande.getPrix()));
        holder.textViewProduitsLivraison.setText(String.valueOf(commande.getProductsList()) );

        //  liste réelle de chauffeurs
        if (chauffeurs != null && !chauffeurs.isEmpty()) {
            Log.d("PlanifAdapter", "Setting spinner for position: " + position);
            ArrayAdapter<UserDTO> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, chauffeurs);
            holder.spinnerChauffeur.setAdapter(spinnerAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return commandeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDateLivraison;
        public TextView textViewPointLivraison;
        public TextView textViewPrixLivraison;
        public TextView textViewProduitsLivraison;
        public Spinner spinnerChauffeur;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateLivraison = itemView.findViewById(R.id.textViewDateLivraison);
            textViewPointLivraison = itemView.findViewById(R.id.textViewPointLivraison);
            textViewPrixLivraison = itemView.findViewById(R.id.textViewPrixLivraison);
            textViewProduitsLivraison = itemView.findViewById(R.id.textViewProduitsLivraison);
            spinnerChauffeur = itemView.findViewById(R.id.spinnerChauffeur);
        }



    }

}
