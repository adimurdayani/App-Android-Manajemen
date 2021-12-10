package com.dila.apprawat.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dila.apprawat.R;
import com.dila.apprawat.network.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint( "SetTextI18n" )
public class UbahPasswod extends AppCompatActivity {

    private ImageView btn_kembali;
    private EditText edt_username, edt_password, edt_konfirmasi_password;
    private LinearLayout btn_simpan;
    private ProgressBar progress;
    private TextView text_simpan, nama;
    private StringRequest ubahPassword;
    private String password, konfir_password;
    private SharedPreferences preferences;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_passwod);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        setInit();
        setDisplay();
        setButton();
    }

    private void setUbahPassword() {
        progress.setVisibility(View.VISIBLE);
        text_simpan.setVisibility(View.GONE);
        ubahPassword = new StringRequest(Request.Method.POST, URLServer.UBAHPASSWORD, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();

                    showDialog();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showError(e.toString());
            }
            progress.setVisibility(View.GONE);
            text_simpan.setVisibility(View.VISIBLE);
        }, error -> {
            progress.setVisibility(View.GONE);
            text_simpan.setVisibility(View.VISIBLE);
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int id_m = preferences.getInt("id_m", 0);
                HashMap<String, String> map = new HashMap<>();
                map.put("id_m", String.valueOf(id_m));
                map.put("password", password);
                return map;
            }
        };
        setErrorNetwork();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(ubahPassword);
    }

    private void setDisplay() {
        nama.setText("Selamat Datang " + preferences.getString("nama", ""));
        edt_username.setText(preferences.getString("username", ""));
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_simpan.setOnClickListener(v -> {
            if (validasi()) {
                setUbahPassword();
            }
        });
    }

    private boolean validasi() {
        setInputText();
        if (password.isEmpty()) {
            edt_password.setError("Kolom password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            edt_password.setError("Kolom password tidak kurang dari 6 karakter!");
            return false;
        }
        if (konfir_password.isEmpty()) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak boleh kosong!");
            return false;
        } else if (konfir_password.length() < 6) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak kurang dari 6 karakter!");
            return false;
        } else if (!konfir_password.matches(password)) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak sama dengan password!");
            return false;
        }
        return true;
    }

    private void setInputText() {
        password = edt_password.getText().toString().trim();
        konfir_password = edt_konfirmasi_password.getText().toString().trim();
    }

    private void setInit() {
        btn_kembali = findViewById(R.id.btn_kembali);
        nama = findViewById(R.id.nama);
        edt_username = findViewById(R.id.username);
        edt_password = findViewById(R.id.password);
        edt_konfirmasi_password = findViewById(R.id.konfirmasi_password);
        btn_simpan = findViewById(R.id.btn_simpan);
        progress = findViewById(R.id.progress);
        text_simpan = findViewById(R.id.text_simpan);
    }

    private void setErrorNetwork() {
        ubahPassword.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                    showError("Koneksi gagal");
                }
            }
        });
    }

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }
}