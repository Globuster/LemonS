package com.example.lemonsoftbag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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

        holder.moreBtn.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, holder.moreBtn);
            popup.inflate(R.menu.item_menu);

            popup.setOnMenuItemClickListener(item -> {

                int currentPosition = holder.getAdapterPosition();

                if (currentPosition == RecyclerView.NO_POSITION) {
                    return false;
                }

                ExpenseModel currentItem = list.get(currentPosition);

                if (item.getItemId() == R.id.delete) {

                    // Удаляем из базы
                    sqlite.deleteById(currentItem.getId(), isExpense);

                    // Удаляем из текущего отображаемого списка
                    list.remove(currentPosition);

                    notifyItemRemoved(currentPosition);

                    return true;
                }

                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvBuy, tvReason;
        ImageView moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBuy = itemView.findViewById(R.id.tvBuy);
            tvReason = itemView.findViewById(R.id.tvReason);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}