package com.dila.apprawat.ui.fragment;

import android.graphics.Color;
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
import com.dila.apprawat.network.model.RawatInap;
import com.dila.apprawat.network.model.SliderItem;
import com.dila.apprawat.network.model.User;
import com.dila.apprawat.network.presentation.AdapterRawatInap;
import com.dila.apprawat.network.presentation.AdapterUser;
import com.dila.apprawat.network.presentation.SliderAdapterExample;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentHome extends Fragment {
    private View view;
    SliderView sliderView;
    private SliderAdapterExample adapter;
    private SearchView search;
    private SwipeRefreshLayout sw_data;
    private RecyclerView rc_data;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getData;
    private AdapterUser adapterUser;
    private ArrayList<User> users;

    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setInit();
        setDisplay();
        return view;
    }
    private void setDisplay() {
        adapter = new SliderAdapterExample(requireActivity());

        List<SliderItem> sliderItemList = new ArrayList<>();
        //dummy data
        for (int i = 0; i < 5; i++) {
            SliderItem sliderItem = new SliderItem();
            if (i % 2 == 0) {
                sliderItem.setImageUrl(R.drawable.slide1);
            } else {
                sliderItem.setImageUrl(R.drawable.slide2);
            }
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);

        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });

        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        sw_data.setOnRefreshListener(this::setGetData);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterUser.getSearchData().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setGetData();
                return false;
            }
        });
    }

    private void setGetData() {
        users = new ArrayList<>();
        sw_data.setRefreshing(true);
        getData = new StringRequest(Request.Method.GET, URLServer.PUTMEMBER, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject getData = data.getJSONObject(i);
                            User getuser = new User();
                            getuser.setId_m(getData.getInt("id_m"));
                            getuser.setNama(getData.getString("nama"));
                            getuser.setMember_id(getData.getString("member_id"));
                            getuser.setNo_hp(getData.getString("no_hp"));
                            users.add(getuser);
                        }
                        adapterUser = new AdapterUser(getContext(), users);
                        rc_data.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
                        rc_data.setAdapter(adapterUser);
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
            } else {
                showError(null);
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("respon", "err: " + error.networkResponse);
        });
        setNetworkError();
        RequestQueue koneksi = Volley.newRequestQueue(requireActivity());
        koneksi.add(getData);
    }

    private void showError(String string) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setNetworkError(){
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
    }

    private void setInit() {
        sliderView = view.findViewById(R.id.imageSlider);
        search = view.findViewById(R.id.search);
        sw_data = view.findViewById(R.id.sw_data);
        rc_data = view.findViewById(R.id.rc_data);
    }

    @Override
    public void onResume() {
        setGetData();
        super.onResume();
    }
}
