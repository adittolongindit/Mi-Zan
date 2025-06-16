package com.example.mi_zan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mi_zan.R;
import com.example.mi_zan.model.LokasiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KotaAdapter extends RecyclerView.Adapter<KotaAdapter.ViewHolder> {

    private final List<LokasiItem> cityList;
    private final List<LokasiItem> cityListOriginal;
    private final OnCityClickListener listener;

    public interface OnCityClickListener {
        void onCityClick(LokasiItem kota);
    }

    public KotaAdapter(List<LokasiItem> cityList, OnCityClickListener listener) {
        this.cityList = new ArrayList<>(cityList);
        this.cityListOriginal = new ArrayList<>(cityList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LokasiItem city = cityList.get(position);
        holder.textView.setText(city.getLokasi());
        holder.itemView.setOnClickListener(v -> listener.onCityClick(city));
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void filter(String query) {
        cityList.clear();
        if (query.isEmpty()) {
            cityList.addAll(cityListOriginal);
        } else {
            List<LokasiItem> filteredList = cityListOriginal.stream()
                    .filter(item -> item.getLokasi().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            cityList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }
    public void updateData(List<LokasiItem> newCityList) {
        cityList.clear();
        cityList.addAll(newCityList);
        cityListOriginal.clear();
        cityListOriginal.addAll(newCityList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}