package com.dila.apprawat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dila.apprawat.R;
import com.dila.apprawat.ui.fragment.FragmentLogin;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frm_login, new FragmentLogin())
                .commit();
    }
}