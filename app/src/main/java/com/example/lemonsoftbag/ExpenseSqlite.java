package com.example.lemonsoftbag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ExpenseSqlite extends SQLiteOpenHelper {

    private static final String DB_NAME = "DATABASE_LEMON";
    private static final int DB_VERSION = 1;

    // Таблицы
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";

    // Колонки
    private static final String COL_ID = "id";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_REASON = "reason";
    private static final String COL_TIME = "time";

    public ExpenseSqlite(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("CREATE TABLE " + TABLE_EXPENSE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " DOUBLE, " +
                COL_REASON + " TEXT, " +
                COL_TIME + " LONG)");

        database.execSQL("CREATE TABLE " + TABLE_INCOME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " DOUBLE, " +
                COL_REASON + " TEXT, " +
                COL_TIME + " LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        onCreate(database);
    }

    // ============================
    // ADD (добавление)
    // ============================

    public long addExpense(double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_REASON, reason);
        values.put(COL_TIME, System.currentTimeMillis());

        return db.insert(TABLE_EXPENSE, null, values);
    }

    public long addIncome(double amount, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_REASON, reason);
        values.put(COL_TIME, System.currentTimeMillis());

        return db.insert(TABLE_INCOME, null, values);
    }

    // ============================
    // DELETE (удаление)
    // ============================

    public int deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSE, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteIncome(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_INCOME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ============================
    // UPDATE (изменение записи)
    // ============================

    public int updateExpense(int id, double newAmount, String newReason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, newAmount);
        values.put(COL_REASON, newReason);

        return db.update(TABLE_EXPENSE, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateIncome(int id, double newAmount, String newReason) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, newAmount);
        values.put(COL_REASON, newReason);

        return db.update(TABLE_INCOME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ============================
    // TOTALS (суммы)
    // ============================

    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSE, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_INCOME, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    // ============================
    // SHOW (получение списка)
    // ============================

    public Cursor showExpenseRecyclerView() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSE + " ORDER BY " + COL_TIME + " DESC", null);
    }

    public Cursor showIncomeRecyclerView() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INCOME + " ORDER BY " + COL_TIME + " DESC", null);
    }

    // ============================
    // SORTING (сортировка)
    // ============================

    public Cursor showExpenseSortedByAmount(boolean desc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order = desc ? "DESC" : "ASC";
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSE + " ORDER BY " + COL_AMOUNT + " " + order, null);
    }

    public Cursor showIncomeSortedByAmount(boolean desc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order = desc ? "DESC" : "ASC";
        return db.rawQuery("SELECT * FROM " + TABLE_INCOME + " ORDER BY " + COL_AMOUNT + " " + order, null);
    }

    public Cursor showExpenseSortedByDate(boolean desc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order = desc ? "DESC" : "ASC";
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSE + " ORDER BY " + COL_TIME + " " + order, null);
    }

    public Cursor showIncomeSortedByDate(boolean desc) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order = desc ? "DESC" : "ASC";
        return db.rawQuery("SELECT * FROM " + TABLE_INCOME + " ORDER BY " + COL_TIME + " " + order, null);
    }

    // ============================
    // SEARCH (поиск)
    // ============================

    public Cursor searchExpense(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSE +
                        " WHERE " + COL_REASON + " LIKE ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{"%" + query + "%"}
        );
    }

    public Cursor searchIncome(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INCOME +
                        " WHERE " + COL_REASON + " LIKE ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{"%" + query + "%"}
        );
    }

    // ============================
    // FILTER BY PERIOD (фильтр по времени)
    // ============================

    public Cursor filterExpenseByPeriod(long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSE +
                        " WHERE " + COL_TIME + " BETWEEN ? AND ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{String.valueOf(startTime), String.valueOf(endTime)}
        );
    }

    public Cursor filterIncomeByPeriod(long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INCOME +
                        " WHERE " + COL_TIME + " BETWEEN ? AND ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{String.valueOf(startTime), String.valueOf(endTime)}
        );
    }

    // ============================
    // FILTER + SEARCH COMBO (поиск в периоде)
    // ============================

    public Cursor searchExpenseInPeriod(String query, long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSE +
                        " WHERE " + COL_REASON + " LIKE ? " +
                        " AND " + COL_TIME + " BETWEEN ? AND ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{"%" + query + "%", String.valueOf(startTime), String.valueOf(endTime)}
        );
    }

    public Cursor searchIncomeInPeriod(String query, long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INCOME +
                        " WHERE " + COL_REASON + " LIKE ? " +
                        " AND " + COL_TIME + " BETWEEN ? AND ? " +
                        " ORDER BY " + COL_TIME + " DESC",
                new String[]{"%" + query + "%", String.valueOf(startTime), String.valueOf(endTime)}
        );
    }

    // ============================
    // GET ONE ITEM BY ID (получить запись по id)
    // ============================

    public Cursor getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public Cursor getIncomeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INCOME + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public double showExpense() {
        return getTotalExpense();
    }

    public double showIncome() {
        return getTotalIncome();
    }
}
