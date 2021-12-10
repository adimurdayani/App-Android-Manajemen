package com.dila.apprawat.network.presentation;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dila.apprawat.R;
import com.dila.apprawat.network.model.RawatInap;
import com.dila.apprawat.network.model.RawatJalan;
import com.dila.apprawat.network.model.User;

import java.util.ArrayList;
import java.util.Collection;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.HolderData> {
    private Context context;
    private ArrayList<User> users;

    public AdapterUser(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        User datauser = users.get(position);
        holder.nama.setText(datauser.getNama());
        holder.nomember.setText(datauser.getMember_id());
        holder.nohp.setText(datauser.getNo_hp());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(users);
            } else {
                for (User getuser : users) {
                    if (getuser.getNama().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getuser.getNo_hp().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getuser.getMember_id().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(getuser);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((Collection<? extends User>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {
        private TextView nomember, nama, nohp;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            nomember = itemView.findViewById(R.id.nomember);
            nama = itemView.findViewById(R.id.nama);
            nohp = itemView.findViewById(R.id.nohp);
        }
    }
}
