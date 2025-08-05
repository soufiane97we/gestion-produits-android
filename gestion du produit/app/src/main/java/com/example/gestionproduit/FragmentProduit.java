package com.example.gestionproduit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentProduit extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_PRODUIT = "produit";

    private int position = -1;
    private Produit produit;
    private OnProduitModifieListener listener;

    public interface OnProduitModifieListener {
        void onProduitModifie(int position, Produit produitModifie);
        void onProduitAjoute(Produit nouveauProduit);
    }

    public FragmentProduit() {

    }

    public static FragmentProduit newInstance(int position, Produit produit) {
        FragmentProduit fragment = new FragmentProduit();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelable(ARG_PRODUIT, produit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnProduitModifieListener) getActivity();

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION, -1);
            produit = getArguments().getParcelable(ARG_PRODUIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_produit, container, false);

        EditText editNom = view.findViewById(R.id.editNom);
        EditText editPrix = view.findViewById(R.id.editPrix);
        Button btnValider = view.findViewById(R.id.btnValider);

        if (produit != null) {
            editNom.setText(produit.getNom());
            editPrix.setText(String.valueOf(produit.getPrix()));
        }

        btnValider.setOnClickListener(v -> {
            String nom = editNom.getText().toString().trim();
            String prixStr = editPrix.getText().toString().trim();

            if (TextUtils.isEmpty(nom)) {
                editNom.setError("Le nom ne peut pas être vide");
                return;
            }

            if (TextUtils.isEmpty(prixStr)) {
                editPrix.setError("Le prix ne peut pas être vide");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixStr);
                if (prix < 0) {
                    editPrix.setError("Le prix doit être positif");
                    return;
                }
            } catch (NumberFormatException e) {
                editPrix.setError("Prix invalide");
                return;
            }

            int imageResId = (produit != null) ? produit.getImageResId() : R.drawable.ic_launcher_foreground;

            Produit nouveauProduit = new Produit(nom, imageResId, prix);

            if (position >= 0) {
                listener.onProduitModifie(position, nouveauProduit);
                Toast.makeText(getContext(), "Produit modifié", Toast.LENGTH_SHORT).show();
            } else {
                listener.onProduitAjoute(nouveauProduit);
                Toast.makeText(getContext(), "Produit ajouté", Toast.LENGTH_SHORT).show();
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
