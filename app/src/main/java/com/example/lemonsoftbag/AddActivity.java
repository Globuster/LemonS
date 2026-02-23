package com.example.lemonsoftbag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    TextView buyDisplay, reasonDisplay, button, addTv;
    EditText edBuy;
    Spinner edReason;

    ExpenseSqlite SQLiteOpenHelper;

    public static boolean EXPENSE = true;

    @SuppressLint({"SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        button = findViewById(R.id.button);
        edBuy = findViewById(R.id.edBuy);
        edReason = findViewById(R.id.edReason);

        buyDisplay = findViewById(R.id.buyDisplay);
        reasonDisplay = findViewById(R.id.reasonDisplay);
        addTv = findViewById(R.id.addTv);

        SQLiteOpenHelper = new ExpenseSqlite(this);

        // Определяем категории автоматически
        String[] categories;

        if (EXPENSE) {
            categories = new String[]{"Еда", "Транспорт", "Развлечения", "Коммуналка"};
        } else {
            categories = new String[]{"Зарплата", "Подработка", "Подарок"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edReason.setAdapter(adapter);

        // Меняем тексты в зависимости от режима
        if (EXPENSE) {

            addTv.setText("Add Expense");
            buyDisplay.setText("How much money do you want to spend?");
            reasonDisplay.setText("What is the reason?");
            button.setText("Add Expense");

        } else {

            addTv.setText("Add Income");
            buyDisplay.setText("How much did you earn?");
            reasonDisplay.setText("Where did you earn this money?");
            button.setText("Add Income");
        }

        // Кнопка добавления
        button.setOnClickListener(v -> {

            if (edBuy.length() > 0) {

                String reason = edReason.getSelectedItem().toString();
                double amount = Double.parseDouble(edBuy.getText().toString());

                if (EXPENSE) {
                    SQLiteOpenHelper.addExpense(amount, reason);
                } else {
                    SQLiteOpenHelper.addIncome(amount, reason);
                }

                edBuy.setText("");
                Toast.makeText(this, "The data Successfully Added", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Amount is empty!", Toast.LENGTH_LONG).show();
            }
        });
    }
}