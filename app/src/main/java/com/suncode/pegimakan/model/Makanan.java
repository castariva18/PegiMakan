package com.suncode.pegimakan.model;

public class Makanan {
    private String namaMakanan;
    private String descMakanan;
    private String HargaMakanan;
    private String gambar;
    private String key;

    public Makanan(String namaMakanan, String descMakanan, String hargaMakanan, String gambar, String key) {
        this.namaMakanan = namaMakanan;
        this.descMakanan = descMakanan;
        HargaMakanan = hargaMakanan;
        this.gambar = gambar;
        this.key = key;
    }

    public Makanan(String namaMakanan, String descMakanan, String hargaMakanan) {
        this.namaMakanan = namaMakanan;
        this.descMakanan = descMakanan;
        HargaMakanan = hargaMakanan;
    }

    public Makanan() {
    }

    public String getNamaMakanan() {
        return namaMakanan;
    }

    public void setNamaMakanan(String namaMakanan) {
        this.namaMakanan = namaMakanan;
    }

    public String getDescMakanan() {
        return descMakanan;
    }

    public void setDescMakanan(String descMakanan) {
        this.descMakanan = descMakanan;
    }

    public String getHargaMakanan() {
        return HargaMakanan;
    }

    public void setHargaMakanan(String hargaMakanan) {
        HargaMakanan = hargaMakanan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}