package com.example.lemonsoftbag;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView mainBalance, totalExpense, addExpense, showExpense,
            totalIncome, addIncome, showIncome;

    ExpenseSqlite sqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqlite = new ExpenseSqlite(this);

        mainBalance = findViewById(R.id.mainBalance);
        totalExpense = findViewById(R.id.totalExpense);
        addExpense = findViewById(R.id.addExpense);
        showExpense = findViewById(R.id.expenseShow);

        totalIncome = findViewById(R.id.totalIncome);
        addIncome = findViewById(R.id.addIncome);
        showIncome = findViewById(R.id.incomeShow);

        // Добавление записи
        addExpense.setOnClickListener(v -> {
            AddActivity.EXPENSE = true;
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        addIncome.setOnClickListener(v -> {
            AddActivity.EXPENSE = false;
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        // Просмотр списка
        showExpense.setOnClickListener(v -> {
            RecyclerViewActivity.REC_VIEW = true;
            startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
        });

        showIncome.setOnClickListener(v -> {
            RecyclerViewActivity.REC_VIEW = false;
            startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
        });
    }

    // Показать баланс и суммы
    public void showData() {
        double expense = sqlite.showExpense();
        double income = sqlite.showIncome();
        double balance = income - expense;

        totalExpense.setText("BDT: " + expense);
        totalIncome.setText("BDT: " + income);
        mainBalance.setText("BDT: " + balance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData(); // обновление при возврате с AddActivity
    }
}
