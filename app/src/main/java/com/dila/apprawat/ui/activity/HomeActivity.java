package com.dila.apprawat.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dila.apprawat.R;
import com.dila.apprawat.ui.fragment.FragmentHome;
import com.dila.apprawat.ui.fragment.FragmentLogin;
import com.dila.apprawat.ui.fragment.FragmentRawatInap;
import com.dila.apprawat.ui.fragment.FragmentRawatJalan;
import com.dila.apprawat.ui.fragment.FragmentSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

@SuppressLint( "NonConstantResourceId" )
public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView btn_navigasi;
    private TextView nama;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama =  findViewById(R.id.nama);
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
    }
}