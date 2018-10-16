package cn.jsyjst.dictionary.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;

/**
 * 中文历史记录的界面
 */
public class ChineseHistoryTranslateActivity extends AppCompatActivity {

    private String wordName;
    private MyDatabaseHelper historyDbHelper;
    private SQLiteDatabase historyDb;

    private  TextView nameTv;
    private  TextView meansTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese_history_translate);

        nameTv=(TextView)findViewById(R.id.tv_chinese_history_word_name);
        meansTv=(TextView)findViewById(R.id.tv_chinese_history_word_symbol);


        /**
         * 返回
         */
        RadioButton historyBackRb = (RadioButton) findViewById(R.id.rb_chinese_history_translate_back);
        Drawable drawable_back = ContextCompat.getDrawable(this,R.drawable.radiobutton_back);
        drawable_back.setBounds(0, 0, 100, 100);
        historyBackRb.setCompoundDrawables(drawable_back, null, null, null);
        historyBackRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChineseHistoryTranslateActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });

        /**
         *  接收上一活动的数据
         */
        Intent intent = getIntent();
        wordName = intent.getStringExtra("extra_name");
        historyBackRb.setText(wordName);
        /***
         * 从数据库历史表查找
         */
        historyDbHelper = new MyDatabaseHelper(this, "Translate.db", null, 4);
        historyDb = historyDbHelper.getWritableDatabase();
        Cursor cursor=historyDb.query("History",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            if(wordName.equals(cursor.getString(cursor.getColumnIndexOrThrow("word_name")))){
                String means=cursor.getString(cursor.getColumnIndexOrThrow("means"));
                nameTv.setText(wordName);
                meansTv.setText(means);
            }
        }
    }
}
