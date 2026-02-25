package com.example.lemonsoftbag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdepter extends RecyclerView.Adapter<ExpenseAdepter.MyViewHolder> {

    ArrayList<ExpenseModel> list;
    Context context;
    ExpenseSqlite sqlite;
    boolean isExpense;

    public ExpenseAdepter(ArrayList<ExpenseModel> list, Context context, boolean isExpense) {
        this.list = list;
        this.context = context;
        this.sqlite = new ExpenseSqlite(context);
        this.isExpense = isExpense;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ExpenseModel model = list.get(position);

        holder.tvBuy.setText(model.getBuy());
        holder.tvReason.setText(model.getReason());

        long time = model.getDate();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        holder.tvDate.setText(format.format(new Date(time)));

        // Цвет зависит от type конкретной записи
        if ("expense".equals(model.getType())) {
            holder.tvBuy.setTextColor(Color.RED); // расходы красные
        } else if ("income".equals(model.getType())) {
            holder.tvBuy.setTextColor(Color.GREEN); // доходы зелёные
        } else {
            holder.tvBuy.setTextColor(Color.WHITE); // на всякий случай
        }

        holder.moreBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.moreBtn);
            popup.inflate(R.menu.item_menu);

            popup.setOnMenuItemClickListener(item -> {

                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return false;

                ExpenseModel currentItem = list.get(currentPosition);

                if (item.getItemId() == R.id.delete) {
                    boolean isExpenseForDeletion = "expense".equals(currentItem.getType());
                    sqlite.deleteById(currentItem.getId(), isExpenseForDeletion);

                    list.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    return true;
                }

                // ===== Добавляем редактирование ====
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean isExpense() {
        return isExpense;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvBuy, tvReason, tvDate;
        ImageView moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBuy = itemView.findViewById(R.id.tvBuy);
            tvReason = itemView.findViewById(R.id.tvReason);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}