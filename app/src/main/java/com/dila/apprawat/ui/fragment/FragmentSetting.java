package com.dila.apprawat.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dila.apprawat.R;
import com.dila.apprawat.network.api.URLServer;
import com.dila.apprawat.network.model.User;
import com.dila.apprawat.network.presentation.AdapterUser;
import com.dila.apprawat.ui.activity.BantuanActivity;
import com.dila.apprawat.ui.activity.LoginActivity;
import com.dila.apprawat.ui.activity.TentangActivity;
import com.dila.apprawat.ui.activity.UbahPasswod;
import com.dila.apprawat.ui.activity.UbahProfile;
import com.dila.apprawat.ui.activity.UploadImage;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentSetting extends Fragment {
    private View view;
    private TextView nama, phone, email, alamat;
    private RelativeLayout btn_ubahprofile, btn_ubahpassword, btn_bantuan, btn_tentang, btn_logout;
    private SharedPreferences preferences;
    private CircularImageView image;
    private StringRequest logout;

    public FragmentSetting() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        preferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        setInit();
        setDisplay();
        setButton();
        return view;
    }

    private void setLogout() {
        SweetAlertDialog pDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        logout = new StringRequest(Request.Method.GET, URLServer.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.remove(String.valueOf(preferences.getInt("id_regis", 0)));
                    editor.remove(preferences.getString("nama", ""));
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
            pDialog.dismiss();
        }, error -> {
            pDialog.dismiss();
            showError(error.toString());
        });
        logout.setRetryPolicy(new RetryPolicy() {
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
                    showError("Koneksi gagal!");
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(logout);
    }

    private void setButton() {
        btn_bantuan.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), BantuanActivity.class));
        });
        btn_tentang.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TentangActivity.class));
        });
        btn_ubahprofile.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), UbahProfile.class));
            requireActivity().finish();
        });
        btn_ubahpassword.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), UbahPasswod.class));
        });
        btn_logout.setOnClickListener(v -> {
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setCancelButton("No", SweetAlertDialog::dismissWithAnimation)
                    .setConfirmText("Yes")
                    .setConfirmClickListener(sDialog -> {
                        setLogout();
                        sDialog.dismissWithAnimation();
                    }).show();
        });

        image.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), UploadImage.class));
        });
    }

    private void setDisplay() {
        nama.setText(preferences.getString("nama", ""));
        phone.setText(preferences.getString("no_hp", ""));
        email.setText(preferences.getString("email", ""));
        alamat.setText(preferences.getString("alamat", ""));
    }

    private void setImage() {
        int id = preferences.getInt("id_m", 0);
        logout = new StringRequest(Request.Method.GET, URLServer.GETGAMBAR + id, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONObject data = object.getJSONObject("data");
                        Picasso.get()
                                .load(URLServer.IMAGE + data.getString("image"))
                                .placeholder(R.drawable.ic_user2)
                                .error(R.drawable.ic_user2)
                                .into(image);
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
            } else {
                showError(null);
            }
        }, error -> {
            Log.d("respon", "err: " + error.networkResponse);
        });
        setNetworkError();
        RequestQueue koneksi = Volley.newRequestQueue(requireActivity());
        koneksi.add(logout);
    }

    private void showError(String string) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setNetworkError() {
        logout.setRetryPolicy(new RetryPolicy() {
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
                    showError("Koneksi gagal!");
                }
            }
        });
    }

    private void setInit() {
        nama = view.findViewById(R.id.nama);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        alamat = view.findViewById(R.id.alamat);
        btn_ubahprofile = view.findViewById(R.id.btn_ubahprofile);
        btn_ubahpassword = view.findViewById(R.id.btn_ubahpassword);
        btn_bantuan = view.findViewById(R.id.btn_bantuan);
        btn_tentang = view.findViewById(R.id.btn_tentang);
        btn_logout = view.findViewById(R.id.btn_logout);
        image = view.findViewById(R.id.image);
    }

    @Override
    public void onResume() {
        setImage();
        super.onResume();
    }
}
