package com.example.gestionproduit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.PanierViewHolder> {
    
    public interface OnPanierItemListener {
        void onQuantiteChanged(PanierItem item, int nouvelleQuantite);
        void onModifierQuantite(PanierItem item);
        void onSupprimerItem(PanierItem item);
    }
    
    private List<PanierItem> listePanier;
    private OnPanierItemListener listener;
    
    public PanierAdapter(OnPanierItemListener listener) {
        this.listePanier = new ArrayList<>();
        this.listener = listener;
    }
    
    public void updatePanier(List<PanierItem> nouveauPanier) {
        this.listePanier.clear();
        if (nouveauPanier != null) {
            this.listePanier.addAll(nouveauPanier);
        }
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public PanierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panier, parent, false);
        return new PanierViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PanierViewHolder holder, int position) {
        PanierItem item = listePanier.get(position);
        holder.bind(item, listener);
    }
    
    @Override
    public int getItemCount() {
        return listePanier.size();
    }
    
    static class PanierViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView imageViewProduitPanier;
        private TextView textViewNomProduitPanier;
        private TextView textViewPrixUnitairePanier;
        private TextView textViewQuantitePanier;
        private TextView textViewDateAjoutPanier;
        private ImageButton btnDiminuerQuantitePanier;
        private ImageButton btnAugmenterQuantitePanier;
        private ImageButton btnModifierQuantite;
        private ImageButton btnSupprimerProduitPanier;
        
        public PanierViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewProduitPanier = itemView.findViewById(R.id.imageViewProduitPanier);
            textViewNomProduitPanier = itemView.findViewById(R.id.textViewNomProduitPanier);
            textViewPrixUnitairePanier = itemView.findViewById(R.id.textViewPrixUnitairePanier);
            textViewQuantitePanier = itemView.findViewById(R.id.textViewQuantitePanier);
            textViewDateAjoutPanier = itemView.findViewById(R.id.textViewDateAjoutPanier);
            btnDiminuerQuantitePanier = itemView.findViewById(R.id.btnDiminuerQuantitePanier);
            btnAugmenterQuantitePanier = itemView.findViewById(R.id.btnAugmenterQuantitePanier);
            btnModifierQuantite = itemView.findViewById(R.id.btnModifierQuantite);
            btnSupprimerProduitPanier = itemView.findViewById(R.id.btnSupprimerProduitPanier);
        }
        
        public void bind(PanierItem item, OnPanierItemListener listener) {
            imageViewProduitPanier.setImageResource(item.getImageResId());
            textViewNomProduitPanier.setText(item.getNomProduit());
            textViewPrixUnitairePanier.setText(
                String.format("Prix: $%.2f", item.getPrixUnitaire()));
            textViewQuantitePanier.setText(
                String.format("Quantité: %d", item.getQuantite()));
            textViewDateAjoutPanier.setText(
                String.format("Date: %s", item.getDateAjout()));

            itemView.setOnClickListener(v -> listener.onModifierQuantite(item));

            btnDiminuerQuantitePanier.setOnClickListener(v -> {
                int nouvelleQuantite = item.getQuantite() - 1;
                if (nouvelleQuantite >= 0) {
                    listener.onQuantiteChanged(item, nouvelleQuantite);
                }
            });

            btnAugmenterQuantitePanier.setOnClickListener(v -> {
                int nouvelleQuantite = item.getQuantite() + 1;
                if (nouvelleQuantite <= 99) {
                    listener.onQuantiteChanged(item, nouvelleQuantite);
                }
            });

            btnModifierQuantite.setOnClickListener(v ->
                listener.onModifierQuantite(item));

            btnSupprimerProduitPanier.setOnClickListener(v ->
                listener.onSupprimerItem(item));
        }
    }
}
