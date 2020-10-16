package com.suncode.pegimakan.model;

public class Minuman {
    String namaMinuman;
    String descMinuman;
    String HargaMinuman;

    public Minuman() {
    }

    public Minuman(String namaMinuman, String descMinuman, String hargaMinuman) {
        this.namaMinuman = namaMinuman;
        this.descMinuman = descMinuman;
        HargaMinuman = hargaMinuman;
    }

    public String getNamaMinuman() {
        return namaMinuman;
    }

    public void setNamaMinuman(String namaMinuman) {
        this.namaMinuman = namaMinuman;
    }

    public String getDescMinuman() {
        return descMinuman;
    }

    public void setDescMinuman(String descMinuman) {
        this.descMinuman = descMinuman;
    }

    public String getHargaMinuman() {
        return HargaMinuman;
    }

    public void setHargaMinuman(String hargaMinuman) {
        HargaMinuman = hargaMinuman;
    }
}
