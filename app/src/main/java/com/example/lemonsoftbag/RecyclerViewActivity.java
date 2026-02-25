package com.example.lemonsoftbag;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpenseAdepter adepter;

    ArrayList<ExpenseModel> arrayList;
    ArrayList<ExpenseModel> displayList;

    ExpenseSqlite sqlite;

    TextView recyTv;
    SearchView searchView;
    ImageView sortFilterBtn;

    boolean isExpense;       // true = расходы, false = доходы
    boolean showAllHistory;  // true = полный список (доходы + расходы)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        recyTv = findViewById(R.id.recyTv);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        sortFilterBtn = findViewById(R.id.sortFilterBtn);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqlite = new ExpenseSqlite(this);

        arrayList = new ArrayList<>();
        displayList = new ArrayList<>();

        // Получаем флаги из Intent
        isExpense = getIntent().getBooleanExtra("isExpense", true);
        showAllHistory = getIntent().getBooleanExtra("showAllHistory", false);

        adepter = new ExpenseAdepter(displayList, this, isExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adepter);

        // Загружаем данные
        if (showAllHistory) {
            loadAllHistory();
        } else {
            loadDataDefault();
        }

        // ===== ПОИСК =====
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

        // ===== BOTTOM SHEET =====
        sortFilterBtn.setOnClickListener(v -> showBottomSheet());
    }

    // ===== Показ Bottom Sheet =====
    private void showBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheet.setContentView(sheetView);

        // ===== Сортировка =====
        sheetView.findViewById(R.id.sortAscending).setOnClickListener(v -> {
            displayList.sort((a, b) -> Double.compare(Double.parseDouble(a.getBuy()), Double.parseDouble(b.getBuy())));
            adepter.notifyDataSetChanged();
            bottomSheet.dismiss();
        });

        sheetView.findViewById(R.id.sortDescending).setOnClickListener(v -> {
            displayList.sort((a, b) -> Double.compare(Double.parseDouble(b.getBuy()), Double.parseDouble(a.getBuy())));
            adepter.notifyDataSetChanged();
            bottomSheet.dismiss();
        });

        // ===== Период =====
        sheetView.findViewById(R.id.periodToday).setOnClickListener(v -> {
            filterByPeriod(0);
            bottomSheet.dismiss();
        });
        sheetView.findViewById(R.id.periodWeek).setOnClickListener(v -> {
            filterByPeriod(7);
            bottomSheet.dismiss();
        });
        sheetView.findViewById(R.id.periodMonth).setOnClickListener(v -> {
            filterByPeriod(30);
            bottomSheet.dismiss();
        });

        // ===== Фильтр категорий (только для обычных расходов/доходов) =====
        LinearLayout categoryContainer = sheetView.findViewById(R.id.categoryContainer);
        if (!showAllHistory) {
            categoryContainer.setVisibility(View.VISIBLE);

            ((TextView) sheetView.findViewById(R.id.filter1)).setText(isExpense ? "Еда" : "Зарплата");
            ((TextView) sheetView.findViewById(R.id.filter2)).setText(isExpense ? "Транспорт" : "Подработка");
            ((TextView) sheetView.findViewById(R.id.filter3)).setText(isExpense ? "Развлечения" : "Подарок");
            ((TextView) sheetView.findViewById(R.id.filter4)).setVisibility(isExpense ? View.VISIBLE : View.GONE);
            ((TextView) sheetView.findViewById(R.id.filter4)).setText(isExpense ? "Коммуналка" : "");

            sheetView.findViewById(R.id.filter1).setOnClickListener(view -> {
                filterByCategory(((TextView)view).getText().toString());
                bottomSheet.dismiss();
            });
            sheetView.findViewById(R.id.filter2).setOnClickListener(view -> {
                filterByCategory(((TextView)view).getText().toString());
                bottomSheet.dismiss();
            });
            sheetView.findViewById(R.id.filter3).setOnClickListener(view -> {
                filterByCategory(((TextView)view).getText().toString());
                bottomSheet.dismiss();
            });
            sheetView.findViewById(R.id.filter4).setOnClickListener(view -> {
                if (isExpense) filterByCategory(((TextView)view).getText().toString());
                bottomSheet.dismiss();
            });

        } else {
            categoryContainer.setVisibility(View.GONE);
        }

        bottomSheet.show();
    }

    // ===== ЗАГРУЗКА ПО УМОЛЧАНИЮ =====
    private void loadDataDefault() {
        Cursor cursor;
        if (isExpense) {
            cursor = sqlite.showExpenseRecyclerView();
            recyTv.setText("Список расходов");
        } else {
            cursor = sqlite.showIncomeRecyclerView();
            recyTv.setText("Список доходов");
        }
        loadDataFromCursor(cursor);
    }

    // ===== ЗАГРУЗКА ИЗ CURSOR =====
    @SuppressLint("NotifyDataSetChanged")
    private void loadDataFromCursor(Cursor cursor) {
        arrayList.clear();
        displayList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                long time = cursor.getLong(3);
                String type = cursor.getString(4);

                arrayList.add(new ExpenseModel(id, String.valueOf(amount), reason, time, type));
            } while (cursor.moveToNext());
            cursor.close();
        }

        displayList.addAll(arrayList);
        adepter.notifyDataSetChanged();
    }

    // ===== ФИЛЬТР ПО ПЕРИОДУ =====
    private void filterByPeriod(int daysBack) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (daysBack > 0) calendar.add(Calendar.DAY_OF_MONTH, -daysBack);

        long fromTime = calendar.getTimeInMillis();
        Cursor cursor = isExpense ? sqlite.getExpenseFrom(fromTime) : sqlite.getIncomeFrom(fromTime);
        loadDataFromCursor(cursor);
    }

    // ===== ФИЛЬТР ПО КАТЕГОРИИ =====
    private void filterByCategory(String category) {
        displayList.clear();
        for (ExpenseModel item : arrayList) {
            if (item.getReason().equalsIgnoreCase(category)) displayList.add(item);
        }
        adepter.notifyDataSetChanged();
    }

    // ===== ПОИСК =====
    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query) {
        displayList.clear();
        if (query == null || query.trim().isEmpty()) {
            displayList.addAll(arrayList);
        } else {
            String text = query.toLowerCase().trim();
            for (ExpenseModel item : arrayList) {
                boolean matchesText = item.getReason().toLowerCase().contains(text);
                boolean matchesAmount = false;
                try {
                    double queryNumber = Double.parseDouble(text);
                    double itemAmount = Double.parseDouble(item.getBuy());
                    matchesAmount = queryNumber == itemAmount;
                } catch (NumberFormatException ignored) {}
                if (matchesText || matchesAmount) displayList.add(item);
            }
        }
        adepter.notifyDataSetChanged();
    }

    // ===== ПОЛНАЯ ИСТОРИЯ =====
    private void loadAllHistory() {
        Cursor cursor = sqlite.getAllHistory();
        arrayList.clear();
        displayList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                long time = cursor.getLong(3);
                String type = cursor.getString(4);

                arrayList.add(new ExpenseModel(id, String.valueOf(amount), reason, time, type));
            } while (cursor.moveToNext());
            cursor.close();
        }

        displayList.addAll(arrayList);
        adepter.notifyDataSetChanged();
    }
}