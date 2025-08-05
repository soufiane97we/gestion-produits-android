package com.example.gestionproduit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentProduit.OnProduitModifieListener {

    private DrawerLayout drawerLayout;
    private List<Produit> listeProduits;
    private ProduitAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView iconMenu = findViewById(R.id.icon_menu);
        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);


        listeProduits = new ArrayList<>();
        listeProduits.add(new Produit("Casque", R.drawable.casque, 49.99));
        listeProduits.add(new Produit("Clavier", R.drawable.clavier, 29.99));
        listeProduits.add(new Produit("Écran", R.drawable.monitor1, 119.99));
        listeProduits.add(new Produit("Souris", R.drawable.souris, 19.99));
        listeProduits.add(new Produit("Manette", R.drawable.manette, 20.99));
        listeProduits.add(new Produit("PlayStation", R.drawable.playstation, 300.99));
        listeProduits.add(new Produit("Montres", R.drawable.watches, 600.99));
        listeProduits.add(new Produit("Xbox Series", R.drawable.xboxseries, 60.99));


        adapter = new ProduitAdapter(listeProduits, new ProduitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openFragmentModification(position);
            }

            @Override
            public void onDeleteClick(int position) {
                confirmerSuppressionProduit(position);
            }

            @Override
            public void onAddToPanier(int position) {
                ajouterAuPanierDepuisListe(position);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_ajouter) {
                openAjoutProduit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (item.getItemId() == R.id.nav_panier) {
                openPanier();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private void openFragmentModification(int position) {
        Produit produit = listeProduits.get(position);
        ProduitDetailsFragment fragment = ProduitDetailsFragment.newInstance(produit);

        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openAjoutProduit() {
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new FragmentProduit())
                .addToBackStack(null)
                .commit();
    }

    private void openPanier() {
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, PanierFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void confirmerSuppressionProduit(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer ce produit ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    Produit produit = listeProduits.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, produit.getNom() + " supprimé", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public void onProduitModifie(int position, Produit produitModifie) {
        listeProduits.set(position, produitModifie);
        adapter.notifyItemChanged(position);
        Toast.makeText(this, "Produit modifié", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProduitAjoute(Produit nouveauProduit) {
        listeProduits.add(nouveauProduit);
        adapter.notifyItemInserted(listeProduits.size() - 1);
        Toast.makeText(this, "Produit ajouté", Toast.LENGTH_SHORT).show();
    }

    private void ajouterAuPanierDepuisListe(int position) {
        Produit produit = listeProduits.get(position);

        try {
            long result = databaseHelper.ajouterAuPanier(
                produit.getNom(),
                produit.getPrix(),
                1,
                produit.getImageResId()
            );

            if (result != -1) {
                Toast.makeText(this, "Produit ajouté au panier !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showMainView() {
        findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
        findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            showMainView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
