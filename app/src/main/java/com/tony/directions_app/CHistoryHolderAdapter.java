package com.tony.directions_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tony.directions_app.Models.Model;

import java.util.ArrayList;

public class CHistoryHolderAdapter extends RecyclerView.Adapter<CHistoryHolderAdapter.MyViewHolder> {

    ArrayList<Model> mList;
    Context context;

    public CHistoryHolderAdapter(Context context, ArrayList<Model> mList) {

        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_history, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        holder.Csource.setText(model.getCCurrentLocality());
        holder.Cdestination.setText(model.getCDestinationName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Csource, Cdestination;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Csource = itemView.findViewById(R.id.tv_from);
            Cdestination = itemView.findViewById(R.id.tv_to);
        }
    }
}
