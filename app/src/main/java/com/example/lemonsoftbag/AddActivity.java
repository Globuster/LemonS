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
    private boolean isExpense;

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


        findViewById(R.id.btnFinish).setOnClickListener(v -> finish());


        SQLiteOpenHelper = new ExpenseSqlite(this);
        int editId = getIntent().getIntExtra("editId", -1);
        boolean isEdit = editId != -1;

        if (isEdit) {
            addTv.setText(isExpense ? "Редактировать расход" : "Редактировать доход");
            button.setText(isExpense ? "Сохранить изменения" : "Сохранить изменения");

            // Заполняем поля
            double amount = getIntent().getDoubleExtra("amount", 0);
            String reason = getIntent().getStringExtra("reason");

            edBuy.setText(String.valueOf(amount));
            int spinnerPos = ((ArrayAdapter<String>) edReason.getAdapter()).getPosition(reason);
            edReason.setSelection(spinnerPos);
        }

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

            addTv.setText("Добавить расход");
            buyDisplay.setText("Сколько денег ты потратил на покупку?");
            reasonDisplay.setText("По какой причине?");
            button.setText("Добавить расход");

        } else {

            addTv.setText("Добавить доход");
            buyDisplay.setText("Сколько дохода ты получил?");
            reasonDisplay.setText("Откуда ты заработал?");
            button.setText("Добавить доход");
        }

        // Кнопка добавления
        button.setOnClickListener(v -> {
            if (edBuy.length() > 0) {
                String reason = edReason.getSelectedItem().toString();
                double amount = Double.parseDouble(edBuy.getText().toString());

                if (isEdit) { // если мы редактируем существующую запись
                    if (EXPENSE) {
                        SQLiteOpenHelper.updateExpense(editId, amount, reason);
                    } else {
                        SQLiteOpenHelper.updateIncome(editId, amount, reason);
                    }
                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                } else { // обычное добавление
                    if (EXPENSE) {
                        SQLiteOpenHelper.addExpense(amount, reason);
                    } else {
                        SQLiteOpenHelper.addIncome(amount, reason);
                    }
                    Toast.makeText(this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                }

                finish(); // закрываем activity
            } else {
                Toast.makeText(this, "Amount is empty!", Toast.LENGTH_LONG).show();
            }
        });
    }
}