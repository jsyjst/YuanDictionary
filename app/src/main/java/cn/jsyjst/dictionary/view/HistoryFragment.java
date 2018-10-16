package cn.jsyjst.dictionary.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jsyjst.dictionary.ui.HistoryActivity;
import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.ui.TranslateActivity;
import cn.jsyjst.dictionary.ui.WordBookActivity;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;


/**
 * Created by 残渊 on 2018/4/17.
 */

public class HistoryFragment extends Fragment implements  View.OnClickListener{
    private View mView;
    /**
     * 生词本个数
     */
    private int newWordNum;
    /**
     * 历史记录个数
     */
    private int historyNum;
    public static Handler handler;
    public static final int UPDATE_TEXT=1;
    private Button bookNumBtn;
    private Button hisNumBtn;
    private Button historyBtn;
    private  Button wordBookBtn;
    private  TextView newWordNumberTv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.history, container, false);
        }


        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        newWordNumberTv = (TextView) getActivity().findViewById(R.id.tv_new_word_number);
        historyBtn = (Button) getActivity().findViewById(R.id.btn_history);
        wordBookBtn = (Button) getActivity().findViewById(R.id.btn_book);
        ImageButton translate = (ImageButton) getActivity().findViewById(R.id.im_translate);
        translate.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bookNumBtn=(Button)getActivity().findViewById(R.id.btn_book_num);
        hisNumBtn =(Button)getActivity().findViewById(R.id.btn_history_num);
        /**
         * 监听历史记录，生词本，翻译和历史记录个数，生词本个数
         */
        historyBtn.setOnClickListener(this);
        wordBookBtn.setOnClickListener(this);
        bookNumBtn.setOnClickListener(this);
        hisNumBtn.setOnClickListener(this);
        translate.setOnClickListener(this);



    }


    /**
     * 利用onstart的方法来更新生词本和历史记录的单词个数
     */

    @Override
    public void onStart(){
        super.onStart();



        /**
         * 显示生词本和历史记录的单词个数
         * 使用了异步消息处理机制来更新单词个数。若发现Message的what字段为UPDATE_TEXT时，就在NewWordBook和history表中查询，
         * newWordNum用来记录生词本单词个数
         * historyNUM用来记录历史记录个数
         */

        handler= new Handler(){
            public void handleMessage(Message msg){
                switch(msg.what){
                    case UPDATE_TEXT:
                        newWordNum=0;
                        historyNum=0;

                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext(), "Translate.db", null, 4);
                        SQLiteDatabase wordDb = dbHelper.getWritableDatabase();

                        Cursor cursor = wordDb.query("NewWordBook", null, null, null, null, null, null, null);
                        while (cursor.moveToNext()) {
                            newWordNum++;
                        }
                        /**
                         * 类型转换
                         */
                        newWordNumberTv.setText(String.valueOf(newWordNum));
                        bookNumBtn.setText(String.valueOf(newWordNum));

                        /**
                         * 查询history表
                         */
                        Cursor historyCursor=wordDb.query("History",null,null,null,null,null,null,null);
                        while (historyCursor.moveToNext()){
                            historyNum++;
                        }
                        hisNumBtn.setText(String.valueOf(historyNum));

                        /**
                         * 如果生词本和历史记录为空的话就设置不可点击
                         */
                        if(historyNum==0){
                            historyBtn.setEnabled(false);
                            hisNumBtn.setEnabled(false);
                        }else{
                            historyBtn.setEnabled(true);
                            hisNumBtn.setEnabled(true);
                        }
                        if(newWordNum==0){
                            bookNumBtn.setEnabled(false);
                            wordBookBtn.setEnabled(false);
                        }else{
                            bookNumBtn.setEnabled(true);
                            wordBookBtn.setEnabled(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        /**
         * 开了个线程来将message传给HistoryFragment
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=UPDATE_TEXT;
                handler.sendMessage(message);
            }
        }).start();


    }

    public void onClick(View v){
        switch(v.getId()){
            /**
             * 点击生词本或最右边的个数时会跳转到生词本界面,即WordBookActivity
             */
            case R.id.btn_book:
            case R.id.btn_book_num:
                Intent bookIntent = new Intent(getActivity(), WordBookActivity.class);
                startActivity(bookIntent);
                break;
            /**
             * 点击历史记录或历史记录最右边的个数时跳转到历史记录界面,即HistoryActivity
             */
            case R.id.btn_history:
            case R.id.btn_history_num:
                Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(historyIntent);
                break;
            /**
             * 跳转到翻译界面，即TranslateActivity
             */
            case R.id.im_translate:
                Intent translateIntent = new Intent(getActivity(),TranslateActivity.class);
                startActivity(translateIntent);
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mView.getParent()).removeView(mView);
    }

}
