package com.suncode.pegimakan.model;

public class Makanan {
    String namaMakanan;
    String descMakanan;
    String HargaMakanan;

    public Makanan() {
    }

    public Makanan(String namaMakanan, String descMakanan, String hargaMakanan) {
        this.namaMakanan = namaMakanan;
        this.descMakanan = descMakanan;
        HargaMakanan = hargaMakanan;
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
}