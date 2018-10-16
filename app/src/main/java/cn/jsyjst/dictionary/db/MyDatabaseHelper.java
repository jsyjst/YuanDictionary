package cn.jsyjst.dictionary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 残渊 on 2018/4/19.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    /**
     * 联网查询单词一次后，都在这个表中来查询单词，不设置删除
     */
    public static final String CREATE_WORD="create table Word("
            +"id integer primary key autoincrement,"
            +"word_name text,"
            +"ph_en text,"
            +"ph_am text,"
            +"ph_en_mp3 text,"
            +"ph_am_mp3 text,"
            +"mean text,"
            +"means text,"
            +"word_pl text,"
            +"word_third text,"
            +"word_past text,"
            +"word_done text,"
            +"word_ing text,"
            +"word_er text,"
            +"word_est text)";

    /**
     * 历史记录的表
     */
    public static final String CREATE_HISTORY="create table History("
            +"id integer primary key autoincrement,"
            +"word_name text,"
            +"ph_en text,"
            +"ph_am text,"
            +"ph_en_mp3 text,"
            +"ph_am_mp3 text,"
            +"mean text,"
            +"means text,"
            +"word_pl text,"
            +"word_third text,"
            +"word_past text,"
            +"word_done text,"
            +"word_ing text,"
            +"word_er text,"
            +"word_est text)";
    /**
     * 生词本的表
     */
    public static final String CREATE_NWEWORDBOOK="create table NewWordBook("
            +"id integer primary key autoincrement,"
            +"word_name text,"
            +"ph_en text,"
            +"ph_am text,"
            +"ph_en_mp3 text,"
            +"ph_am_mp3 text,"
            +"mean text,"
            +"means text,"
            +"word_pl text,"
            +"word_third text,"
            +"word_past text,"
            +"word_done text,"
            +"word_ing text,"
            +"word_er text,"
            +"word_est text)";

    /**
     * 每日一句的表
     */
    public static final String CREATE_DAYWORD="create table DayWord("
            +"id integer primary key autoincrement,"
            +"dateline text,"
            +"content text,"
            +"note text,"
            +"translation text,"
            +"tts text)";


    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factor, int version ){
        super(context,name,factor,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_HISTORY);
        db.execSQL(CREATE_NWEWORDBOOK);
        db.execSQL(CREATE_WORD);
        db.execSQL(CREATE_DAYWORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists History");
        db.execSQL("drop table if exists NewWordBook");
        db.execSQL("drop table if exists Word");
        db.execSQL("drop table if exists DayWord");
        onCreate(db);
    }
}
