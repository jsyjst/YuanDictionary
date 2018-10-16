package cn.jsyjst.dictionary.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.db.MyDatabaseHelper;

public class DaySentenceActivity extends AppCompatActivity {

    private TextView noteTv;
    private TextView contentTv;
    private RadioButton backRb;
    /**
     * 每日一句背景布局
     */
    private LinearLayout sentenceImage;
    private ImageButton playerIm;
    private MediaPlayer mediaPlayer;
    private TextView datelineTv;
    private TextView commentTv;
    /**
     * 每日一句英文内容
     */
    private String content;
    /**
     * 每日一句中文内容
     */
    private String note;
    /**
     * 每日一句词霸小编
     */
    private String translation;
    /**
     * 当前日期。格式为yyyy-MM-dd
     */
    private String nowDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_sentence);
        /**
         * 初始化各控件
         */
        backRb = (RadioButton) findViewById(R.id.rb_day_sentence_back);
        sentenceImage = (LinearLayout) findViewById(R.id.layout_day_sentence);
        playerIm = (ImageButton) findViewById(R.id.im_sentence_player);
        contentTv = (TextView) findViewById(R.id.tv_sentence_content);
        noteTv = (TextView) findViewById(R.id.tv_sentence_note);
        datelineTv = (TextView) findViewById(R.id.tv_sentence_dateline);
        commentTv = (TextView) findViewById(R.id.tv_translation);

        /**
         *获得2018-05-05格式的string,便于与数据库中的dateline匹配
         * 从而显示出每日一句,同时作为每日一句照片和音频的标签,查找本地文件中的音频和照片
         */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        nowDay = format.format(new Date());

        /**
         * 返回到主活动
         */
        Drawable drawable_day = getResources().getDrawable(R.drawable.radiobutton_back);
        drawable_day.setBounds(0, 0, 100, 100);
        backRb.setCompoundDrawables(drawable_day, null, null, null);
        backRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DaySentenceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 从每日一句的数据库中查找每日一句
         */
        queryDaySentence();

        /**
         * 每日一句发音
         */
        playerIm.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        playerIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }

            }
        });


    }
    private void queryDaySentence(){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "DayWord.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("DayWord", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (nowDay.equals(cursor.getString(cursor.getColumnIndexOrThrow("dateline")))) {
                content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                translation = cursor.getString(cursor.getColumnIndexOrThrow("translation"));
                /**
                 * 更新ui
                 */
                updateUI();
                /**
                 * 初始化音频
                 */
                initMediaPlayer();

            }
        }
    }

    private void updateUI() {
        /**
         * 从本地读取每日一句的图片
         */

        File file = new File(Environment.getExternalStorageDirectory() + "/dictionary/dayword/", nowDay + ".jpg");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            sentenceImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        /**
         * 显示每日一句的中英内容和词霸小编
         */
        contentTv.setText(content);
        noteTv.setText(note);
        commentTv.setText(translation);

        /**
         * 获取当前日期，显示出来
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        Date now = new Date();
        String time = dateFormat.format(now);
        datelineTv.setText(time);

    }

    private void initMediaPlayer() {
        try {
            File playFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", nowDay + ".mp3");
            if (playFile.exists()) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(playFile.getPath());
                mediaPlayer.prepare();
            }
            /**
             * 如果金山api网络音频出现错误，则在异常中告诉用户
             */
        } catch (Exception e) {
            Toast.makeText(this, "连接服务器失败，获取不到音频，请稍后再试", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}

