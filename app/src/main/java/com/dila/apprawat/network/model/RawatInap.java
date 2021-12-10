package com.dila.apprawat.network.model;

public class RawatInap {
    private int id, umur;
    private String no_rekam_inap, nama_pasien, kelamin, pekerjaan, alamat, tgl_masuk,
            p_jawab, pekerjaan_p_jawab, keterangan;

    public String getNo_rekam_inap() {
        return no_rekam_inap;
    }

    public void setNo_rekam_inap(String no_rekam_inap) {
        this.no_rekam_inap = no_rekam_inap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getNama_pasien() {
        return nama_pasien;
    }

    public void setNama_pasien(String nama_pasien) {
        this.nama_pasien = nama_pasien;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTgl_masuk() {
        return tgl_masuk;
    }

    public void setTgl_masuk(String tgl_masuk) {
        this.tgl_masuk = tgl_masuk;
    }

    public String getP_jawab() {
        return p_jawab;
    }

    public void setP_jawab(String p_jawab) {
        this.p_jawab = p_jawab;
    }

    public String getPekerjaan_p_jawab() {
        return pekerjaan_p_jawab;
    }

    public void setPekerjaan_p_jawab(String pekerjaan_p_jawab) {
        this.pekerjaan_p_jawab = pekerjaan_p_jawab;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
