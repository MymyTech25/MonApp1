package com.example.monapp1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private List<Product> filteredList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = productList;
        this.filteredList = new ArrayList<>(productList);
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filterList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;
        private final TextView productPriceTextView;
        private final ImageView imageView;

        public ProductViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            productNameTextView = itemView.findViewById(R.id.textViewNomProduit);
            productPriceTextView = itemView.findViewById(R.id.textViewPrixProduit);
            imageView = itemView.findViewById(R.id.imageViewProduit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(productList.get(position));
                        }
                    }
                }
            });
        }

        public void bind(Product product) {
            Log.d("ProductAdapter", "Binding product: " + product.getNom());

            if (productNameTextView == null || productPriceTextView == null) {
                Log.e("ProductAdapter", "TextViews are null");
                return;
            }

            productNameTextView.setText(product.getNom());
            productPriceTextView.setText(String.valueOf(product.getPrix()));

            // Récupérer l'ID de la ressource de manière dynamique
           int resID = context.getResources().getIdentifier(product.getImage(), "drawable", context.getPackageName());

            // Appliquer l'image à l'ImageView
            imageView.setImageResource(resID);
            //imageView.setImageResource(R.drawable.boeuf);

        }
    }
}
