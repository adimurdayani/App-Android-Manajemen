package com.dila.apprawat.network.presentation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dila.apprawat.R;
import com.dila.apprawat.network.model.RawatInap;
import com.dila.apprawat.network.model.RawatJalan;
import com.dila.apprawat.ui.activity.DetailRawatInap;
import com.google.android.material.transition.Hold;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class AdapterRawatInap extends RecyclerView.Adapter<AdapterRawatInap.HolderData> {
    private Context context;
    private ArrayList<RawatInap> rawatInaps;

    public AdapterRawatInap(Context context, ArrayList<RawatInap> rawatInaps) {
        this.context = context;
        this.rawatInaps = rawatInaps;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_rawatinap, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        RawatInap inap = rawatInaps.get(position);
        holder.nama.setText(inap.getNama_pasien());
        holder.norekam.setText(inap.getNo_rekam_inap());

        String tglLama = inap.getTgl_masuk();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String tglBaru = dateFormat.format(df.parse(tglLama));
            holder.tgl_masuk.setText(tglBaru);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.layout.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailRawatInap.class);
            i.putExtra("id", inap.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return rawatInaps.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RawatInap> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(rawatInaps);
            } else {
                for (RawatInap getRekamMedik : rawatInaps) {
                    if (getRekamMedik.getNama_pasien().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getNo_rekam_inap().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getTgl_masuk().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(getRekamMedik);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            rawatInaps.clear();
            rawatInaps.addAll((Collection<? extends RawatInap>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {
        private TextView norekam, nama, tgl_masuk;
        private LinearLayout layout;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            norekam = itemView.findViewById(R.id.norekam);
            nama = itemView.findViewById(R.id.nama);
            tgl_masuk = itemView.findViewById(R.id.tgl_masuk);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
