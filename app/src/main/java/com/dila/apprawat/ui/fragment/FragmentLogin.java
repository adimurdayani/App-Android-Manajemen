package com.dila.apprawat.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.dila.apprawat.ui.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentLogin extends Fragment {
    private View view;
    private EditText edt_username, edt_password;
    private LinearLayout btn_login;
    private ProgressBar progress;
    private TextView text_login, btn_registrasi;
    private String username, password;
    private StringRequest loginUser;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public FragmentLogin() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        setInit();
        setButton();
        return view;
    }

    private void setButton() {
        btn_login.setOnClickListener(v -> {
            if (validasi()) {
                setLoginUser();
            }
        });
        btn_registrasi.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            manager.beginTransaction()
                    .replace(R.id.frm_login, new FragmentRegister())
                    .commit();
        });
    }

    private void setLoginUser() {
        progress.setVisibility(View.VISIBLE);
        text_login.setVisibility(View.GONE);
        loginUser = new StringRequest(Request.Method.POST, URLServer.LOGIN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    preferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putInt("id_m", data.getInt("id_m"));
                    editor.putString("nama", data.getString("nama"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putString("member_id", data.getString("member_id"));
                    editor.putInt("nik", data.getInt("nik"));
                    editor.putString("kelamin", data.getString("kelamin"));
                    editor.putString("gol_darah", data.getString("gol_darah"));
                    editor.putString("agama", data.getString("agama"));
                    editor.putString("pekerjaan", data.getString("pekerjaan"));
                    editor.putString("alamat", data.getString("alamat"));
                    editor.putString("t_lahir", data.getString("t_lahir"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    startActivity(new Intent(requireActivity(), HomeActivity.class));
                    requireActivity().finish();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.getMessage());
            }
            progress.setVisibility(View.GONE);
            text_login.setVisibility(View.VISIBLE);
        }, error -> {
            progress.setVisibility(View.GONE);
            text_login.setVisibility(View.VISIBLE);
            showError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        networkError();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(loginUser);
    }

    private void networkError() {
        loginUser.setRetryPolicy(new RetryPolicy() {
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

    private boolean validasi() {
        setInputText();
        if (username.isEmpty()) {
            edt_username.setError("Kolom username tidak boleh kosong!");
            return false;
        }
        if (password.isEmpty()) {
            edt_password.setError("Kolom password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            edt_password.setError("Kolom password tidak boleh kurang dari 6 karakter!");
            return false;
        }
        return true;
    }

    private void setInputText() {
        username = edt_username.getText().toString().trim();
        password = edt_password.getText().toString().trim();
    }

    private void setInit() {
        edt_username = view.findViewById(R.id.username);
        edt_password = view.findViewById(R.id.password);
        btn_login = view.findViewById(R.id.btn_login);
        progress = view.findViewById(R.id.progress);
        text_login = view.findViewById(R.id.text_login);
        btn_registrasi = view.findViewById(R.id.btn_registrasi);
    }
}
