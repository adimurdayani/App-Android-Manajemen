package com.dila.apprawat.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dila.apprawat.R;
import com.dila.apprawat.network.api.URLServer;
import com.dila.apprawat.network.model.RawatJalan;
import com.dila.apprawat.network.presentation.AdapterRawatJalan;
import com.dila.apprawat.ui.activity.AddRawatJalan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentRawatJalan extends Fragment {
    private View view;
    private SearchView search;
    private SwipeRefreshLayout sw_data;
    private RecyclerView rc_data;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getData;
    private ArrayList<RawatJalan> rawatJalans;
    private AdapterRawatJalan adapter;
    private LinearLayout btn_add;

    public FragmentRawatJalan() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rawatjalan, container, false);
        setInit();
        setDisplay();
        return view;
    }

    private void setGetData() {
        rawatJalans = new ArrayList<>();
        sw_data.setRefreshing(true);
        getData = new StringRequest(Request.Method.GET, URLServer.GETRAWATJALAN, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject getData = data.getJSONObject(i);
                            RawatJalan getrawatjalan = new RawatJalan();
                            getrawatjalan.setId(getData.getInt("id"));
                            getrawatjalan.setNama_pasien(getData.getString("nama_pasien"));
                            getrawatjalan.setNo_rekam_jalan(getData.getString("no_rekam_jalan"));
                            getrawatjalan.setTgl_berobat(getData.getString("tgl_berobat"));
                            rawatJalans.add(getrawatjalan);
                        }
                        adapter = new AdapterRawatJalan(getContext(), rawatJalans);
                        rc_data.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
                        rc_data.setAdapter(adapter);
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
                sw_data.setRefreshing(false);
            } else {
                showError(null);
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("respon", "err: " + error.networkResponse);
        });
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
                    showError("Koneksi gagal!");

                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireActivity());
        koneksi.add(getData);
    }

    private void showError(String string) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setDisplay() {
        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        sw_data.setOnRefreshListener(this::setGetData);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getSearchData().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setGetData();
                return false;
            }
        });

        btn_add.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AddRawatJalan.class));
        });
    }

    private void setInit() {
        search = view.findViewById(R.id.search);
        sw_data = view.findViewById(R.id.sw_data);
        rc_data = view.findViewById(R.id.rc_data);
        btn_add = view.findViewById(R.id.btn_add);
    }

    @Override
    public void onResume() {
        setGetData();
        super.onResume();
    }
}
