package com.dila.apprawat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
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

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailRawatJalan extends AppCompatActivity {
    private TextView no_rekam, nama, umur, kelamin, phone,
            tgl_berobat, pekerjaan, alamat, penyakit, ruangan, keterangan,
            phone_p, pekerjaan_p, alamt_p, nama_p;
    private ImageView btn_kembali;
    private StringRequest getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rawat_jalan);
        setInit();
    }

    private void setData() {
        int id = getIntent().getIntExtra("id", 0);
        getData = new StringRequest(Request.Method.GET, URLServer.GETRAWATJALANID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    no_rekam.setText(data.getString("no_rekam_jalan"));
                    nama.setText(data.getString("nama_pasien"));
                    umur.setText(data.getString("umur"));
                    kelamin.setText(data.getString("kelamin"));
                    phone.setText(data.getString("phone"));
                    tgl_berobat.setText(data.getString("tgl_berobat"));
                    pekerjaan.setText(data.getString("pekerjaan"));
                    alamat.setText(data.getString("alamat"));
                    penyakit.setText(data.getString("nama_penyakit"));
                    ruangan.setText(data.getString("nama_ruangan"));
                    keterangan.setText(data.getString("keterangan"));
                    phone_p.setText(data.getString("phone_p_jawab"));
                    pekerjaan_p.setText(data.getString("pekerjaan_p_jawab"));
                    alamt_p.setText(data.getString("a_p_jawab"));
                    nama_p.setText(data.getString("p_jawab"));
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
        umur = findViewById(R.id.umur);
        kelamin = findViewById(R.id.kelamin);
        phone = findViewById(R.id.phone);
        tgl_berobat = findViewById(R.id.tgl_berobat);
        pekerjaan = findViewById(R.id.pekerjaan);
        alamat = findViewById(R.id.alamat);
        penyakit = findViewById(R.id.penyakit);
        ruangan = findViewById(R.id.ruangan);
        keterangan = findViewById(R.id.keterangan);
        phone_p = findViewById(R.id.phone_p);
        pekerjaan_p = findViewById(R.id.pekerjaan_p);
        alamt_p = findViewById(R.id.alamt_p);
        nama_p = findViewById(R.id.nama_p);
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