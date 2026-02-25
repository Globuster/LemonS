package com.example.lemonsoftbag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    LinearLayout balanceHistory;
    LinearLayout addExpense, addIncome, expenseShow, incomeShow;

    TextView mainBalance, totalExpense;
    TextView totalIncome;

    ExpenseSqlite sqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqlite = new ExpenseSqlite(this);

        balanceHistory = findViewById(R.id.balanceHistory);

        mainBalance = findViewById(R.id.mainBalance);

        totalExpense = findViewById(R.id.totalExpense);
        addExpense = findViewById(R.id.addExpense);
        expenseShow = findViewById(R.id.expenseShow);

        totalIncome = findViewById(R.id.totalIncome);
        addIncome = findViewById(R.id.addIncome);
        incomeShow = findViewById(R.id.incomeShow);

        balanceHistory.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);

            // если хочешь передавать тип (доходы или расходы)
            intent.putExtra("showAllHistory", true); // теперь будет загружаться полная история
            startActivity(intent);
        });

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
            Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
            intent.putExtra("isExpense", true); // передаем флаг расходов
            startActivity(intent);
        });

// SHOW INCOME LIST
        incomeShow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
            intent.putExtra("isExpense", false); // передаем флаг доходов
            startActivity(intent);
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
