package cn.jsyjst.dictionary.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.adapter.BookAdapter;
import cn.jsyjst.dictionary.listener.DelButtonClickListener;
import cn.jsyjst.dictionary.listener.OnWordsChangeListener;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;
import cn.jsyjst.dictionary.entity.Book;
import cn.jsyjst.dictionary.widget.DelListView;
import cn.jsyjst.dictionary.widget.IndexView;

public class WordBookActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Book> bookList = new ArrayList<>();
    private List<Book> tempList = new ArrayList<>();
    private SQLiteDatabase bookDb;
    private BookAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private Cursor cursor;
    /**
     * 自定义的索引控件
     */
    private IndexView indexView;
    /**
     * 自定义的带有左滑删除的ListView控件
     */
    private DelListView listView;
    /**
     * 索引后会出现在中央的字母
     */
    private TextView circleWord;
    private Handler handler;

    /**
     * 用来判断是否隐藏adapter中的means
     */
    private boolean isHide = false;
    /**
     * 隐藏和显示释义
     */
    private Button hideBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book);

        /**
         * 初始化各控件
         */
        RadioButton wordBookBack = (RadioButton) findViewById(R.id.rb_wordBook_back);
        Button wordSort = (Button) findViewById(R.id.btn_word_sort);
        Button timeSort = (Button) findViewById(R.id.btn_time_sort);
        Button bookDel = (Button) findViewById(R.id.btn_book_delete);
        Button bookReciteBtn=(Button)findViewById(R.id.btn_book_recite);
        indexView = (IndexView) findViewById(R.id.sideBar);
        circleWord = (TextView) findViewById(R.id.tv_circle_word);
        hideBtn = (Button)findViewById(R.id.btn_book_means_hide);

        /**
         * 缩小返回Rb
         */
        Drawable drawable_back = ContextCompat.getDrawable(this,R.drawable.radiobutton_back);
        drawable_back.setBounds(0, 0, 100, 100);
        wordBookBack.setCompoundDrawables(drawable_back, null, null, null);
        /**
         * 监听两个排序功能的按钮,返回,隐藏释义和清空按钮
         */
        bookDel.setOnClickListener(this);
        wordSort.setOnClickListener(this);
        timeSort.setOnClickListener(this);
        wordBookBack.setOnClickListener(this);
        bookReciteBtn.setOnClickListener(this);
        hideBtn.setOnClickListener(this);

        /**
         * 对生词本的单词进行初始化
         */
        initBook();
        /**
         * 适配
         */
        adapter = new BookAdapter(WordBookActivity.this, R.layout.history_item, bookList);
        listView = (DelListView) findViewById(R.id.lv_book);
        listView.setAdapter(adapter);

        /**
         * 左滑删除单个单词功能
         */
        listView.setDelButtonClickListener(new DelButtonClickListener() {
            @Override
            public void clickHappend(int position) {
                Book book = bookList.get(position);
                String word = book.getmWordName();
                HistoryCrud historyCrud = new HistoryCrud(WordBookActivity.this, "Translate.db", null, 4);
                historyCrud.isHasHistory("NewWordBook", word);
                adapter.remove(adapter.getItem(position));
            }
        });
        /**
         * 子项的点击效果
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 *  获得点击的item取值
                 */
                Book book = bookList.get(position);
                Intent intent = new Intent(WordBookActivity.this, HistoryBookTranslateActivity.class);
                /**
                 * 向historyTranslate活动传递数据，传了两个数据
                 * 一个为String。便于在historyTranslate活动在数据库中查找单词
                 * 一个为int型。给historyTranslate活动识别是要在哪个表中查找数据
                 */
                intent.putExtra("extra_name", book.getmWordName());
                intent.putExtra("extra_number", 2);
                startActivity(intent);
            }
        });
        /**
         * 索引功能的实现
         */
        indexView.setOnWordsChangeListener(new OnWordsChangeListener() {
            @Override
            public void wordsChange(String words) {
                /**
                 * 更新中央字母
                 */
                updateWord(words);

                for (int i = 0; i < bookList.size(); i++) {
                    Book book = bookList.get(i);
                    String headWord = book.getmHeadWord();
                    /**
                     * 将手指按下的字母与列表中相同字母(不分大小写)开头的项找出来
                     */
                    if (words.equalsIgnoreCase(headWord)) {
                        /**
                         * 在列表中匹配
                         */
                        listView.setSelection(i);
                    }
                }
            }
        });

    }
    public void initBook() {
        dbHelper = new MyDatabaseHelper(WordBookActivity.this, "Translate.db", null, 4);
        bookDb = dbHelper.getWritableDatabase();
        cursor = bookDb.query("NewWordBook", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String wordName = cursor.getString(cursor.getColumnIndexOrThrow("word_name"));
            String mean = cursor.getString(cursor.getColumnIndexOrThrow("mean"));
            Book book = new Book(wordName,mean);
            tempList.add(book);
        }
        /**
         * 默认排序为时间排序
         */
        timeSort();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_time_sort:
                tempList.clear();
                initBook();
                indexView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_word_sort:
                wordSort();
                indexView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_book_delete:
                /**
                 * 弹出删除对话框的方法
                 */
                allBookDel();
                break;
            case R.id.rb_wordBook_back:
                Intent intent = new Intent(WordBookActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_book_means_hide:
                isHide=!isHide;
                adapter.hideBookMeans(isHide);
                if(isHide){
                    hideBtn.setText("显示释义");
                }else{
                    hideBtn.setText("隐藏释义");
                }
                break;
            case R.id.btn_book_recite:
                Intent bookReciteIntent=new Intent(WordBookActivity.this,BookReciteActivity.class);
                startActivity(bookReciteIntent);
            default:
                break;
        }
    }

    /**
     * 更新中央的字母提示
     */
    private void updateWord(String words) {
        circleWord.setText(words);
        circleWord.setVisibility(View.VISIBLE);
        /**
         *  清空之前的所有消息
         */
        handler = new Handler();
        handler.removeCallbacksAndMessages(null);
        /**
         *  500ms后让tv隐藏
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleWord.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 时间排序
     */
    public void timeSort() {
        bookList.clear();
        for (int i = tempList.size() - 1; i >= 0; i--) {
            bookList.add(tempList.get(i));
            bookDb.close();
        }

    }
    /**
     * 字母排序
     */
    public void wordSort() {
        /**
         * 对集合排序
         */
        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book book1, Book book2) {
                /**
                 *  根据单词的首字母大写比较进行排序
                 */
                String s1 = book1.getmHeadWord().toUpperCase();
                String s2 = book2.getmHeadWord().toUpperCase();
                return s1.compareTo(s2);
            }
        });
    }
    private void allBookDel() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(WordBookActivity.this);
        dialog.setTitle("确认要清空所有生词吗");
        /**
         * 点击ProgressDialog以外的区域也可以让ProgressDialog dismiss掉。
         * 但有时我们不希望是这样的效果",可以直接使用setCancelable(false);
         */
        dialog.setCancelable(false);
        dialog.setPositiveButton("清空", new DialogInterface.OnClickListener() {


            /**
             * 点击“确认”后的操作，调用HistoryCurd的deleteAllHistory的方法
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HistoryCrud historyCrud = new HistoryCrud(WordBookActivity.this, "Translate.db", null, 4);
                historyCrud.deleteAllhistory("NewWordBook");
                /**
                 * 用adapter.notifyDataSetChanged()并不能达到效果，
                 * 故使用了clear的方法
                 */
                adapter.clear();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            /**
             *  点击“返回”后的操作,这里不设置没有任何操作
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}