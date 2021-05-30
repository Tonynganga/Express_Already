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

public class PHistoryHolderAdapter extends RecyclerView.Adapter<PHistoryHolderAdapter.MyViewHolder> {

    ArrayList<Model> mList;
    Context context;

    public PHistoryHolderAdapter(Context context, ArrayList<Model> mList) {

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
        holder.Psource.setText(model.getPSourceName());
        holder.Pdestination.setText(model.getPDestinationName());
        holder.Pdistance.setText(model.getPDistance());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Psource, Pdestination, Pdistance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Psource = itemView.findViewById(R.id.tv_from);
            Pdestination = itemView.findViewById(R.id.tv_to);
            Pdistance = itemView.findViewById(R.id.tv_distance);
        }
    }
}
