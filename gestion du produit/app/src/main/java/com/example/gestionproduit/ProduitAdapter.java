package com.example.gestionproduit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onAddToPanier(int position);
    }

    private final List<Produit> produits;
    private final OnItemClickListener listener;

    public ProduitAdapter(List<Produit> produits, OnItemClickListener listener) {
        this.produits = produits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produit produit = produits.get(position);
        holder.nom.setText(produit.getNom());
        holder.prix.setText(String.format("$%.2f", produit.getPrix()));
        holder.image.setImageResource(produit.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.btnSupprimer.setOnClickListener(v -> listener.onDeleteClick(position));
        holder.btnAjouterPanier.setOnClickListener(v -> listener.onAddToPanier(position));
    }

    @Override
    public int getItemCount() {
        return produits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nom;
        TextView prix;
        ImageButton btnSupprimer;
        ImageButton btnAjouterPanier;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduit);
            nom = itemView.findViewById(R.id.nomProduit);
            prix = itemView.findViewById(R.id.prixProduit);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
            btnAjouterPanier = itemView.findViewById(R.id.btnAjouterPanier);
        }
    }
}
