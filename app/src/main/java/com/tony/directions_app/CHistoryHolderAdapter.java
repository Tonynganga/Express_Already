package com.tony.directions_app;

import android.content.Context;
import android.content.Intent;
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
        holder.Cdistance.setText(model.getCDistance());
        holder.CDate.setText(model.getCDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("Csource", model.getCCurrentLocality());
                intent.putExtra("Cdestination", model.getCDestinationName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Csource, Cdestination, Cdistance, CDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Csource = itemView.findViewById(R.id.tv_from);
            Cdestination = itemView.findViewById(R.id.tv_to);
            Cdistance = itemView.findViewById(R.id.tv_distance);
            CDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
