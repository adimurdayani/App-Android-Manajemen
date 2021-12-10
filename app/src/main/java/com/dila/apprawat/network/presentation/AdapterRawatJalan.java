package com.dila.apprawat.network.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dila.apprawat.R;
import com.dila.apprawat.network.model.RawatJalan;

import java.util.ArrayList;
import java.util.Collection;

public class AdapterRawatJalan extends RecyclerView.Adapter<AdapterRawatJalan.HolderData> {
    private Context context;
    private ArrayList<RawatJalan> rawatJalans;

    public AdapterRawatJalan(Context context, ArrayList<RawatJalan> rawatJalans) {
        this.context = context;
        this.rawatJalans = rawatJalans;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_rawatjalan, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        RawatJalan jalan = rawatJalans.get(position);
        holder.nama.setText(jalan.getNama_pasien());
        holder.norekam.setText(jalan.getNo_rekam_jalan());
        holder.tgl_berobat.setText(jalan.getTgl_berobat());
    }

    @Override
    public int getItemCount() {
        return rawatJalans.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RawatJalan> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(rawatJalans);
            } else {
                for (RawatJalan getRekamMedik : rawatJalans) {
                    if (getRekamMedik.getNama_pasien().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getNo_rekam_jalan().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getTgl_berobat().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            rawatJalans.clear();
            rawatJalans.addAll((Collection<? extends RawatJalan>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {
        private TextView norekam, nama, tgl_berobat;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            norekam = itemView.findViewById(R.id.norekam);
            nama = itemView.findViewById(R.id.nama);
            tgl_berobat = itemView.findViewById(R.id.tgl_berobat);
        }
    }
}
