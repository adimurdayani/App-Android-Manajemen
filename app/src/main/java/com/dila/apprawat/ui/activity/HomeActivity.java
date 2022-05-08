package com.dila.apprawat.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
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
import com.dila.apprawat.ui.fragment.FragmentHome;
import com.dila.apprawat.ui.fragment.FragmentLogin;
import com.dila.apprawat.ui.fragment.FragmentRawatInap;
import com.dila.apprawat.ui.fragment.FragmentRawatJalan;
import com.dila.apprawat.ui.fragment.FragmentSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint( "NonConstantResourceId" )
public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView btn_navigasi;
    private TextView nama;
    private CircularImageView img_user;
    private SharedPreferences preferences;
    private StringRequest setImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama =  findViewById(R.id.nama);
        img_user =  findViewById(R.id.img_user);
        nama.setText(preferences.getString("nama", ""));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frm_home, new FragmentHome())
                .commit();

        btn_navigasi = findViewById(R.id.btn_navigasi);
        BottomNavigationView.OnNavigationItemSelectedListener navigasi =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new FragmentHome();
                        break;
                    case R.id.rawat_jalan:
                        fragment = new FragmentRawatJalan();
                        break;
                    case R.id.rawat_inap:
                        fragment = new FragmentRawatInap();
                        break;
                    case R.id.setting:
                        fragment = new FragmentSetting();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frm_home, fragment).commit();
                return true;
            }
        };
        btn_navigasi.setOnNavigationItemSelectedListener(navigasi);
        setImage();
    }

    private void setImage() {
        int id = preferences.getInt("id_m", 0);
        setImg = new StringRequest(Request.Method.GET, URLServer.GETGAMBAR + id, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONObject data = object.getJSONObject("data");
                        Picasso.get()
                                .load(URLServer.IMAGE + data.getString("image"))
                                .error(R.drawable.ic_user)
                                .placeholder(R.drawable.ic_user)
                                .into(img_user);
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
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(setImg);
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setNetworkError() {
        setImg.setRetryPolicy(new RetryPolicy() {
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
}