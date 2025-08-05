package com.example.gestionproduit;

import android.os.Parcel;
import android.os.Parcelable;

public class Produit implements Parcelable {
    private String nom;
    private int imageResId;
    private double prix;

    public Produit(String nom, int imageResId, double prix) {
        this.nom = nom != null ? nom.trim() : "";
        this.imageResId = imageResId;
        this.prix = Math.max(0, prix);
    }

    protected Produit(Parcel in) {
        nom = in.readString();
        imageResId = in.readInt();
        prix = in.readDouble();
    }

    public static final Creator<Produit> CREATOR = new Creator<Produit>() {
        @Override
        public Produit createFromParcel(Parcel in) {
            return new Produit(in);
        }

        @Override
        public Produit[] newArray(int size) {
            return new Produit[size];
        }
    };

    public String getNom() { return nom; }
    public int getImageResId() { return imageResId; }
    public double getPrix() { return prix; }

    public void setNom(String nom) { this.nom = nom != null ? nom.trim() : ""; }
    public void setPrix(double prix) { this.prix = Math.max(0, prix); }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeInt(imageResId);
        dest.writeDouble(prix);
    }
}
