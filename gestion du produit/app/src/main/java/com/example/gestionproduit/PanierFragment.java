package com.example.gestionproduit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PanierFragment extends Fragment implements PanierAdapter.OnPanierItemListener {
    
    private DatabaseHelper databaseHelper;
    private PanierAdapter adapter;
    private List<PanierItem> listePanier;

    private RecyclerView recyclerViewPanier;
    private LinearLayout layoutPanierVide;
    private TextView textViewNombreArticles;
    private TextView textViewTotalPanier;
    private Button btnViderPanier;
    private Button btnCommander;
    private ImageButton btnRetourPanier;
    
    public PanierFragment() {
    }
    
    public static PanierFragment newInstance() {
        return new PanierFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panier, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        chargerPanier();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerViewPanier = view.findViewById(R.id.recyclerViewPanier);
        layoutPanierVide = view.findViewById(R.id.layoutPanierVide);
        textViewTotalPanier = view.findViewById(R.id.textViewTotalPanier);
        btnViderPanier = view.findViewById(R.id.btnViderPanier);
        btnCommander = view.findViewById(R.id.btnCommander);
        btnRetourPanier = view.findViewById(R.id.btnRetourPanier);

        textViewNombreArticles = null;
    }
    
    private void setupRecyclerView() {
        recyclerViewPanier.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PanierAdapter(this);
        recyclerViewPanier.setAdapter(adapter);
    }
    
    private void setupListeners() {
        btnViderPanier.setOnClickListener(v -> confirmerViderPanier());
        btnCommander.setOnClickListener(v -> commander());
        btnRetourPanier.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                getActivity().getSupportFragmentManager().popBackStack();
                ((MainActivity) getActivity()).showMainView();
            }
        });
    }
    
    private void chargerPanier() {
        listePanier = databaseHelper.getTousLesProduitsDuPanier();
        adapter.updatePanier(listePanier);
        updateUI();
    }
    
    private void updateUI() {
        if (listePanier.isEmpty()) {
            layoutPanierVide.setVisibility(View.VISIBLE);
            recyclerViewPanier.setVisibility(View.GONE);
            textViewTotalPanier.setText("Total: $0.00");
        } else {
            layoutPanierVide.setVisibility(View.GONE);
            recyclerViewPanier.setVisibility(View.VISIBLE);

            double total = databaseHelper.calculerTotalPanier();
            textViewTotalPanier.setText(String.format("Total: $%.2f", total));
        }
    }
    
    @Override
    public void onQuantiteChanged(PanierItem item, int nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            supprimerItem(item);
            return;
        }
        
        int result = databaseHelper.mettreAJourQuantite(
            item.getId(), 
            nouvelleQuantite, 
            item.getPrixUnitaire()
        );
        
        if (result > 0) {
            chargerPanier(); // Recharger pour mettre à jour l'affichage
            Toast.makeText(getContext(), "Quantité mise à jour", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onModifierQuantite(PanierItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifier la quantité");
        
        final EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(item.getQuantite()));
        input.selectAll();
        builder.setView(input);
        
        builder.setPositiveButton("Confirmer", (dialog, which) -> {
            try {
                int nouvelleQuantite = Integer.parseInt(input.getText().toString().trim());
                if (nouvelleQuantite > 0 && nouvelleQuantite <= 99) {
                    onQuantiteChanged(item, nouvelleQuantite);
                } else {
                    Toast.makeText(getContext(), 
                        "Quantité invalide (1-99)", 
                        Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), 
                    "Veuillez entrer un nombre valide", 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    @Override
    public void onSupprimerItem(PanierItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous vraiment supprimer \"" + item.getNomProduit() + "\" du panier ?");
        
        builder.setPositiveButton("Oui", (dialog, which) -> supprimerItem(item));
        builder.setNegativeButton("Non", null);
        builder.show();
    }
    
    private void supprimerItem(PanierItem item) {
        int result = databaseHelper.supprimerDuPanier(item.getId());
        if (result > 0) {
            chargerPanier();
            Toast.makeText(getContext(), 
                item.getNomProduit() + " supprimé du panier", 
                Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), 
                "Erreur lors de la suppression", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    private void confirmerViderPanier() {
        if (listePanier.isEmpty()) {
            Toast.makeText(getContext(), "Le panier est déjà vide", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous vraiment vider tout le panier ?");
        
        builder.setPositiveButton("Oui", (dialog, which) -> {
            databaseHelper.viderPanier();
            chargerPanier();
            Toast.makeText(getContext(), "Panier vidé", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Non", null);
        builder.show();
    }
    
    private void commander() {
        if (listePanier.isEmpty()) {
            Toast.makeText(getContext(), "Votre panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double total = databaseHelper.calculerTotalPanier();
        int nombreArticles = databaseHelper.getNombreArticlesPanier();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmer la commande");
        builder.setMessage(String.format(
            "Commande de %d article(s)\nTotal: %.2f €\n\nConfirmer la commande ?", 
            nombreArticles, total));
        
        builder.setPositiveButton("Commander", (dialog, which) -> {
            // Ici vous pouvez ajouter la logique de commande
            databaseHelper.viderPanier();
            chargerPanier();
            Toast.makeText(getContext(), 
                "Commande confirmée ! Merci pour votre achat.", 
                Toast.LENGTH_LONG).show();
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        chargerPanier(); // Recharger le panier quand on revient sur le fragment
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
