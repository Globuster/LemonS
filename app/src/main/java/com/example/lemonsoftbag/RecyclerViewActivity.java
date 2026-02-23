package com.example.lemonsoftbag;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpenseAdepter adepter;

    ArrayList<ExpenseModel> arrayList;
    ArrayList<ExpenseModel> displayList;

    ExpenseSqlite sqlite;
    public static boolean REC_VIEW = true;

    TextView recyTv;
    SearchView searchView;
    ImageView sortFilterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyTv = findViewById(R.id.recyTv);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        sortFilterBtn = findViewById(R.id.sortFilterBtn);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqlite = new ExpenseSqlite(this);

        arrayList = new ArrayList<>();
        displayList = new ArrayList<>();

        adepter = new ExpenseAdepter(displayList, this, REC_VIEW);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adepter);

        loadDataDefault();

        // Поиск
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

        // Кнопка сортировки/фильтрации
        sortFilterBtn.setOnClickListener(v -> {
            androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, sortFilterBtn);
            popup.getMenu().add("Сортировка: возрастание");
            popup.getMenu().add("Сортировка: убывание");
            if (REC_VIEW) { // Expenses
                popup.getMenu().add("Фильтр: Еда");
                popup.getMenu().add("Фильтр: Транспорт");
                popup.getMenu().add("Фильтр: Развлечения");
                popup.getMenu().add("Фильтр: Коммуналка");
            } else { // Income
                popup.getMenu().add("Фильтр: Зарплата");
                popup.getMenu().add("Фильтр: Подработка");
                popup.getMenu().add("Фильтр: Подарок");
            }

            popup.getMenu().add("Фильтр: Все");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();

                switch (title) {
                    case "Сортировка: возрастание":
                        displayList.sort((a, b) -> Double.compare(Double.parseDouble(a.getBuy()), Double.parseDouble(b.getBuy())));
                        break;
                    case "Сортировка: убывание":
                        displayList.sort((a, b) -> Double.compare(Double.parseDouble(b.getBuy()), Double.parseDouble(a.getBuy())));
                        break;
                    case "Фильтр: Все":
                        displayList.clear();
                        displayList.addAll(arrayList);
                        break;

                    default: // Все остальные — фильтры по категории
                        filterByCategory(title.replace("Фильтр: ", ""));
                        break;    

                }

                adepter.notifyDataSetChanged();
                return true;
            });

            popup.show();
        });
    }

    private void filterByCategory(String category) {
        displayList.clear();
        for (ExpenseModel item : arrayList) {
            if (item.getReason().equalsIgnoreCase(category)) {
                displayList.add(item);
            }
        }


    }

    private void loadDataDefault() {
        Cursor cursor;

        if (REC_VIEW) {
            cursor = sqlite.showExpenseRecyclerView();
            recyTv.setText("Список расходов");
        } else {
            cursor = sqlite.showIncomeRecyclerView();
            recyTv.setText("Список доходов");
        }

        loadDataFromCursor(cursor);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataFromCursor(Cursor cursor) {
        arrayList.clear();
        displayList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);

                ExpenseModel model = new ExpenseModel(id, String.valueOf(amount), reason);
                arrayList.add(model);

            } while (cursor.moveToNext());

            cursor.close();
        }

        displayList.addAll(arrayList);
        adepter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query) {
        displayList.clear();

        if (query == null || query.trim().isEmpty()) {
            displayList.addAll(arrayList);
        } else {
            String text = query.toLowerCase().trim();

            for (ExpenseModel item : arrayList) {
                // Поиск по тексту
                boolean matchesText = item.getReason().toLowerCase().contains(text);

                // Поиск по сумме (числам)
                boolean matchesAmount = false;
                try {
                    double queryNumber = Double.parseDouble(text);
                    double itemAmount = Double.parseDouble(item.getBuy());
                    matchesAmount = (queryNumber == itemAmount);
                } catch (NumberFormatException ignored) { }

                // Добавляем элемент, если совпадает текст или сумма
                if (matchesText || matchesAmount) {
                    displayList.add(item);
                }
            }
        }

        adepter.notifyDataSetChanged();
    }
    private void filterByType(boolean isExpenseType) {
        displayList.clear();
        for (ExpenseModel item : arrayList) {
            if (REC_VIEW == isExpenseType) {
                displayList.add(item);
            }
        }
    }
}