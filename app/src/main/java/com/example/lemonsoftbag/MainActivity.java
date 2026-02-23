package com.example.lemonsoftbag;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView mainBalance, totalExpense, addExpense, expenseShow;
    TextView totalIncome, addIncome, incomeShow;

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
        expenseShow = findViewById(R.id.expenseShow);

        totalIncome = findViewById(R.id.totalIncome);
        addIncome = findViewById(R.id.addIncome);
        incomeShow = findViewById(R.id.incomeShow);

        // ADD EXPENSE
        addExpense.setOnClickListener(v -> {
            AddActivity.EXPENSE = true;
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        // ADD INCOME
        addIncome.setOnClickListener(v -> {
            AddActivity.EXPENSE = false;
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        });

        // SHOW EXPENSE LIST
        expenseShow.setOnClickListener(v -> {
            RecyclerViewActivity.REC_VIEW = true;
            startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
        });

        // SHOW INCOME LIST
        incomeShow.setOnClickListener(v -> {
            RecyclerViewActivity.REC_VIEW = false;
            startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
        });

        showData();
    }

    public void showData() {
        double expense = sqlite.showExpense();
        double income = sqlite.showIncome();
        double balance = income - expense;

        totalExpense.setText("" + expense);
        totalIncome.setText("" + income);
        mainBalance.setText("" + balance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }
}
