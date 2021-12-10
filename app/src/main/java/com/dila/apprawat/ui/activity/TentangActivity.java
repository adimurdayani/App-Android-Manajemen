package com.dila.apprawat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.dila.apprawat.R;

public class TentangActivity extends AppCompatActivity {
    private ImageView btn_kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}