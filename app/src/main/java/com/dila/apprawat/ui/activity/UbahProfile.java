package com.dila.apprawat.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
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
import com.dila.apprawat.network.model.User;
import com.dila.apprawat.ui.fragment.FragmentSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint( "SetTextI18n" )
public class UbahProfile extends AppCompatActivity {

    private ImageView btn_kembali;
    private EditText edt_t_lahir, edt_tgl_lahir, edt_kelamin, edt_gol_darah, edt_agama,
            edt_pekerjaan, edt_alamat, edt_nik;
    private LinearLayout btn_simpan;
    private ProgressBar progress;
    private TextView text_simpan, nama;
    private StringRequest simpanProfile;
    private String t_lahir, tgl_lahir, kelamin, gol_darah, agama, pekerjaan, alamat, nik, member_id;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profile);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        setInit();
        setDisplay();
        setButton();
    }

    private void setDisplay() {
        member_id = preferences.getString("member_id", "");
        nama.setText("Selamat Datang "+preferences.getString("nama", ""));
        edt_t_lahir.setText(preferences.getString("t_lahir", ""));
        edt_tgl_lahir.setText(preferences.getString("tgl_lahir", ""));
        edt_kelamin.setText(preferences.getString("kelamin", ""));
        edt_gol_darah.setText(preferences.getString("gol_darah", ""));
        edt_agama.setText(preferences.getString("agama", ""));
        edt_pekerjaan.setText(preferences.getString("pekerjaan", ""));
        edt_alamat.setText(preferences.getString("alamat", ""));
        edt_nik.setText("" + preferences.getInt("nik", 0));
    }

    private void setSimpanProfile() {
        progress.setVisibility(View.VISIBLE);
        text_simpan.setVisibility(View.GONE);
        simpanProfile = new StringRequest(Request.Method.POST, URLServer.PUTMEMBER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");

                    editor = preferences.edit();
                    editor.putInt("nik", data.getInt("nik"));
                    editor.putString("tgl_lahir", data.getString("tgl_lahir"));
                    editor.putString("kelamin", data.getString("kelamin"));
                    editor.putString("gol_darah", data.getString("gol_darah"));
                    editor.putString("agama", data.getString("agama"));
                    editor.putString("pekerjaan", data.getString("pekerjaan"));
                    editor.putString("alamat", data.getString("alamat"));
                    editor.putString("t_lahir", data.getString("t_lahir"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    showDialog();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.getMessage());
            }
            progress.setVisibility(View.GONE);
            text_simpan.setVisibility(View.VISIBLE);
        }, error -> {
            progress.setVisibility(View.GONE);
            text_simpan.setVisibility(View.VISIBLE);
            showError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("member_id", member_id);
                map.put("nik", nik);
                map.put("tgl_lahir", tgl_lahir);
                map.put("kelamin", kelamin);
                map.put("gol_darah", gol_darah);
                map.put("agama", agama);
                map.put("pekerjaan", pekerjaan);
                map.put("alamat", alamat);
                map.put("t_lahir", t_lahir);
                return map;
            }
        };
        networkError();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(simpanProfile);
    }

    private void networkError() {
        simpanProfile.setRetryPolicy(new RetryPolicy() {
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

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .show();
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        btn_simpan.setOnClickListener(v -> {
            if (validasi()) {
                setSimpanProfile();
            }
        });
    }

    private boolean validasi() {
        setInputText();
        if (nik.isEmpty()) {
            edt_nik.setError("Kolom NIK tidak boleh kosong!");
            return false;
        }
        if (t_lahir.isEmpty()) {
            edt_t_lahir.setError("Kolom tempat lahir tidak boleh kosong!");
            return false;
        }
        if (tgl_lahir.isEmpty()) {
            edt_tgl_lahir.setError("Kolom tanggal lahir tidak boleh kosong!");
            return false;
        }
        if (kelamin.isEmpty()) {
            edt_kelamin.setError("Kolom jenis kelamin tidak boleh kosong!");
            return false;
        }
        if (gol_darah.isEmpty()) {
            edt_gol_darah.setError("Kolom golongan darah tidak boleh kosong!");
            return false;
        }
        if (agama.isEmpty()) {
            edt_agama.setError("Kolom agama tidak boleh kosong!");
            return false;
        }
        if (pekerjaan.isEmpty()) {
            edt_pekerjaan.setError("Kolom pekerjaan tidak boleh kosong!");
            return false;
        }
        if (alamat.isEmpty()) {
            edt_alamat.setError("Kolom alamat tidak boleh kosong!");
            return false;
        }
        return true;
    }

    private void setInputText() {
        t_lahir = edt_t_lahir.getText().toString().trim();
        tgl_lahir = edt_tgl_lahir.getText().toString().trim();
        kelamin = edt_kelamin.getText().toString().trim();
        gol_darah = edt_gol_darah.getText().toString().trim();
        agama = edt_agama.getText().toString().trim();
        pekerjaan = edt_pekerjaan.getText().toString().trim();
        alamat = edt_alamat.getText().toString().trim();
        nik = edt_nik.getText().toString().trim();
    }

    private void setInit() {
        btn_kembali = findViewById(R.id.btn_kembali);
        edt_t_lahir = findViewById(R.id.t_lahir);
        edt_tgl_lahir = findViewById(R.id.tgl_lahir);
        edt_kelamin = findViewById(R.id.kelamin);
        edt_gol_darah = findViewById(R.id.gol_darah);
        edt_agama = findViewById(R.id.agama);
        edt_pekerjaan = findViewById(R.id.pekerjaan);
        edt_alamat = findViewById(R.id.alamat);
        btn_simpan = findViewById(R.id.btn_simpan);
        progress = findViewById(R.id.progress);
        text_simpan = findViewById(R.id.text_simpan);
        edt_nik = findViewById(R.id.nik);
        nama = findViewById(R.id.nama);
    }
}