package com.example.lemonsoftbag;

import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpenseAdepter adepter;
    ArrayList<ExpenseModel> arrayList;

    ExpenseSqlite sqlite;
    public static boolean REC_VIEW = true;

    TextView recyTv;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyTv = findViewById(R.id.recyTv);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqlite = new ExpenseSqlite(this);

        arrayList = new ArrayList<>();
        adepter = new ExpenseAdepter(arrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adepter);

        loadDataDefault();

        // SEARCH
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void loadDataDefault() {
        Cursor cursor;
        if (REC_VIEW) {
            cursor = sqlite.showExpenseRecyclerView();
            recyTv.setText("Expense List");
        } else {
            cursor = sqlite.showIncomeRecyclerView();
            recyTv.setText("Income List");
        }
        loadDataFromCursor(cursor);
    }

    private void loadDataFromCursor(Cursor cursor) {
        arrayList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                arrayList.add(new ExpenseModel(id, String.valueOf(amount), reason));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adepter.notifyDataSetChanged();
    }

    private void filterList(String text) {
        ArrayList<ExpenseModel> filteredList = new ArrayList<>();
        for (ExpenseModel item : arrayList) {
            if (item.getReason().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adepter.updateList(filteredList);
    }
}
