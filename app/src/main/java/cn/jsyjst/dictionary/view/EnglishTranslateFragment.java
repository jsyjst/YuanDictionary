package cn.jsyjst.dictionary.view;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.listener.HttpCallbackListener;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;
import cn.jsyjst.dictionary.https.HttpUtil;

import static cn.jsyjst.dictionary.https.HttpUtil.savePlay;


/**
 * Created by 残渊 on 2018/4/18.
 */

public class EnglishTranslateFragment extends Fragment implements View.OnClickListener {
    /**
     * word表示查询的单词转化为小写
     */
    private String word;
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
    private String ph_en;
    private String ph_am;
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
     * i的值决定从网络或本地读取数据
     */
    private int i = 0;
    /**
     * j来记录没有对应时态的个数
     */
    private int j = 0;
    private EditText editText;
    private TextView partTv;
    private TextView wordTv;
    private RadioButton enRb;
    private RadioButton amRb;
    /**
     * 7个时态的布局
     */
    private TextView plTv, thirdTv, pastTv, doneTv, ingTv, erTv, estTv;

    private RadioButton addBookRb;
    private HistoryCrud dbHelper;
    private MyDatabaseHelper myDatabaseHelper;
    private View line;
    private MediaPlayer enPlayer;
    private MediaPlayer amPlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.english_translate_fragment, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * 初始化各个控件
         */
        addBookRb = (RadioButton) getActivity().findViewById(R.id.rb_add_new_word);
        editText = (EditText) getActivity().findViewById(R.id.edit_fan_yi);
        wordTv = (TextView) getActivity().findViewById(R.id.tv_word_play);
        partTv = (TextView) getActivity().findViewById(R.id.tv_parts);
        enRb = (RadioButton) getActivity().findViewById(R.id.rb_ph_en);
        amRb = (RadioButton) getActivity().findViewById(R.id.rb_ph_am);
        plTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordPl);
        thirdTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordThird);
        pastTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordPast);
        doneTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordDone);
        ingTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordIng);
        erTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordEr);
        estTv = (TextView) getActivity().findViewById(R.id.tv_translate_wordEst);
        /**
         * 缩小添加生词本的rb
         */
        Drawable drawable_add_book = ContextCompat.getDrawable(getActivity(),R.drawable.selector_book_image);
        drawable_add_book.setBounds(0, 0, 70, 70);
        addBookRb.setCompoundDrawables(drawable_add_book, null, null, null);
        addBookRb.setOnClickListener(this);
        /**
         * 得到文本框的单词
         */
        wordName = editText.getText().toString();
        word = wordName.trim().toLowerCase(); /**  得到输入的单词,若为大写就转化为小写 */
        /**
         * 查找数据库生词本的表
         */
        queryBook();
        /**
         * 查找word表
         */
        queryWord();
    }

    private void queryBook() {
        /**
         * 判断数据库是否有生词本的单词,从而实现翻译单词界面生词本的变化
         */
        myDatabaseHelper = new MyDatabaseHelper(getContext(), "Translate.db", null, 4);
        SQLiteDatabase bookDb = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = bookDb.query("NewWordBook", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (wordName.equals(cursor.getString(cursor.getColumnIndexOrThrow("word_name")))) {
                Drawable drawable_add_book_change = ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton_added_book);
                drawable_add_book_change.setBounds(0, 0, 80, 80);
                addBookRb.setCompoundDrawables(drawable_add_book_change, null, null, null);
                addBookRb.setText("已加入生词本");
                addBookRb.setEnabled(false);

            }
        }
        bookDb.close();
    }

    private void queryWord() {
        /**
         *     先在数据库中查找，若word数据库中有,则在数据库中直接查询
         *     数据库中没有，则表示单词为第一次查找的，则发送网络请求查询单词
         */

        SQLiteDatabase wordDb = myDatabaseHelper.getWritableDatabase();
        dbHelper = new HistoryCrud(getContext(), "Translate.db", null, 4);
        Cursor wordCursor = wordDb.query("Word", null, null, null, null, null, null);
        while (wordCursor.moveToNext()) {
            if (wordName.equals(wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_name")))) {
                ph_en = wordCursor.getString(wordCursor.getColumnIndexOrThrow("ph_en"));
                ph_am = wordCursor.getString(wordCursor.getColumnIndexOrThrow("ph_am"));
                phEnMp3 = wordCursor.getString(wordCursor.getColumnIndexOrThrow("ph_en_mp3"));
                phAmMp3 = wordCursor.getString(wordCursor.getColumnIndexOrThrow("ph_am_mp3"));
                mean = wordCursor.getString(wordCursor.getColumnIndexOrThrow("mean"));
                means = wordCursor.getString(wordCursor.getColumnIndexOrThrow("means"));
                wordPl = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_pl"));
                wordThird = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_third"));
                wordPast = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_past"));
                wordDone = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_done"));
                wordIng = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_ing"));
                wordEr = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_er"));
                wordEst = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word_est"));
                /**
                 *      在数据库查找的单词就直接保存在History表中
                 */
                dbHelper.Create("History", wordName, ph_en, ph_am, phEnMp3, phAmMp3, mean, means,
                        wordPl, wordThird, wordPast, wordDone, wordIng, wordEr, wordEst);
                /**
                 * 更新Ui
                 */
                UpdateUI();
                /**
                 * 从本地读取音频
                 * 将初始化单词音频和监听事件放在ui更新后防止某些单词获取不到网络发音时
                 * 导致程序崩溃，比如hello，you
                 */
                File enPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", wordName + "." + "phEn.mp3");
                File amPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", wordName + "." + "phAm.mp3");
                initMediaPlayer(enPlayFile.getAbsolutePath(), amPlayFile.getAbsolutePath());
                enRb.setOnClickListener(this);
                amRb.setOnClickListener(this);

                /**
                 * 令i=1；不再执行网络请求获取单词
                 */
                i = 1;
            }
        }
        if (i == 0) {
            String address = "http://dict-co.iciba.com/api/dictionary.php?w=" + word + "&type=json&key=F5133012083DD817DC4DD22FCA8B57A3";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(final String response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            praseJSONWithJsonObject(response);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                /**
                 * 如果没网并且在数据库中找不到查询单词时，会抛出这个异常
                 * 捕获到这个异常的时候，就将网络不可用的界面显示出来
                 * @param e
                 */
                @Override
                public void onNetWorkError(UnknownHostException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout translate = (LinearLayout) getActivity().findViewById(R.id.linear_translate);
                            LinearLayout noNetWork = (LinearLayout) getActivity().findViewById(R.id.linear_no_network);
                            translate.setVisibility(View.GONE);
                            noNetWork.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });

        }
    }

    private void praseJSONWithJsonObject(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            /**
             * 音标和网络发音获取
             */
            JSONArray jsonArray = jsonObject.getJSONArray("symbols");
            ph_en = jsonArray.getJSONObject(0).getString("ph_en");
            ph_am = jsonArray.getJSONObject(0).getString("ph_am");
            phEnMp3 = jsonArray.getJSONObject(0).getString("ph_en_mp3");
            phAmMp3 = jsonArray.getJSONObject(0).getString("ph_am_mp3");
            /**
             * 解析中文意思
             */
            JSONArray jsonArrayParts = jsonArray.getJSONObject(0).getJSONArray("parts");
            means = "";
            mean = "";
            for (int i = 0; i < jsonArrayParts.length(); i++) {
                JSONObject jsonObjectParts = jsonArrayParts.getJSONObject(i);
                String part = jsonObjectParts.getString("part");
                String meaning = jsonObjectParts.getString("means").replace("\"", "").replace("[", "").replace("]", "");//去掉双引号和[]符号
                mean += part + meaning;
                means += "\n" + part + "    " + meaning + "\n";
            }
            /**
             * 获取单词的各种时态
             */
            wordPl = jsonObject.getJSONObject("exchange").getString("word_pl").replace("\"", "").replace("[", "").replace("]", "");
            wordThird = jsonObject.getJSONObject("exchange").getString("word_third").replace("\"", "").replace("[", "").replace("]", "");
            wordPast = jsonObject.getJSONObject("exchange").getString("word_past").replace("\"", "").replace("[", "").replace("]", "");
            wordDone = jsonObject.getJSONObject("exchange").getString("word_done").replace("\"", "").replace("[", "").replace("]", "");
            wordIng = jsonObject.getJSONObject("exchange").getString("word_ing").replace("\"", "").replace("[", "").replace("]", "");
            wordEr = jsonObject.getJSONObject("exchange").getString("word_er").replace("\"", "").replace("[", "").replace("]", "");
            wordEst = jsonObject.getJSONObject("exchange").getString("word_est").replace("\"", "").replace("[", "").replace("]", "");

            /**
             *      将第一次从网络查询的单词保存到数据库中的Word表和History表中
             */
            dbHelper.Create("Word", wordName, ph_en, ph_am, phEnMp3, phAmMp3, mean, means,
                    wordPl, wordThird, wordPast, wordDone, wordIng, wordEr, wordEst);
            dbHelper.Create("History", wordName, ph_en, ph_am, phEnMp3, phAmMp3, mean, means,
                    wordPl, wordThird, wordPast, wordDone, wordIng, wordEr, wordEst);

            /**
             *第一次查询时将音频保存在本地
             */
            if (phEnMp3.length() != 0) {
                savePlay(phEnMp3, wordName + "." + "phEn.mp3");
            }
            if (phAmMp3.length() != 0) {
                savePlay(phAmMp3, wordName + "." + "phAm.mp3");
            }
            /**
             * 更新界面
             */
            UpdateUI();

            /**
             * 将初始化单词音频和监听事件放在ui更新后防止某些单词获取不到网络发音时
             *导致程序崩溃，比如hello，you
             */
            initMediaPlayer(phEnMp3, phAmMp3);
            enRb.setOnClickListener(this);
            amRb.setOnClickListener(this);
            /**
             * 如果捕获到jsonException,即输入的单词是无效的，就显示找不到页面
             */
        } catch (JSONException e) {
            LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.linear_translate);
            LinearLayout unfound = (LinearLayout) getActivity().findViewById(R.id.linear_unfound);
            linearLayout.setVisibility(View.GONE);
            unfound.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 首先监听两个发音的button，利用mediaPlay播放音频
     * 然后监听生词本，按下后将单词保存到数据库中Book表，并令button失效，图片改变
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_ph_en:
                enPlayer.start();
                break;
            case R.id.rb_ph_am:
                amPlayer.start();
                break;

            /**
             *   令添加按钮失效
             */
            case R.id.rb_add_new_word:
                addBookRb.setText("已加入生词本");
                addBookRb.setEnabled(false);
                dbHelper.Create("NewWordBook", wordName, ph_en, ph_am, phEnMp3, phAmMp3, mean, means,
                        wordPl, wordThird, wordPast, wordDone, wordIng, wordEr, wordEst);
                break;
            default:
                break;
        }

    }

    private void UpdateUI() {
        /**
         * 显示英文单词
         */
        wordTv.setText(wordName);
        /**
         * 显示中文意思
         */
        partTv.setText(means);
        /**
         * 显示音标，实现单词发音功能
         * 判断音标是否存在
         */
        if (ph_en.length() != 0 ) {
            enRb.setText("  英" + "/" + ph_en + "/");
        }else{
            enRb.setText(" 英");
        }
        if (ph_am.length() != 0 ) {
            amRb.setText("  美" + "/" + ph_am + "/");
        }else{
            amRb.setText("  美");
        }
        /**
         * 判断字符串的长度，若为0，则隐藏对应的时态,j用来记录时态的隐藏状态
         * 若7个时态都隐藏，即j=7时，就将分割线隐藏
         */
        line = getActivity().findViewById(R.id.view_translate_line);
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
             * 如果没有发音，则隐藏发音按钮
             */
        } catch (Exception e) {
            enRb.setVisibility(View.GONE);
            e.printStackTrace();
        }
        try {
            amPlayer = new MediaPlayer();
            amPlayer.setDataSource(amPath);
            amPlayer.prepare();
            /**
             * 如果没有发音，则隐藏发音按钮
             */
        } catch (Exception e) {
            amRb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
}

