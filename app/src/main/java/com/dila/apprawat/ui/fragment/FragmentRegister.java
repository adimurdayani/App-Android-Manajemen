package com.dila.apprawat.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.dila.apprawat.ui.activity.UbahProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentRegister extends Fragment {
    private View view;
    private ImageView btn_kembali;
    private EditText edt_nama, edt_email, edt_phone, edt_username, edt_password, edt_konfirmasi_password;
    private LinearLayout btn_registrasi;
    private ProgressBar progress;
    private TextView text_registrasi;
    private StringRequest registrasiUser;
    private String nama, email, phone, username, password, konfirmasi_password;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public FragmentRegister() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        setInit();
        setButton();
        return view;
    }

    private void setRegistrasiUser() {
        progress.setVisibility(View.VISIBLE);
        text_registrasi.setVisibility(View.GONE);
        registrasiUser = new StringRequest(Request.Method.POST, URLServer.REGISTER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    User postUser = new User();
                    postUser.setNama(data.getString("nama"));
                    postUser.setUsername(data.getString("username"));
                    postUser.setEmail(data.getString("email"));
                    postUser.setNo_hp(data.getString("no_hp"));

                    preferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("member_id", data.getString("member_id"));
                    editor.putString("nama", data.getString("nama"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(requireActivity(), UbahProfile.class));
                    requireActivity().finish();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.getMessage());
            }
            progress.setVisibility(View.GONE);
            text_registrasi.setVisibility(View.VISIBLE);
        }, error -> {
            progress.setVisibility(View.GONE);
            text_registrasi.setVisibility(View.VISIBLE);
            showError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("nama", nama);
                map.put("email", email);
                map.put("username", username);
                map.put("password", password);
                map.put("no_hp", phone);
                return map;
            }
        };
        networkError();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(registrasiUser);
    }

    private void networkError() {
        registrasiUser.setRetryPolicy(new RetryPolicy() {
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
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setInputText() {
        nama = edt_nama.getText().toString().trim();
        email = edt_email.getText().toString().trim();
        phone = edt_phone.getText().toString().trim();
        username = edt_username.getText().toString().trim();
        password = edt_password.getText().toString().trim();
        konfirmasi_password = edt_konfirmasi_password.getText().toString().trim();
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            manager.beginTransaction()
                    .replace(R.id.frm_login, new FragmentLogin())
                    .commit();
        });
        btn_registrasi.setOnClickListener(v -> {
            if (validasi()) {
                setRegistrasiUser();
            }
        });
    }

    private boolean validasi() {
        setInputText();
        if (nama.isEmpty()) {
            edt_nama.setError("Kolom nama tidak boleh kosong!");
            return false;
        }
        if (email.isEmpty()) {
            edt_email.setError("Kolom email tidak boleh kosong!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_email.setError("Format email salah!. Contoh: gunakan @example.com");
            return false;
        }
        if (phone.isEmpty()) {
            edt_phone.setError("Kolom phone tidak boleh kosong!");
            return false;
        }
        if (username.isEmpty()) {
            edt_username.setError("Kolom username tidak boleh kosong!");
            return false;
        }
        if (password.isEmpty()) {
            edt_password.setError("Kolom password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            edt_password.setError("Kolom password tidak kurang dari 6 karakter!");
        }
        if (konfirmasi_password.isEmpty()) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak boleh kosong!");
            return false;
        } else if (konfirmasi_password.length() < 6) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak kurang dari 6 karakter!");
            return false;
        } else if (!konfirmasi_password.matches(password)) {
            edt_konfirmasi_password.setError("Kolom konfirmasi password tidak sama dengan password!");
        }
        return true;
    }

    private void setInit() {
        btn_kembali = view.findViewById(R.id.btn_kembali);
        edt_nama = view.findViewById(R.id.nama);
        edt_email = view.findViewById(R.id.email);
        edt_phone = view.findViewById(R.id.phone);
        edt_username = view.findViewById(R.id.username);
        edt_password = view.findViewById(R.id.password);
        edt_konfirmasi_password = view.findViewById(R.id.konfirmasi_password);
        btn_registrasi = view.findViewById(R.id.btn_registrasi);
        progress = view.findViewById(R.id.progress);
        text_registrasi = view.findViewById(R.id.text_registrasi);
    }
}
