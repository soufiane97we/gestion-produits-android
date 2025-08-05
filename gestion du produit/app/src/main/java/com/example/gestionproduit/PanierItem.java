package com.example.gestionproduit;

import android.os.Parcel;
import android.os.Parcelable;

public class PanierItem implements Parcelable {
    private int id;
    private String nomProduit;
    private double prixUnitaire;
    private int quantite;
    private int imageResId;
    private String dateAjout;
    private double total;
    
    public PanierItem(int id, String nomProduit, double prixUnitaire, int quantite,
                     int imageResId, String dateAjout, double total) {
        this.id = id;
        this.nomProduit = nomProduit != null ? nomProduit.trim() : "";
        this.prixUnitaire = Math.max(0, prixUnitaire);
        this.quantite = Math.max(1, quantite);
        this.imageResId = imageResId;
        this.dateAjout = dateAjout != null ? dateAjout : "";
        this.total = Math.max(0, total);
    }
    
    public PanierItem(String nomProduit, double prixUnitaire, int quantite,
                     int imageResId, String dateAjout) {
        this(0, nomProduit, prixUnitaire, quantite, imageResId, dateAjout, 
             prixUnitaire * quantite);
    }
    
    protected PanierItem(Parcel in) {
        id = in.readInt();
        nomProduit = in.readString();
        prixUnitaire = in.readDouble();
        quantite = in.readInt();
        imageResId = in.readInt();
        dateAjout = in.readString();
        total = in.readDouble();
    }
    
    public static final Creator<PanierItem> CREATOR = new Creator<PanierItem>() {
        @Override
        public PanierItem createFromParcel(Parcel in) {
            return new PanierItem(in);
        }
        
        @Override
        public PanierItem[] newArray(int size) {
            return new PanierItem[size];
        }
    };
    
    public int getId() { return id; }
    public String getNomProduit() { return nomProduit; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public int getQuantite() { return quantite; }
    public int getImageResId() { return imageResId; }
    public String getDateAjout() { return dateAjout; }
    public double getTotal() { return total; }
    
    public void setId(int id) { this.id = id; }
    
    public void setNomProduit(String nomProduit) { 
        this.nomProduit = nomProduit != null ? nomProduit.trim() : ""; 
    }
    
    public void setPrixUnitaire(double prixUnitaire) { 
        this.prixUnitaire = Math.max(0, prixUnitaire);
        updateTotal();
    }
    
    public void setQuantite(int quantite) { 
        this.quantite = Math.max(1, quantite);
        updateTotal();
    }
    
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    
    public void setDateAjout(String dateAjout) { 
        this.dateAjout = dateAjout != null ? dateAjout : ""; 
    }
    
    public void setTotal(double total) { this.total = Math.max(0, total); }
    
    private void updateTotal() {
        this.total = this.prixUnitaire * this.quantite;
    }
    
    public void recalculerTotal() {
        updateTotal();
    }
    
    public String getTotalFormate() {
        return String.format("%.2f €", total);
    }
    
    public String getPrixUnitaireFormate() {
        return String.format("%.2f €", prixUnitaire);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nomProduit);
        dest.writeDouble(prixUnitaire);
        dest.writeInt(quantite);
        dest.writeInt(imageResId);
        dest.writeString(dateAjout);
        dest.writeDouble(total);
    }
    
    @Override
    public String toString() {
        return "PanierItem{" +
                "id=" + id +
                ", nomProduit='" + nomProduit + '\'' +
                ", prixUnitaire=" + prixUnitaire +
                ", quantite=" + quantite +
                ", imageResId=" + imageResId +
                ", dateAjout='" + dateAjout + '\'' +
                ", total=" + total +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PanierItem that = (PanierItem) obj;
        return id == that.id && nomProduit.equals(that.nomProduit);
    }
    
    @Override
    public int hashCode() {
        return nomProduit.hashCode() + id;
    }
}
