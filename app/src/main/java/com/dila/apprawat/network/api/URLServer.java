package com.dila.apprawat.network.api;

public class URLServer {
    public static final String BASE_URL = "http://manajemen-rawat.my.id/api/";
    public static final String IMAGE =  "http://manajemen-rawat.my.id/assets/images/uploads/";
    public static final String LOGIN = BASE_URL + "auth/login";
    public static final String LOGOUT = BASE_URL + "auth/logout";
    public static final String REGISTER = BASE_URL + "auth/register";
    public static final String PUTMEMBER = BASE_URL + "member";
    public static final String UBAHPASSWORD = BASE_URL + "member/password";
    public static final String GETRAWATINAP = BASE_URL + "rawat_inap";
    public static final String GETRAWATINAPID = BASE_URL + "rawat_inap?id=";
    public static final String GETRAWATJALAN = BASE_URL + "rawat_jalan";
    public static final String GETRAWATJALANID = BASE_URL + "rawat_jalan?id=";
    public static final String GETPENYAKIT = BASE_URL + "penyakit";
    public static final String POSTGAMBARPFORILE = BASE_URL + "member/gambar";
    public static final String GETGAMBAR = BASE_URL + "member/gambar?id_m=";
}
