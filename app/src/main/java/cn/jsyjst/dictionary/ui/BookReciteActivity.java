package cn.jsyjst.dictionary.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.adapter.BookCardAdapter;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.entity.BookCard;
import cn.jsyjst.dictionary.widget.MyListView;

/**
 * 生词本卡片背词功能
 */
public class BookReciteActivity extends AppCompatActivity {

    private List<BookCard> bookList= new ArrayList<>();
    private List<BookCard> tempList=new ArrayList<>();
    private MyListView listView;
    private boolean isTouch=false;
    private  BookCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recite);

        listView=(MyListView) findViewById(R.id.lv_book_card);
        RadioButton backRb=(RadioButton)findViewById(R.id.rb_book_card_back);


        Drawable drawable_back = getResources().getDrawable(R.drawable.radiobutton_back);
        drawable_back.setBounds(0, 0, 100, 100);
        backRb.setCompoundDrawables(drawable_back, null, null, null);
        backRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookReciteActivity.this,WordBookActivity.class);
                startActivity(intent);
            }
        });

        queryBook();
    }

    /**
     * 从生词本的数据库中查找并太添加到list中
     */
    private void queryBook(){
        MyDatabaseHelper dbHelper=new MyDatabaseHelper(this,"Translate.db",null,4);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor = db.query("NewWordBook",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("word_name"));
            String means = cursor.getString(cursor.getColumnIndexOrThrow("means"));
            String phEn = cursor.getString(cursor.getColumnIndexOrThrow("ph_en"));
            String phAm = cursor.getString(cursor.getColumnIndexOrThrow("ph_am"));
            BookCard bookCard=new BookCard(name,means,phEn,phAm);
            tempList.add(bookCard);
            bookList.clear();
            for (int i = tempList.size() - 1; i >= 0; i--) {
                bookList.add(tempList.get(i));
                db.close();
            }
        }
        adapter=new BookCardAdapter(this,R.layout.book_card_item,bookList);
        listView.setAdapter(adapter);
        /**
         * item的监听，点击改变bookCard的isTouch值
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookCard bookCard=bookList.get(position);
                isTouch=!isTouch;
                bookCard.setIsTouch(isTouch);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
