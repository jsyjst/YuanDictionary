package cn.jsyjst.dictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.entity.Book;

/**
 * Created by 残渊 on 2018/4/22.
 */


/**
 * 生词本适配器
 */
public class BookAdapter extends ArrayAdapter<Book>{

    private int resourceId;
    /**
     * 用来判断是否隐藏翻译的意思
     */
    private boolean isHide;

    public BookAdapter(Context context, int textViewResourceId, List<Book> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookName = (TextView) view.findViewById(R.id.tv_word_name);
            viewHolder.bookMeans = (TextView) view.findViewById(R.id.tv_word_means);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.bookName.setText(book.getmWordName());
        viewHolder.bookMeans.setText(book.getmMeans());
        if(isHide){
            viewHolder.bookMeans.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.bookMeans.setVisibility(View.VISIBLE);
        }

        return view;
    }

    class  ViewHolder{
        TextView bookName;
        TextView bookMeans;
    }
    public void hideBookMeans(boolean isHide){
        this.isHide=isHide;
        notifyDataSetChanged();
    }
}
