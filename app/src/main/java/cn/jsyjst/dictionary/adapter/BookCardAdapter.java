package cn.jsyjst.dictionary.adapter;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.entity.BookCard;

/**
 * Created by 残渊 on 2018/5/10.
 */


/**
 * 生词本功能适配器
 */
public class BookCardAdapter extends ArrayAdapter<BookCard> {

    private int resourceId;
    private MediaPlayer  enPlayer;
    private MediaPlayer  amPlayer;

    public BookCardAdapter(Context context, int textViewResourceId, List<BookCard> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BookCard bookcard = getItem(position);

        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookName = (TextView) view.findViewById(R.id.tv_book_card_word_name);
            viewHolder.phEn = (RadioButton) view.findViewById(R.id.rb_book_card_ph_en);
            viewHolder.phAm = (RadioButton) view.findViewById(R.id.rb_book_card_ph_am);
            viewHolder.bookMeans = (TextView) view.findViewById(R.id.tv_book_card_means);
            viewHolder.bookHint = (TextView)view.findViewById(R.id.tv_book_card_hint) ;
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.bookName.setText(bookcard.getmWordName());
        if (bookcard.getmPhEn().length() != 0) {
            viewHolder.phEn.setText("  英" + "/" + bookcard.getmPhEn() + "/");
        } else {
            viewHolder.phEn.setText("  英");
        }
        if (bookcard.getmPhAm().length() != 0) {
            viewHolder.phAm.setText(("  美" + "/" + bookcard.getmPhAm() + "/"));
        } else {
            viewHolder.phAm.setText("  美");
        }
        viewHolder.bookMeans.setText(bookcard.getmMeans());

        /**
         * 监听listView中的发音，加入把音频准备放在监听外面，会出现监听混乱，所以放在里面
         * 但有个缺点，不能马上将没有发音的按钮gone掉，必须点击后才能实现。
         */
        viewHolder.phEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File enPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/",bookcard.getmWordName()+ "." + "phEn.mp3");
                try {
                    enPlayer = new MediaPlayer();
                    enPlayer.setDataSource(enPlayFile.getAbsolutePath());
                    enPlayer.prepare();
                    /**
                     * 如果没有发音就隐藏
                     */
                } catch (Exception e) {
                    viewHolder.phEn.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                enPlayer.start();
            }
        });
        viewHolder.phAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File amPlayFile = new File(Environment.getExternalStorageDirectory() + "/dictionary/play/", bookcard.getmWordName() + "." + "phAm.mp3");

                try {
                    amPlayer = new MediaPlayer();
                    amPlayer.setDataSource(amPlayFile.getAbsolutePath());
                    amPlayer.prepare();
                    /**
                     * 如果没有发音就隐藏
                     */
                } catch (Exception e) {
                    viewHolder.phAm.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                amPlayer.start();
            }
        });

        /**
         * 实现释义隐藏，默认是item没有被点击，如果item被点击的话，就显示释义，并隐藏提示语
         */
        if (!bookcard.getIsTouch()) {
            viewHolder.bookMeans.setVisibility(View.GONE);
            viewHolder.bookHint.setVisibility(View.VISIBLE);
        } else {
            viewHolder.bookMeans.setVisibility(View.VISIBLE);
            viewHolder.bookHint.setVisibility(View.GONE);
        }
        return view;
    }

    class ViewHolder {
        TextView bookName;
        RadioButton phEn;
        RadioButton phAm;
        TextView bookMeans;
        /**
         * 提示语
         */
        TextView bookHint;
    }

}