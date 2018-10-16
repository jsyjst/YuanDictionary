package cn.jsyjst.dictionary.view;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.adapter.SeekHistoryAdapter;
import cn.jsyjst.dictionary.db.db.biz.HistoryCrud;
import cn.jsyjst.dictionary.entity.History;


import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by 残渊 on 2018/4/18.
 */

public class TranslateHistoryFragment extends Fragment {
    private HistoryCrud dbHelper;
    private ArrayAdapter<History> adapter;
    private EditText editText;
    private List<History> historyList;
    private RelativeLayout allDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translate_history_fragment, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         *    显示历史记录
         */
        dbHelper = new HistoryCrud(getActivity(), "Translate.db", null, 4);
        historyList = dbHelper.getHistoryList();
        /**
         * 检查历史记录是否为空
         */
        checkHistorySize();
        adapter = new SeekHistoryAdapter(getActivity(), R.layout.seek_history_item, historyList);
        ListView listView = (ListView) getActivity().findViewById(R.id.lv_history);
        listView.setAdapter(adapter);

        /**
         *   监听ListView
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /**
                 * 获得点击的item取值,获取到历史记录的单词
                 */
                History history = historyList.get(position);
                editText = (EditText) getActivity().findViewById(R.id.edit_fan_yi);
                editText.setText(history.getmWordName());

                /**
                 *   将光标移到最后，参数设置成EditText输入框中字符的长度，光标则为最后
                 */
                editText.setSelection(editText.getText().length());
                /**
                 * 判断历史记录是中文还是英文
                 * 点击item时跳转到对应翻译的fragment
                 */
                String word = editText.getText().toString().substring(0,1);
                Pattern p = Pattern.compile("[a-zA-Z]");
                Matcher m = p.matcher(word);
                if (m.matches()) {
                    replaceFragment(new EnglishTranslateFragment());
                }
                p=Pattern.compile("[\\u4e00-\\u9fa5]");
                m=p.matcher(word);
                if(m.matches()){
                    replaceFragment(new ChineseTranslateFragment());
                }
                /**
                 *   如果跳转到翻译界面的时候将软键盘隐藏
                 */

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


        /**
         * 缩放垃圾桶图片
         */
        ImageButton delete = (ImageButton) getActivity().findViewById(R.id.im_all_history_delete);
        delete.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        /**
         *  清空历史记录功能
         */
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allHistoryDel();
            }
        });
    }


    /**
     * 检查历史记录是否为空,若为空，则隐藏历史记录和垃圾桶的显示
     */

    public void checkHistorySize() {
        allDelete = (RelativeLayout) getActivity().findViewById(R.id.RL_allDelete);
        if (historyList.size() == 0) {
            allDelete.setVisibility(View.GONE);
        } else {
            allDelete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 清空历史记录的方法
     */
    public void allHistoryDel() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        /**
         * 点击ProgressDialog以外的区域也可以让ProgressDialog dismiss掉。
         * 但有时我们不希望是这样的效果",可以直接使用setCanceledOnTouchOutside(false);
         */
        dialog.setTitle("确认要删除吗？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            /**
             * 点击“确认”后的操作，调用HistoryCurd的deleteAllHistory的方法
             */

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteAllhistory("History");
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_translate, fragment);
        transaction.commit();
    }
}
