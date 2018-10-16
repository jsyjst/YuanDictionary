package cn.jsyjst.dictionary.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by 残渊 on 2018/4/21.
 */


/**
 * 查询界面的历史记录和历史记录活动都用这个实体类
 */
public class History {

    private String mWordName;

    private String mMeans;


    public History(String wordName,String means){
        mWordName=wordName;
        mMeans=means;
    }

    public String getmWordName(){
        return mWordName;
    }

    public String getmMeans(){
        return mMeans;
    }

}
