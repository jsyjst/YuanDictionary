package cn.jsyjst.dictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.entity.History;

/**
 * Created by 残渊 on 2018/4/21.
 */


/**
 * 历史记录活动的适配器
 */
public class HistoryAdapter extends ArrayAdapter<History> {

    private int resourceId;
    /**
     * 用来隐藏释义
     */
    private boolean isHide;

    public HistoryAdapter(Context context, int textViewResourceId, List<History> objects){
        super(context,textViewResourceId,objects);

        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        History history = getItem(position);

        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder= new ViewHolder();
            viewHolder.historyWordName=(TextView)view.findViewById(R.id.tv_word_name);
            viewHolder.historyMeans = (TextView) view.findViewById(R.id.tv_word_means);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.historyWordName.setText(history.getmWordName());
        viewHolder.historyMeans.setText(history.getmMeans());
        if(isHide){
            viewHolder.historyMeans.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.historyMeans.setVisibility(View.VISIBLE);
        }
        return view;
    }

    class ViewHolder {

        TextView historyWordName;
        TextView historyMeans;
    }
    public void hideHistoryMeans(boolean isHide){
        this.isHide=isHide;
        notifyDataSetChanged();
    }
}
