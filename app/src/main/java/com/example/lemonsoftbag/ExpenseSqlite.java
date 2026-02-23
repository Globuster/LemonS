package com.example.lemonsoftbag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ExpenseSqlite extends SQLiteOpenHelper {

    public ExpenseSqlite(@Nullable Context context) {
        super(context, "DATABASE_LEMON", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table expense(id INTEGER primary key autoincrement,amount DOUBLE,reason TEXT,time DOUBLE)");
        database.execSQL("create table income(id INTEGER primary key autoincrement,amount DOUBLE,reason TEXT,time DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("drop table if exists expense");
        database.execSQL("drop table if exists income");
    }

    public void addExpense(double amount, String reason) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("reason", reason);
        values.put("time", System.currentTimeMillis());
        database.insert("expense", null, values);
        database.close();
    }

    public void addIncome(double amount, String reason) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("reason", reason);
        values.put("time", System.currentTimeMillis());
        database.insert("income", null, values);
        database.close();
    }

    public double showExpense() {
        double totalExpense = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from expense", null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                double expense = cursor.getDouble(1);
                totalExpense += expense;
            }
            cursor.close();
        }
        database.close();
        return totalExpense;
    }

    public double showIncome() {
        double totalIncome = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from income", null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                double income = cursor.getDouble(1);
                totalIncome += income;
            }
            cursor.close();
        }
        database.close();
        return totalIncome;
    }

    public Cursor showExpenseRecyclerView() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from expense", null);
    }

    public Cursor showIncomeRecyclerView() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from income", null);
    }

    // ✅ Новый метод для удаления записи
    public void deleteById(int id, boolean isExpense) {
        SQLiteDatabase database = this.getWritableDatabase();

        if (isExpense) {
            database.delete("expense", "id = ?", new String[]{String.valueOf(id)});
        } else {
            database.delete("income", "id = ?", new String[]{String.valueOf(id)});
        }

        database.close();
    }
}