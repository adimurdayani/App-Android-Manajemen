package com.dila.apprawat.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.dila.apprawat.network.model.Penyakit;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddRawatJalan extends AppCompatActivity {
    private TextView nama, text_kirim;
    private EditText edt_nama_pasien, edt_phone, edt_umur, edt_pekerjaan, edt_tgl_berobat, edt_p_jawab,
            edt_a_p_jawab, edt_phone_p_jawab, edt_pekerjaan_p_jawab, edt_alamat, edt_keterangan;
    private ProgressBar progress;
    private Spinner edt_Kelamin;
    private SearchableSpinner nama_penyakit;
    private LinearLayout btn_kirim;
    private SharedPreferences preferences;
    private StringRequest kirimData;
    private String nama_pasien, Kelamin, phone, umur, pekerjaan, tgl_berobat,
            p_jawab, a_p_jawab, phone_p_jawab, pekerjaan_p_jawab, alamat, keterangan, user_id, id_penyakit;
    private ImageView btn_kembali;
    ArrayList<Penyakit> listpenyakit;
    private ArrayList<String> list;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rawat_jalan);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        setInit();
        setDisplay();
        setButton();
        setDate();
        setSpinner();
    }

    private void getPenyakit() {
        listpenyakit = new ArrayList<>();
        list = new ArrayList<>();
        kirimData = new StringRequest(Request.Method.GET, URLServer.GETPENYAKIT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    list = new ArrayList<>();
                    JSONArray dataArray = new JSONArray(object.getString("data"));
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject data = dataArray.getJSONObject(i);
                        Penyakit getPenyakit = new Penyakit();
                        getPenyakit.setId(data.getInt("id"));
                        getPenyakit.setNama_penyakit(data.getString("nama_penyakit"));
                        listpenyakit.add(getPenyakit);
                    }
                    for (int i = 0; i < listpenyakit.size(); i++) {
                        list.add(listpenyakit.get(i).getNama_penyakit());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
                    nama_penyakit.setAdapter(adapter);
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
        koneksi.add(kirimData);
    }

    private void setDate() {
        edt_tgl_berobat.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    edt_tgl_berobat.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    private void setSpinner() {
        edt_Kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelamin = edt_Kelamin.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nama_penyakit.setTitle("Pilih Penyakit");
        nama_penyakit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_penyakit = String.valueOf(listpenyakit.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_kirim.setOnClickListener(v -> {
            if (validasi()) {
                setKirimData();
            }
        });
    }

    private void setKirimData() {
        progress.setVisibility(View.VISIBLE);
        text_kirim.setVisibility(View.GONE);
        kirimData = new StringRequest(Request.Method.POST, URLServer.GETRAWATJALAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    showDialog();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showError(e.toString());
            }
            progress.setVisibility(View.GONE);
            text_kirim.setVisibility(View.VISIBLE);
        }, error -> {
            progress.setVisibility(View.GONE);
            text_kirim.setVisibility(View.VISIBLE);
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                user_id = preferences.getString("member_id", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("nama_pasien", nama_pasien);
                map.put("kelamin", Kelamin);
                map.put("pekerjaan", pekerjaan);
                map.put("alamat", alamat);
                map.put("umur", umur);
                map.put("phone", phone);
                map.put("tgl_berobat", tgl_berobat);
                map.put("p_jawab", p_jawab);
                map.put("a_p_jawab", a_p_jawab);
                map.put("phone_p_jawab", phone_p_jawab);
                map.put("pekerjaan_p_jawab", pekerjaan_p_jawab);
                map.put("keterangan", keterangan);
                map.put("penyakit_id", id_penyakit);
                map.put("user_id", user_id);
                return map;
            }
        };
        setErrorNetwork();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirimData);
    }

    private boolean validasi() {
        setTextInput();
        if (nama_pasien.isEmpty()) {
            edt_nama_pasien.setError("Kolom nama pasien tidak boleh kosong!");
            return false;
        }
        if (phone.isEmpty()) {
            edt_phone.setError("Kolom phone tidak boleh kosong!");
            return false;
        }
        if (umur.isEmpty()) {
            edt_umur.setError("Kolom umur pasien tidak boleh kosong!");
            return false;
        }
        if (pekerjaan.isEmpty()) {
            edt_pekerjaan.setError("Kolom pekerjaan pasien tidak boleh kosong!");
            return false;
        }
        if (tgl_berobat.isEmpty()) {
            edt_tgl_berobat.setError("Kolom tanggal berobat pasien tidak boleh kosong!");
            return false;
        }
        if (p_jawab.isEmpty()) {
            edt_p_jawab.setError("Kolom nama penanggung jawab pasien tidak boleh kosong!");
            return false;
        }
        if (a_p_jawab.isEmpty()) {
            edt_a_p_jawab.setError("Kolom alamat penanggung jawab pasien tidak boleh kosong!");
            return false;
        }
        if (phone_p_jawab.isEmpty()) {
            edt_phone_p_jawab.setError("Kolom phone penanggung jawab pasien tidak boleh kosong!");
            return false;
        }
        if (pekerjaan_p_jawab.isEmpty()) {
            edt_pekerjaan_p_jawab.setError("Kolom pekerjaan penanggung jawab tidak boleh kosong!");
            return false;
        }
        if (alamat.isEmpty()) {
            edt_alamat.setError("Kolom alamat tidak boleh kosong!");
            return false;
        }
        if (keterangan.isEmpty()) {
            edt_keterangan.setError("Kolom keterangan tidak boleh kosong!");
            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void setDisplay() {
        nama.setText("Hallo " + preferences.getString("nama", ""));
    }

    private void setTextInput() {
        nama_pasien = edt_nama_pasien.getText().toString().trim();
        phone = edt_phone.getText().toString().trim();
        umur = edt_umur.getText().toString().trim();
        pekerjaan = edt_pekerjaan.getText().toString().trim();
        tgl_berobat = edt_tgl_berobat.getText().toString().trim();
        p_jawab = edt_p_jawab.getText().toString().trim();
        a_p_jawab = edt_a_p_jawab.getText().toString().trim();
        phone_p_jawab = edt_phone_p_jawab.getText().toString().trim();
        pekerjaan_p_jawab = edt_pekerjaan_p_jawab.getText().toString().trim();
        alamat = edt_alamat.getText().toString().trim();
        keterangan = edt_keterangan.getText().toString().trim();
    }

    private void setInit() {
        edt_nama_pasien = findViewById(R.id.nama_pasien);
        edt_Kelamin = findViewById(R.id.Kelamin);
        edt_phone = findViewById(R.id.phone);
        edt_umur = findViewById(R.id.umur);
        edt_pekerjaan = findViewById(R.id.pekerjaan);
        edt_tgl_berobat = findViewById(R.id.tgl_berobat);
        edt_p_jawab = findViewById(R.id.p_jawab);
        edt_a_p_jawab = findViewById(R.id.a_p_jawab);
        edt_phone_p_jawab = findViewById(R.id.phone_p_jawab);
        edt_pekerjaan_p_jawab = findViewById(R.id.pekerjaan_p_jawab);
        edt_alamat = findViewById(R.id.alamat);
        edt_keterangan = findViewById(R.id.keterangan);
        nama = findViewById(R.id.nama);
        text_kirim = findViewById(R.id.text_kirim);
        progress = findViewById(R.id.progress);
        btn_kirim = findViewById(R.id.btn_kirim);
        btn_kembali = findViewById(R.id.btn_kembali);
        nama_penyakit = findViewById(R.id.nama_penyakit);
    }

    private void setErrorNetwork() {
        kirimData.setRetryPolicy(new RetryPolicy() {
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

    @Override
    protected void onResume() {
        super.onResume();
        getPenyakit();
    }
}