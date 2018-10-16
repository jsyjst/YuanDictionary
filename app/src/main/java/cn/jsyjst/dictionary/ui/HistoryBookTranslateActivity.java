package cn.jsyjst.dictionary.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;

public class HistoryBookTranslateActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 英式和美式发音地址
     */
    private String phEnMp3, phAmMp3;
    /**
     * 显示在ListView上的中文意思
     */
    private String mean;
    /**
     * 详细的中文意思
     */
    private String means;
    /**
     * 英式和美式音标
     */
    private String phEn;
    private String phAm;
    /**
     * 英文单词
     */
    private String wordName;
    /**
     * 单词复数
     */
    private String wordPl;
    /**
     * 单词第三人称单数
     */
    private String wordThird;
    /**
     * 过去式
     */
    private String wordPast;
    /**
     * 过去分词
     */
    private String wordDone;
    /**
     * 现在分词
     */
    private String wordIng;
    /**
     * 比较级
     */
    private String wordEr;
    /**
     * 最高级
     */
    private String wordEst;
    /**
     * 其他活动传过来的值
     */
    private int number;

    private TextView wordNameTv;
    private RadioButton enPlayerRb, amPlayerRb;
    private RadioButton historyBackRb;
    private RadioButton addBookRb;
    private TextView meansTv;
    /**
     * 7个时态布局
     */
    private TextView plTv, thirdTv, pastTv, doneTv, ingTv, erTv, estTv;

    private MyDatabaseHelper historyDbHelper;
    private SQLiteDatabase historyDb;
    private HistoryCrud historyCrud;
    private Cursor hCursor;

    private MediaPlayer enPlayer;
    private MediaPlayer amPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_book_translate);

        /**
         * 初始化各控件
         */
        wordNameTv = (TextView) findViewById(R.id.tv_history_translate_word);
        enPlayerRb = (RadioButton) findViewById(R.id.rb_history_ph_en);
        amPlayerRb = (RadioButton) findViewById(R.id.rb_history_ph_am);
        meansTv = (TextView) findViewById(R.id.tv_history_parts);
        plTv = (TextView) findViewById(R.id.tv_history_translate_wordPl);
        thirdTv = (TextView) findViewById(R.id.tv_history_translate_wordThird);
        pastTv = (TextView) findViewById(R.id.tv_history_translate_wordPast);
        doneTv = (TextView) findViewById(R.id.tv_history_translate_wordDone);
        ingTv = (TextView) findViewById(R.id.tv_history_translate_wordIng);
        erTv = (TextView) findViewById(R.id.tv_history_translate_wordEr);
        estTv = (TextView) findViewById(R.id.tv_history_translate_wordEst);
        addBookRb = (RadioButton) findViewById(R.id.rb_history_add_new_word);
        historyBackRb = (RadioButton) findViewById(R.id.rb_history_translate_back);

        /**
         * 生词本缩小
         */
        Drawable drawable_add_book = getResources().getDrawable(R.drawable.selector_book_image);
        drawable_add_book.setBounds(0, 0, 80, 70);
        addBookRb.setCompoundDrawables(drawable_add_book, null, null, null);
        addBookRb.setOnClickListener(this);
        /**
         * 返回值缩小
         */
        Drawable drawable_back = getResources().getDrawable(R.drawable.radiobutton_back);
        drawable_back.setBounds(0, 0, 100, 100);
        historyBackRb.setCompoundDrawables(drawable_back, null, null, null);

        /**
         * 接受其他活动传过来的值
         * 1表示若没有接受到number，则number默认为1
         */
        Intent intent = getIntent();
        number = intent.getIntExtra("extra_number", 1);
        /**
         *  根据传过来的number来决定返回哪个活动，若为1，则返回HistoryActivity,反之，返回WordBookActivity;
         */
        historyBackRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number == 1) {
                    Intent intent = new Intent(HistoryBookTranslateActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HistoryBookTranslateActivity.this, WordBookActivity.class);
                    startActivity(intent);
                }
            }
        });
        /**
         *  接收上一活动的数据
         */
        wordName = intent.getStringExtra("extra_name");
        /**
         * 在数据库中查询单词
         */
        queryWord();
    }
    /**
     * 从数据库中查询单词,根据number的值来判断从哪个数据库来查询单词。
     * number=1,表示是历史记录的单词，所以还得在数据库中查询生词表看看该单词是否加入生词本，然后在历史纪录表中查询
     * 如果number不是为1，则是生词本中的单词，故直接更新添加生词本的按钮。然后再生词本表中查询单词
     */
    private void queryWord() {
        historyDbHelper = new MyDatabaseHelper(this, "Translate.db", null, 4);
        historyDb = historyDbHelper.getWritableDatabase();
        if (number == 1) {
            queryBook();
            hCursor = historyDb.query("History", null, null, null, null, null, null);
        } else {
            initAddBook();
            hCursor = historyDb.query("NewWordBook", null, null, null, null, null, null);
        }
        while (hCursor.moveToNext()) {
            if (wordName.equals(hCursor.getString(hCursor.getColumnIndexOrThrow("word_name")))) {
                phEn = hCursor.getString(hCursor.getColumnIndexOrThrow("ph_en"));
                phAm = hCursor.getString(hCursor.getColumnIndexOrThrow("ph_am"));
                phEnMp3 = hCursor.getString(hCursor.getColumnIndexOrThrow("ph_en_mp3"));
                phAmMp3 = hCursor.getString(hCursor.getColumnIndexOrThrow("ph_am_mp3"));
                mean = hCursor.getString(hCursor.getColumnIndexOrThrow("mean"));
                means = hCursor.getString(hCursor.getColumnIndexOrThrow("means"));
                wordPl = hCursor.getString(hCursor.getColumnIndexOrThrow("word_pl"));
                wordThird = hCursor.getString(hCursor.getColumnIndexOrThrow("word_third"));
                wordPast = hCursor.getString(hCursor.getColumnIndexOrThrow("word_past"));
                wordDone = hCursor.getString(hCursor.getColumnIndexOrThrow("word_done"));
                wordIng = hCursor.getString(hCursor.getColumnIndexOrThrow("word_ing"));
                wordEr = hCursor.getString(hCursor.getColumnIndexOrThrow("word_er"));
                wordEst = hCursor.getString(hCursor.getColumnIndexOrThrow("word_est"));
            }
        }
        /**
         * ui更新
         */
        updateUI();
        /**
         *历史记录和生词本的发音直接从本地读取
         * 将监听事件放在ui更新后防止某些单词获取不到网络发音时
         *导致程序崩溃，比如hello，you
         */
        File enPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", wordName + "." + "phEn.mp3");
        File amPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", wordName + "." + "phAm.mp3");
        initMediaPlayer(enPlayFile.getAbsolutePath(), amPlayFile.getAbsolutePath());
        enPlayerRb.setOnClickListener(this);
        amPlayerRb.setOnClickListener(this);
    }
    /**
     *  判断历史记录单词是否加入生词本，在数据库生词本表中查询
     */
    private void queryBook() {
        Cursor cursor = historyDb.query("NewWordBook", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (wordName.equals(cursor.getString(cursor.getColumnIndexOrThrow("word_name")))) {
                initAddBook();
            }
        }
    }

    /**
     * 改变添加生词本的按钮，令按钮失效并变化里面内容告诉用户
     */
    private void initAddBook(){
        Drawable drawable_add_book_change = ContextCompat.getDrawable(this,R.drawable.radiobutton_added_book);
        drawable_add_book_change.setBounds(0, 0, 80, 80);
        addBookRb.setCompoundDrawables(drawable_add_book_change, null, null, null);
        addBookRb.setText("已加入生词本");
        addBookRb.setEnabled(false);
    }

    private void updateUI() {

        historyBackRb.setText(wordName);
        wordNameTv.setText(wordName);
        meansTv.setText(means);
        /**
         * 判断单词是否有音标
         */
        if (phAm.length() != 0) {
            enPlayerRb.setText("  英 " + "/" + phEn + "/");
        } else {
            enPlayerRb.setText("  英");
        }
        if (phAm.length() != 0) {
            amPlayerRb.setText("  美 " + "/" + phAm + "/");
        } else {
            amPlayerRb.setText("  美");
        }

        /**
         * 判断字符串的长度，若为0，则隐藏对应的时态,j用来记录时态的隐藏状态
         * 若7个时态都隐藏，即j=7时，就将分割线隐藏
         */
        int j = 0;
        View line = findViewById(R.id.view_history_translate_line);
        if (wordPl.length() != 0) {
            plTv.setText("复数 :  " + wordPl);
        } else {
            plTv.setVisibility(View.GONE);
            j++;
        }
        if (wordThird.length() != 0) {
            thirdTv.setText("第三人称单数 :  " + wordThird);
        } else {
            thirdTv.setVisibility(View.GONE);
            j++;
        }
        if (wordPast.length() != 0) {
            pastTv.setText("过去式 :  " + wordPast);
        } else {
            pastTv.setVisibility(View.GONE);
            j++;
        }
        if (wordDone.length() != 0) {
            doneTv.setText("过去分词 :  " + wordPast);
        } else {
            doneTv.setVisibility(View.GONE);
            j++;
        }
        if (wordIng.length() != 0) {
            ingTv.setText("现在分词 :  " + wordIng);
        } else {
            ingTv.setVisibility(View.GONE);
            j++;
        }
        if (wordEr.length() != 0) {
            erTv.setText("比较级 :  " + wordEr);
        } else {
            erTv.setVisibility(View.GONE);
            j++;
        }
        if (wordEst.length() != 0) {
            estTv.setText("最高级 :  " + wordEst);
        } else {
            estTv.setVisibility(View.GONE);
            j++;
        }
        if (j == 7) {
            line.setVisibility(View.GONE);
        }

    }

    private void initMediaPlayer(String enPath, String amPath) {
        try {
            enPlayer = new MediaPlayer();
            enPlayer.setDataSource(enPath);
            enPlayer.prepare();
            /**
             * 如果没有发音就隐藏
             */
        } catch (Exception e) {
            enPlayerRb.setVisibility(View.GONE);
            e.printStackTrace();
        }
        try {
            amPlayer = new MediaPlayer();
            amPlayer.setDataSource(amPath);
            amPlayer.prepare();
            /**
             * 如果没有发音就隐藏
             */
        } catch (Exception e) {
            amPlayerRb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    /**
     * 获取单词发音
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_history_ph_en:
                enPlayer.start();
                break;
            case R.id.rb_history_ph_am:
                amPlayer.start();
                break;
            case R.id.rb_history_add_new_word:
                addBookRb.setText("已加入生词本");
                addBookRb.setEnabled(false);
                historyCrud = new HistoryCrud(this, "Translate.db", null, 4);
                historyCrud.Create("NewWordBook", wordName, phEn, phAm, phEnMp3, phAmMp3, mean, means,
                        wordPl, wordThird, wordPast, wordDone, wordIng, wordEr, wordEst);
                break;
            default:
                break;
        }
    }
}