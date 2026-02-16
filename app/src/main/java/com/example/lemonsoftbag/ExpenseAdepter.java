package com.example.lemonsoftbag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdepter extends RecyclerView.Adapter<ExpenseAdepter.ViewHolder> {

    ArrayList<ExpenseModel> arrayList = new ArrayList<>();
    Context context;

    public ExpenseAdepter(ArrayList<ExpenseModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }





    @NonNull
    @Override
    public ExpenseAdepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.data_list,parent,false));




    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdepter.ViewHolder holder, int position) {

        holder.tvReason.setText(arrayList.get(position).getReason());
        holder.tvBuy.setText(arrayList.get(position).getBuy());




    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void updateList(ArrayList<ExpenseModel> filteredList) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBuy,tvReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvBuy = itemView.findViewById(R.id.tvBuy);
            tvReason = itemView.findViewById(R.id.tvReason);









        }
    }
}
