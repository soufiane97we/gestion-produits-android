package com.example.gestionproduit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProduitDetailsFragment extends Fragment {
    
    private static final String ARG_PRODUIT = "produit";
    
    private Produit produit;
    private int quantite = 1;
    private DatabaseHelper databaseHelper;

    private ImageView imageViewProduit;
    private TextView textViewNomProduit;
    private TextView textViewPrixProduit;
    private TextView textViewQuantite;
    private TextView textViewTotalQuantite;
    private ImageButton btnDiminuerQuantite;
    private ImageButton btnAugmenterQuantite;
    private Button btnAjouterAuPanier;
    private Button btnRetour;
    
    public ProduitDetailsFragment() {
    }
    
    public static ProduitDetailsFragment newInstance(Produit produit) {
        ProduitDetailsFragment fragment = new ProduitDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUIT, produit);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            produit = getArguments().getParcelable(ARG_PRODUIT);
        }
        databaseHelper = new DatabaseHelper(getContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produit_details, container, false);
        
        initializeViews(view);
        setupData();
        setupListeners();
        updateTotalQuantite();
        
        return view;
    }
    
    private void initializeViews(View view) {
        imageViewProduit = view.findViewById(R.id.imageViewProduit);
        textViewNomProduit = view.findViewById(R.id.textViewNomProduit);
        textViewPrixProduit = view.findViewById(R.id.textViewPrixProduit);
        textViewQuantite = view.findViewById(R.id.textViewQuantite);
        textViewTotalQuantite = view.findViewById(R.id.textViewTotalQuantite);
        btnDiminuerQuantite = view.findViewById(R.id.btnDiminuerQuantite);
        btnAugmenterQuantite = view.findViewById(R.id.btnAugmenterQuantite);
        btnAjouterAuPanier = view.findViewById(R.id.btnAjouterAuPanier);
        btnRetour = view.findViewById(R.id.btnRetour);
    }
    
    private void setupData() {
        if (produit != null) {
            imageViewProduit.setImageResource(produit.getImageResId());
            textViewNomProduit.setText(produit.getNom());
            textViewPrixProduit.setText(String.format("$%.2f", produit.getPrix()));
        }
    }
    
    private void setupListeners() {
        btnDiminuerQuantite.setOnClickListener(v -> {
            if (quantite > 1) {
                quantite--;
                updateQuantiteDisplay();
                updateTotalQuantite();
            }
        });
        
        btnAugmenterQuantite.setOnClickListener(v -> {
            if (quantite < 99) { 
                quantite++;
                updateQuantiteDisplay();
                updateTotalQuantite();
            }
        });
        
        btnAjouterAuPanier.setOnClickListener(v -> ajouterAuPanier());
        
        btnRetour.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                getActivity().getSupportFragmentManager().popBackStack();
                ((MainActivity) getActivity()).showMainView();
            }
        });
    }
    
    private void updateQuantiteDisplay() {
        textViewQuantite.setText(String.valueOf(quantite));
    }
    
    private void updateTotalQuantite() {
        if (produit != null) {
            double total = produit.getPrix() * quantite;
            textViewTotalQuantite.setText(String.format("Total: $%.2f", total));
        }
    }
    
    private void ajouterAuPanier() {
        if (produit == null) {
            Toast.makeText(getContext(), "Erreur: produit non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            long result = databaseHelper.ajouterAuPanier(
                produit.getNom(),
                produit.getPrix(),
                quantite,
                produit.getImageResId()
            );
            
            if (result != -1) {
                Toast.makeText(getContext(), 
                    "Produit ajouté au panier !", 
                    Toast.LENGTH_SHORT).show();
                
                quantite = 1;
                updateQuantiteDisplay();
                updateTotalQuantite();
                
            } else {
                Toast.makeText(getContext(), 
                    "Erreur lors de l'ajout au panier", 
                    Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), 
                "Erreur: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
