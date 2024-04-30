package com.example.monapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>  {

    private List<Product> cartList;
    private CartAdapterListener listener;
    private static Context context;

    public interface CartAdapterListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, List<Product> cartList, CartAdapterListener listener) {
        this.cartList = cartList;
        this.listener = listener;
        this.context = context;


    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.bind(product);

        holder.btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Diminuer la quantité du produit
                product.decreaseQuantity();
                // MAJ du total du produit après l'ajustement de la quantité
                notifyDataSetChanged();
                listener.onQuantityChanged();
            }
        });

        holder.btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Augmenter la quantité du produit
                product.increaseQuantity();
                // MAJ du total du produit après l'ajustement de la quantité
                notifyDataSetChanged();
                // Informer l'activité parente du changement de quantité
                listener.onQuantityChanged();
            }
        });
        // MAJ du total après l'ajout initial d'un produit
        ((PanierActivity) holder.itemView.getContext()).updateTotal();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }



    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;
        private final TextView productQuantityTextView;
        private final TextView productPriceTextView;
        private final Button btnDecreaseQuantity;
        private final Button btnIncreaseQuantity;
        private final TextView totalPriceTextView;
        private final  ImageView imageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            imageView = itemView.findViewById(R.id.imageViewProduit);
        }

        public void bind(Product product) {
            productNameTextView.setText(product.getNom());
            productPriceTextView.setText(String.valueOf(product.getPrix()));
            productQuantityTextView.setText("" + product.getQuantity());
            int resID = context.getResources().getIdentifier(product.getImage(), "drawable", context.getPackageName());
            imageView.setImageResource(resID);
            totalPriceTextView.setText("Total : " + String.valueOf(product.getTotal()));

        }

    }
}
