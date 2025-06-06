package com.example.mi_zan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NextPrayAdapter extends RecyclerView.Adapter<NextPrayAdapter.ViewHolder> {
    private String[] waktuList;

    public NextPrayAdapter(String[] waktuList) {
        this.waktuList = waktuList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_next_sholat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(waktuList[position]);
    }

    @Override
    public int getItemCount() {
        return waktuList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.prayer_text);
        }
    }
}