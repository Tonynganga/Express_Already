package com.tony.directions_app;


import android.app.Activity;
import android.content.Intent;
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
    private  Activity context;


    public PHistoryHolderAdapter(Activity context, ArrayList<Model> mList) {

        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_history, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        holder.Psource.setText(model.getPSourceName());
        holder.Pdestination.setText(model.getPDestinationName());
        holder.Pdistance.setText(model.getPDistance());
        holder.Pdate.setText(model.getPDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("psource", mList.get(position).getPSourceName());
                intent.putExtra("pdestination", mList.get(position).getPDestinationName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Psource, Pdestination, Pdistance, Pdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Psource = itemView.findViewById(R.id.tv_from);
            Pdestination = itemView.findViewById(R.id.tv_to);
            Pdistance = itemView.findViewById(R.id.tv_distance);
            Pdate = itemView.findViewById(R.id.tv_date);



        }

    }
}
