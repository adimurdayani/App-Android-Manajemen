package com.dila.apprawat.network.model;

public class Penyakit {
    private int id, ruangan;
    private String nama_penyakit;

    public int getRuangan() {
        return ruangan;
    }

    public void setRuangan(int ruangan) {
        this.ruangan = ruangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_penyakit() {
        return nama_penyakit;
    }

    public void setNama_penyakit(String nama_penyakit) {
        this.nama_penyakit = nama_penyakit;
    }
}
