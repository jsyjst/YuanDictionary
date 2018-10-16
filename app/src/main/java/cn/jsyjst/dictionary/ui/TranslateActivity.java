package cn.jsyjst.dictionary.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.view.ChineseTranslateFragment;
import cn.jsyjst.dictionary.view.EnglishTranslateFragment;
import cn.jsyjst.dictionary.view.TranslateHistoryFragment;

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edit;
    private ImageView lineIv;
    private ImageButton deleteIm;
    private ImageButton seekIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        /**
         * 初始化布局
         */
        ImageButton back = (ImageButton) findViewById(R.id.im_back);
        edit = (EditText) findViewById(R.id.edit_fan_yi);
        deleteIm = (ImageButton) findViewById(R.id.im_delete_word);
        lineIv = (ImageView) findViewById(R.id.iv_line);
        seekIm = (ImageButton) findViewById(R.id.im_seek);
        /**
         * 把图片按比例扩大/缩小到View的宽度，居中显示
         */
        back.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        deleteIm.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        seekIm.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        /**
         * 监听按钮
         */
        back.setOnClickListener(this);
        deleteIm.setOnClickListener(this);
        seekIm.setOnClickListener(this);

        /**
         * TranslateActivity生成后默认让历史记录的fragment显示出来
         */
        replaceFragment(new TranslateHistoryFragment());
        /**
         *  输入后seek,delete,line按钮显示，否则隐藏
         */
        visibility();
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.im_back:
                Intent intent = new Intent(TranslateActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.im_delete_word:
                edit.getText().clear();
                /**
                 * 删除后显示虚拟键盘
                 */
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.im_seek:
                /**
                 *  跳转后软键盘隐藏
                 */
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                /**
                 * 判断输入的是中文还是英文，从而跳转不同的fragment
                 * 根据输入的第一个字符来判断
                 */
                String word = edit.getText().toString().substring(0,1);
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
                 * 如果输入的是数字就提示输入单词和中文
                 */
                p=Pattern.compile("[0-9]");
                m=p.matcher(word);
                if(m.matches()){
                    Toast.makeText(this,"目前只支持中英互译，请输入单词或中文",Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * 动态变化碎片的方法
     * @param fragment
     */

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_translate, fragment);
        transaction.commit();

    }

    /**
     *  监听EditText输入过程，在onTextChanged中添加是为了更详细的监听输入
     */
    private void visibility(){
        seekIm.setVisibility(View.INVISIBLE);
        lineIv.setVisibility(View.INVISIBLE);
        deleteIm.setVisibility(View.INVISIBLE);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /**
                 * 判断输入不为空，按钮显示
                 */
                if (edit.length() != 0) {
                    seekIm.setVisibility(View.VISIBLE);
                    deleteIm.setVisibility(View.VISIBLE);
                    lineIv.setVisibility(View.VISIBLE);

                } else {
                    seekIm.setVisibility(View.INVISIBLE);
                    deleteIm.setVisibility(View.INVISIBLE);
                    lineIv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }
}
