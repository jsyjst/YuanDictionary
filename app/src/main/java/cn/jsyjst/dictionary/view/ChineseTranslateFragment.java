package cn.jsyjst.dictionary.view;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.listener.HttpCallbackListener;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;
import cn.jsyjst.dictionary.https.HttpUtil;

/**
 * Created by 残渊 on 2018/5/6.
 */

public class ChineseTranslateFragment extends Fragment {

    private EditText edit;
    private TextView nameTv;
    private TextView symbolsTv;
    private String wordName;
    private String wordSymbol;

    /**
     * means为翻译界面的翻译意思
     */
    private String means = "";
    /**
     * mean为历史记录的翻译意思
     */
    private String mean = "";
    private HistoryCrud dbHelper;
    /**
     * i用来判断到底是从网络获取还是从数据库读取翻译内容
     */
    private int i = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chinese_translate_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        /**
         * 初始化各控件
         */
        edit = (EditText) getActivity().findViewById(R.id.edit_fan_yi);
        nameTv = (TextView) getActivity().findViewById(R.id.tv_chinese_word_name);
        symbolsTv = (TextView) getActivity().findViewById(R.id.tv_chinese_word_symbol);
        queryTranslate();

    }
    private void queryTranslate(){

        /**
         * 得到查询的单词
         */
        wordName = edit.getText().toString();
        /**
         *     先在数据库中查找，若word数据库中有,则在数据库中直接查询
         *     数据库中没有，则表示单词为第一次查找的，则发送网络请求查询单词
         */

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext(), "Translate.db", null, 4);
        SQLiteDatabase Db = myDatabaseHelper.getWritableDatabase();
        dbHelper = new HistoryCrud(getContext(), "Translate.db", null, 4);
        Cursor cursor = Db.query("Word", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (wordName.equals(cursor.getString(cursor.getColumnIndexOrThrow("word_name")))) {
                means = cursor.getString(cursor.getColumnIndexOrThrow("means"));
                mean = cursor.getString(cursor.getColumnIndexOrThrow("mean"));
                /**
                 *      在数据库查找的单词就直接保存在History表中
                 */
                dbHelper.Create("History", wordName, null, null, null, null, mean, means,
                        null, null, null, null, null, null, null);
                /**
                 * 更新UI
                 */
                updateUI();
                /**
                 * 令i=1;不再执行联网操作
                 */
                i = 1;
            }
        }
        if (i == 0) {
            String address = "http://dict-co.iciba.com/api/dictionary.php?w=" + wordName + "&type=json&key=F5133012083DD817DC4DD22FCA8B57A3";
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
                 *
                 * @param e
                 */
                @Override
                public void onNetWorkError(UnknownHostException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout translate = (LinearLayout) getActivity().findViewById(R.id.linear_chinese_translate);
                            LinearLayout noNetWork = (LinearLayout) getActivity().findViewById(R.id.linear_chinese_no_network);
                            translate.setVisibility(View.GONE);
                            noNetWork.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });
        }
    }

    /**
     * 联网解析金山api
     *
     * @param response
     */
    private void praseJSONWithJsonObject(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray ja = jsonObject.getJSONArray("symbols");
            for (int i = 0; i < ja.length(); i++) {
                wordSymbol = ja.getJSONObject(i).getString("word_symbol");
                if (wordSymbol.length() != 0) {
                    means += "[ " + wordSymbol + " ]" + "\n\n";
                }
                JSONArray jaParts = ja.getJSONObject(i).getJSONArray("parts");
                for (int j = 0; j < jaParts.length(); j++) {
                    String part_name = jaParts.getJSONObject(j).getString("part_name");
                    if (part_name.length() != 0) {
                        mean += part_name + "  ";
                        means += part_name + "  ";
                    }

                    JSONArray jaMeans = jaParts.getJSONObject(j).getJSONArray("means");
                    for (int k = 0; k < jaMeans.length(); k++) {
                        String word_mean = jaMeans.getJSONObject(k).getString("word_mean");
                        mean += word_mean + " ; ";
                        means += word_mean + " ; ";
                        if (k == jaMeans.length() - 1) {
                            means += "\n\n";
                        }
                    }
                }
            }
            /**
             *      将第一次从网络查询的单词保存到数据库中的Word表和History表中
             */

            dbHelper.Create("History", wordName, null, null, null, null, mean, means,
                    null, null, null, null, null, null, null);
            dbHelper.Create("Word", wordName, null, null, null, null, mean, means,
                    null, null, null, null, null, null, null);

            /**
             * ui处理
             */
            updateUI();
            /**
             * 如果从金山api获取不到意思的话，会抛出JSONException的异常
             * 如果捕获到jsonException,即输入的单词是无效的，就显示找不到页面
             */

        } catch (JSONException e) {
            LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.linear_chinese_translate);
            LinearLayout unfound = (LinearLayout) getActivity().findViewById(R.id.linear_chinese_unfound);
            linearLayout.setVisibility(View.GONE);
            unfound.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void updateUI(){
        nameTv.setText(wordName);
        symbolsTv.setText(means);
    }

}
