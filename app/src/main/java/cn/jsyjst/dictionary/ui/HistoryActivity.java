package cn.jsyjst.dictionary.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.adapter.HistoryAdapter;
import cn.jsyjst.dictionary.listener.DelButtonClickListener;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;
import cn.jsyjst.dictionary.entity.History;
import cn.jsyjst.dictionary.widget.DelListView;

import static android.R.attr.button;

public class HistoryActivity extends AppCompatActivity {
    private List<History> historyList = new ArrayList<>();
    private HistoryAdapter adapter;
    private HistoryCrud historyCrud;
    private Intent intent;
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
        setContentView(R.layout.activity_history);

        /**
         * 返回到上个fragment
         */
        RadioButton historyBack = (RadioButton) findViewById(R.id.rb_history_back);
        hideBtn = (Button) findViewById(R.id.btn_history_means_hide);

        Drawable drawable_back = ContextCompat.getDrawable(this,R.drawable.radiobutton_back);
        drawable_back.setBounds(0, 0, 100, 100);
        historyBack.setCompoundDrawables(drawable_back, null, null, null);
        historyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 调用HistoryCurd中的getHistoryList方法
         */
        historyCrud = new HistoryCrud(HistoryActivity.this, "Translate.db", null, 4);
        historyList = historyCrud.getHistoryList();

        adapter = new HistoryAdapter(HistoryActivity.this, R.layout.history_item, historyList);
        DelListView listView = (DelListView) findViewById(R.id.lv_history);
        listView.setAdapter(adapter);
        /**
         * 清空历史记录
         */
        Button allDelBtn = (Button) findViewById(R.id.btn_history_delete);
        allDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allHistoryDel();
            }
        });

        /**
         * 滑动删除单个单词
         */
        listView.setDelButtonClickListener(new DelButtonClickListener() {
            @Override
            public void clickHappend(int position) {
                History history = historyList.get(position);
                String word = history.getmWordName();
                historyCrud.isHasHistory("History", word);
                adapter.remove(adapter.getItem(position));
                adapter.notifyDataSetChanged();
            }
        });

        /**
         * 子项点击效果
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 *  获得点击的item取值
                 */
                History history = historyList.get(position);

                /**
                 * 判断历史记录是中文还是英文
                 * 点击item时跳转到对应翻译的fragment
                 */
                String word = history.getmWordName().substring(0, 1);
                Pattern p = Pattern.compile("[a-zA-Z]");
                Matcher m = p.matcher(word);
                if (m.matches()) {
                    intent = new Intent(HistoryActivity.this, HistoryBookTranslateActivity.class);
                }
                p = Pattern.compile("[\\u4e00-\\u9fa5]");
                m = p.matcher(word);
                if (m.matches()) {
                    intent = new Intent(HistoryActivity.this, ChineseHistoryTranslateActivity.class);
                }

                /**
                 * 向下个活动传递数据
                 */
                intent.putExtra("extra_name", history.getmWordName());
                startActivity(intent);
            }
        });
        /**
         * 隐藏释义
         */
        hideMeans();
    }

    private void allHistoryDel() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoryActivity.this);
        dialog.setTitle("确认要清空所有历史记录吗");
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
                historyCrud.deleteAllhistory("History");
                adapter.notifyDataSetChanged();
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

    private void hideMeans() {
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHide = !isHide;
                adapter.hideHistoryMeans(isHide);
                if (isHide) {
                    hideBtn.setText("显示释义");
                }else{
                    hideBtn.setText("隐藏释义");
                }
            }
        });
    }

}
