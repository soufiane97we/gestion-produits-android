package com.example.gestionproduit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gestion_produit.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PANIER = "panier";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOM_PRODUIT = "nom_produit";
    private static final String COLUMN_PRIX_UNITAIRE = "prix_unitaire";
    private static final String COLUMN_QUANTITE = "quantite";
    private static final String COLUMN_IMAGE_RES_ID = "image_res_id";
    private static final String COLUMN_DATE_AJOUT = "date_ajout";
    private static final String COLUMN_TOTAL = "total";

    private static final String CREATE_TABLE_PANIER =
        "CREATE TABLE " + TABLE_PANIER + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_NOM_PRODUIT + " TEXT NOT NULL, " +
        COLUMN_PRIX_UNITAIRE + " REAL NOT NULL, " +
        COLUMN_QUANTITE + " INTEGER NOT NULL, " +
        COLUMN_IMAGE_RES_ID + " INTEGER NOT NULL, " +
        COLUMN_DATE_AJOUT + " TEXT NOT NULL, " +
        COLUMN_TOTAL + " REAL NOT NULL" +
        ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANIER);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER);
        onCreate(db);
    }
    
    public long ajouterAuPanier(String nomProduit, double prixUnitaire, int quantite, int imageResId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_PANIER,
            new String[]{COLUMN_ID, COLUMN_QUANTITE},
            COLUMN_NOM_PRODUIT + "=?",
            new String[]{nomProduit},
            null, null, null);

        long result;
        if (cursor.moveToFirst()) {
            int idExistant = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int quantiteExistante = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITE));
            int nouvelleQuantite = quantiteExistante + quantite;
            double nouveauTotal = prixUnitaire * nouvelleQuantite;

            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITE, nouvelleQuantite);
            values.put(COLUMN_TOTAL, nouveauTotal);
            values.put(COLUMN_DATE_AJOUT, getCurrentDateTime());

            result = db.update(TABLE_PANIER, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(idExistant)});
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOM_PRODUIT, nomProduit);
            values.put(COLUMN_PRIX_UNITAIRE, prixUnitaire);
            values.put(COLUMN_QUANTITE, quantite);
            values.put(COLUMN_IMAGE_RES_ID, imageResId);
            values.put(COLUMN_DATE_AJOUT, getCurrentDateTime());
            values.put(COLUMN_TOTAL, prixUnitaire * quantite);

            result = db.insert(TABLE_PANIER, null, values);
        }

        cursor.close();
        db.close();
        return result;
    }
    
    public List<PanierItem> getTousLesProduitsDuPanier() {
        List<PanierItem> listePanier = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_PANIER, null, null, null, null, null, 
            COLUMN_DATE_AJOUT + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                PanierItem item = new PanierItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM_PRODUIT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRIX_UNITAIRE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_AJOUT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL))
                );
                listePanier.add(item);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return listePanier;
    }
    
    public int mettreAJourQuantite(int id, int nouvelleQuantite, double prixUnitaire) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITE, nouvelleQuantite);
        values.put(COLUMN_TOTAL, prixUnitaire * nouvelleQuantite);
        values.put(COLUMN_DATE_AJOUT, getCurrentDateTime());
        
        int result = db.update(TABLE_PANIER, values, COLUMN_ID + "=?", 
            new String[]{String.valueOf(id)});
        
        db.close();
        return result;
    }
    
    public int supprimerDuPanier(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PANIER, COLUMN_ID + "=?", 
            new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
    
    public void viderPanier() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANIER, null, null);
        db.close();
    }
    
    public double calculerTotalPanier() {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_TOTAL + ") FROM " + TABLE_PANIER, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        
        cursor.close();
        db.close();
        return total;
    }
    
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public int getNombreArticlesPanier() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_QUANTITE + ") FROM " + TABLE_PANIER, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }
}
