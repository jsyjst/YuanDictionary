package cn.jsyjst.dictionary.view;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jsyjst.dictionary.ui.DaySentenceActivity;
import cn.jsyjst.dictionary.ui.HistoryActivity;
import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.ui.TranslateActivity;
import cn.jsyjst.dictionary.ui.WordBookActivity;
import cn.jsyjst.dictionary.listener.HttpCallbackListener;
import cn.jsyjst.dictionary.listener.HttpImageCallbackListener;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.https.HttpUtil;

import static cn.jsyjst.dictionary.https.HttpUtil.saveBitmap;
import static cn.jsyjst.dictionary.https.HttpUtil.savePlay;

/**
 * Created by 残渊 on 2018/4/17.
 */

public class FindFragment extends Fragment implements View.OnClickListener {
    private View mView;
    /**
     * 显示每日一句的照片
     */
    private ImageButton dayPicture;
    /**
     * 下面都是每日一句的内容
     */
    private String tts;
    private String content;
    private String note;
    private String picture;
    private String translation;
    /**
     * 日期，保存到本地的照片跟音频的文件名，作为标签
     */
    private String dateline;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.find, container, false);
        }

        return mView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /**
         * 初始化各控件
         */
        Button translate = (Button) getActivity().findViewById(R.id.btn_dictionary);
        ImageButton book = (ImageButton) getActivity().findViewById(R.id.im_book);
        ImageButton history = (ImageButton) getActivity().findViewById(R.id.im_history);
        dayPicture = (ImageButton) getActivity().findViewById(R.id.im_day_picture);
        /**
         * 把图片按比例扩大/缩小到View的宽度，居中显示
         */
        book.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        history.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        /**
         * 监听按钮
         */
        translate.setOnClickListener(this);
        book.setOnClickListener(this);
        dayPicture.setOnClickListener(this);
        history.setOnClickListener(this);

        /**
         *  初始化每日一句照片
         */
        initDayPicture();


    }

    public void initDayPicture() {
        /**
         * 获得2018-05-05格式的日期,作为保存在本地的每日一句照片的文件名
         */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = format.format(new Date());
        /**
         * 每日一句解析图片,如果本地存在本天的每日一句图片就直接从本地读取图片
         * 如果没有，则从网络获取每日一句的照片
         *
         */
        File file = new File(Environment.getExternalStorageDirectory() + "/dictionary/dayword/", nowDay + ".jpg");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            dayPicture.setImageBitmap(bitmap);
        } else {
            String address = "http://open.iciba.com/dsapi";
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
                 * 在本地没有每日一句的图片时并且没有网络时，会捕获到这个异常
                 * 从而执行这个回调方法，方法里面将之前的每日一句的布局gone掉
                 * 令事先准备好的告诉用户网络不可用的布局View出来
                 * @param e
                 */

                @Override
                public void onNetWorkError(UnknownHostException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout noNetwork = (LinearLayout) getActivity().findViewById(R.id.linear_day_sentence_no_network);
                            LinearLayout daySentence = (LinearLayout) getActivity().findViewById(R.id.linear_day_sentence);
                            daySentence.setVisibility(View.GONE);
                            noNetwork.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });
        }
    }

    /**
     * 网络解析json数据
     *
     * @param response
     */
    private void praseJSONWithJsonObject(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            content = jsonObject.getString("content");
            note = jsonObject.getString("note");
            picture = jsonObject.getString("picture2");
            translation = jsonObject.getString("translation");
            tts = jsonObject.getString("tts");
            dateline = jsonObject.getString("dateline");
            /**
             * 保存音频文件到本地
             * 这个方法在网络类中
             */
            savePlay(tts, dateline + ".mp3");

            /**
             * 保存在Dayword数据库中，dateline是用来给DayWordActivity匹配数据库中的对应天数的列
             */
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity(), "DayWord.db", null, 2);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("dateline", dateline);
            values.put("content", content);
            values.put("note", note);
            values.put("translation", translation);
            values.put("tts", tts);
            db.insert("DayWord", null, values);
            db.close();


            /**
             * 解析网上图片地址
             */
            HttpUtil.getBitmap(picture, new HttpImageCallbackListener() {
                @Override
                public void onFinish(final Bitmap bitmap) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dayPicture.setImageBitmap(bitmap);
                            /**
                             * 保存图片到本地，图片名为日期加.jpg。
                             * 这个方法在网络类中
                             */
                            saveBitmap(bitmap, dateline);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            /**
             *    跳转翻译界面按钮,即TranslateActivity
             */
            case R.id.btn_dictionary:
                Intent translateIntent = new Intent(getActivity(), TranslateActivity.class);
                startActivity(translateIntent);
                break;
            /**
             *   跳到daySentenceActivity
             */
            case R.id.im_day_picture:
                Intent dayWordIntent = new Intent(getActivity(), DaySentenceActivity.class);
                startActivity(dayWordIntent);
                break;
            /**
             *   跳转历史记录界面按钮,即HistoryActivity
             */
            case R.id.im_history:
                Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(historyIntent);
                break;
            /**
             *   跳转生词本按钮,即WorkBookActivity
             */
            case R.id.im_book:
                Intent bookIntent = new Intent(getActivity(), WordBookActivity.class);
                startActivity(bookIntent);
                break;
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