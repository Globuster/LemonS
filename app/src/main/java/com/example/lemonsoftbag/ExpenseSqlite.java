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
        database.execSQL("CREATE TABLE expense(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DOUBLE," +
                "reason TEXT," +
                "time INTEGER)");

        database.execSQL("CREATE TABLE income(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DOUBLE," +
                "reason TEXT," +
                "time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS expense");
        database.execSQL("DROP TABLE IF EXISTS income");
        onCreate(database);
    }

    // ===== ДОБАВЛЕНИЕ =====

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

    // ===== ПОДСЧЁТ =====

    public double showExpense() {
        double total = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT amount FROM expense", null);

        if (cursor.moveToFirst()) {
            do {
                total += cursor.getDouble(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return total;
    }

    public double showIncome() {
        double total = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT amount FROM income", null);

        if (cursor.moveToFirst()) {
            do {
                total += cursor.getDouble(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return total;
    }

    // ===== ДЛЯ RECYCLERVIEW =====

    public Cursor showExpenseRecyclerView() {
        SQLiteDatabase database = this.getReadableDatabase();

        return database.rawQuery(
                "SELECT id, amount, reason, time, 'expense' as type " +
                        "FROM expense ORDER BY time DESC",
                null
        );
    }

    public Cursor showIncomeRecyclerView() {
        SQLiteDatabase database = this.getReadableDatabase();

        return database.rawQuery(
                "SELECT id, amount, reason, time, 'income' as type " +
                        "FROM income ORDER BY time DESC",
                null
        );
    }

    public Cursor getExpenseFrom(long fromTime) {
        SQLiteDatabase database = this.getReadableDatabase();

        return database.rawQuery(
                "SELECT id, amount, reason, time, 'expense' as type " +
                        "FROM expense WHERE time >= ? ORDER BY time DESC",
                new String[]{String.valueOf(fromTime)}
        );
    }

    public Cursor getIncomeFrom(long fromTime) {
        SQLiteDatabase database = this.getReadableDatabase();

        return database.rawQuery(
                "SELECT id, amount, reason, time, 'income' as type " +
                        "FROM income WHERE time >= ? ORDER BY time DESC",
                new String[]{String.valueOf(fromTime)}
        );
    }

    public Cursor getAllHistory() {
        SQLiteDatabase database = this.getReadableDatabase();

        return database.rawQuery(
                "SELECT id, amount, reason, time, 'expense' as type FROM expense " +
                        "UNION ALL " +
                        "SELECT id, amount, reason, time, 'income' as type FROM income " +
                        "ORDER BY time DESC",
                null
        );
    }

    // ===== УДАЛЕНИЕ =====

    public void deleteById(int id, boolean isExpense) {
        SQLiteDatabase database = this.getWritableDatabase();

        if (isExpense) {
            database.delete("expense", "id = ?", new String[]{String.valueOf(id)});
        } else {
            database.delete("income", "id = ?", new String[]{String.valueOf(id)});
        }

        database.close();
    }

    public void updateExpense(int id, double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("reason", reason);
        db.update("expenseTable", cv, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateIncome(int id, double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("reason", reason);
        db.update("incomeTable", cv, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }}