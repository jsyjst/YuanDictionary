package cn.jsyjst.dictionary.db.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.entity.History;

/**
 * Created by 残渊 on 2018/4/19.
 */

public class HistoryCrud {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase historyDb;

    private List<History> tempList = new ArrayList<>();
    private List<History> historyList;

    public HistoryCrud(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        dbHelper = new MyDatabaseHelper(context, name, factory, version);
    }

    public void Create(String table, String wordName, String phEN, String phAM, String phEnMp3, String phAmMp3, String mean, String means,
                       String wordPl, String wordThird, String wordPast, String wordDone, String wordIng, String wordEr, String wordEst) {


        /**
         *   添加搜索记录
         */


        isHasHistory(table, wordName);
        historyDb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word_name", wordName);
        values.put("ph_en", phEN);
        values.put("ph_am", phAM);
        values.put("ph_en_mp3", phEnMp3);
        values.put("ph_am_mp3", phAmMp3);
        values.put("mean", mean);
        values.put("means", means);
        values.put("word_pl", wordPl);
        values.put("word_third", wordThird);
        values.put("word_past", wordPast);
        values.put("word_done", wordDone);
        values.put("word_ing", wordIng);
        values.put("word_er", wordEr);
        values.put("word_est", wordEst);
        historyDb.insert(table, null, values);
        historyDb.close();

    }

    /**
     * 判断是否含有该搜索记录,若有就删除该记录
     */

    public void isHasHistory(String table, String wordName) {

        historyDb = dbHelper.getWritableDatabase();
        Cursor cursor = historyDb.query(table, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (wordName.equals(cursor.getString(cursor.getColumnIndexOrThrow("word_name")))) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));//获取该记录的id
                historyDb.execSQL("delete from " + table + " Where id=" + id);
            }
        }
        historyDb.close();
        cursor.close();
    }

    /**
     * 获取全部搜索记录
     */

    public List<History> getHistoryList() {
        historyList = new ArrayList<>();
        historyDb = dbHelper.getWritableDatabase();
        Cursor cursor = historyDb.query("History", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("word_name"));
            String mean = cursor.getString(cursor.getColumnIndexOrThrow("mean"));
            History history = new History(name, mean);
            historyList.add(history);
        }


        /**
         *  颠倒list顺序，使记录从上往下显示
         */

        tempList.clear();
        for (int i = historyList.size() - 1; i >= 0; i--) {
            tempList.add(historyList.get(i));
        }
        historyDb.close();
        cursor.close();
        return tempList;
    }


    /**
     * 清空搜索记录
     */

    public void deleteAllhistory(String table) {
        tempList.clear();
        historyDb = dbHelper.getWritableDatabase();
        historyDb.execSQL("delete from " + table);
        historyDb.close();
    }
}


