package cn.jsyjst.dictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.entity.History;

/**
 * Created by 残渊 on 2018/4/27.
 */

/**
 * 查询界面的历史记录适配器
 */
public class SeekHistoryAdapter extends ArrayAdapter<History> {

    private int resourceId;

    public SeekHistoryAdapter(Context context, int textViewResourceId, List<History>objects){
        super(context,textViewResourceId,objects);

        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        History history=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.historyName=(TextView)view.findViewById(R.id.tv_seek_history_name);
            viewHolder.historyMean=(TextView)view.findViewById(R.id.tv_seek_history_mean);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.historyName.setText(history.getmWordName());
        viewHolder.historyMean.setText(history.getmMeans());

        return view;
    }

    class ViewHolder{
        TextView historyName;
        TextView historyMean;
    }
}
