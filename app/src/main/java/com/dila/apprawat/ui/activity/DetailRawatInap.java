package com.dila.apprawat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dila.apprawat.R;
import com.dila.apprawat.network.api.URLServer;
import com.dila.apprawat.network.model.Penyakit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailRawatInap extends AppCompatActivity {
    private TextView no_rekam, nama, kelamin, phone, pekerjaan, alamat, penyakit,
            ruangan, nama_p, pekerjaan_p,tgl_berobat,umur;
    private ImageView btn_kembali;
    private StringRequest getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rawat_inap);
        setInit();
    }

    private void setData() {
        int id = getIntent().getIntExtra("id", 0);
        getData = new StringRequest(Request.Method.GET, URLServer.GETRAWATINAPID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    no_rekam.setText(data.getString("no_rekam_inap"));
                    nama.setText(data.getString("nama_pasien"));
                    kelamin.setText(data.getString("kelamin"));
                    phone.setText(data.getString("phone"));
                    pekerjaan.setText(data.getString("pekerjaan"));
                    alamat.setText(data.getString("alamat"));
                    tgl_berobat.setText(data.getString("tgl_masuk"));
                    umur.setText(data.getString("umur"));
                    penyakit.setText(data.getString("nama_penyakit"));
                    ruangan.setText(data.getString("nama_ruangan"));
                    nama_p.setText(data.getString("p_jawab"));
                    pekerjaan_p.setText(data.getString("pekerjaan_p_jawab"));
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showError(e.toString());
            }
        }, error -> {
            showError(error.toString());
        });
        setErrorNetwork();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getData);
    }

    private void setErrorNetwork() {
        getData.setRetryPolicy(new RetryPolicy() {
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
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setInit() {
        no_rekam = findViewById(R.id.no_rekam);
        nama = findViewById(R.id.nama);
        kelamin = findViewById(R.id.kelamin);
        phone = findViewById(R.id.phone);
        pekerjaan = findViewById(R.id.pekerjaan);
        alamat = findViewById(R.id.alamat);
        penyakit = findViewById(R.id.penyakit);
        ruangan = findViewById(R.id.ruangan);
        nama_p = findViewById(R.id.nama_p);
        umur = findViewById(R.id.umur);
        pekerjaan_p = findViewById(R.id.pekerjaan_p);
        tgl_berobat = findViewById(R.id.tgl_berobat);
        btn_kembali = findViewById(R.id.btn_kembali);

        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
}