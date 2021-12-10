package com.dila.apprawat.network.api;

public class URLServer {
    public static final String BASE_URL = "http://10.0.2.2/manajemen/api/";
    public static final String LOGIN = BASE_URL + "auth/login";
    public static final String LOGOUT = BASE_URL + "auth/logout";
    public static final String REGISTER = BASE_URL + "auth/register";
    public static final String PUTMEMBER = BASE_URL + "member";
    public static final String UBAHPASSWORD = BASE_URL + "member/password";
    public static final String GETRAWATINAP = BASE_URL + "rawat_inap";
    public static final String GETRAWATJALAN = BASE_URL + "rawat_jalan";
}
