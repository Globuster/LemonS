package com.example.lemonsoftbag;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {

    TextView buyDisplay,reasonDisplay,button,addTv;
    EditText edBuy,edReason;

    ExpenseSqlite SQLiteOpenHelper;



    public static boolean EXPENSE = true;


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



        if (EXPENSE==true){

            addTv.setText("Add Expense");
            buyDisplay.setText("How much money do you want to spend to Buy?");
            reasonDisplay.setText("What is the reason for Buying");
            button.setText("Add Expense to SQlite");

        }else {

            addTv.setText("Add Income");
            buyDisplay.setText("How much did you earn?");
            reasonDisplay.setText("Where did you earn this money");
            button.setText("Add Income to SQlite");





        }







        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edBuy.length()>0&&edReason.length()>0){





                String reason = edReason.getText().toString();
                String buy = edBuy.getText().toString();
                double amount = Double.parseDouble(buy);



                if (EXPENSE==true){

                    SQLiteOpenHelper.addExpense(amount,reason);
                    edBuy.setText("");
                    edReason.setText("");
                    Toast.makeText(AddActivity.this,"The data Successfully Added",Toast.LENGTH_LONG).show();

                }else {

                    SQLiteOpenHelper.addIncome(amount,reason);
                    edBuy.setText("");
                    edReason.setText("");
                    Toast.makeText(AddActivity.this,"The data Successfully Added",Toast.LENGTH_LONG).show();

                }




                }else {

                    Toast.makeText(AddActivity.this,"The edit text is empty!",Toast.LENGTH_LONG).show();

                }



            }
        });















    }
}