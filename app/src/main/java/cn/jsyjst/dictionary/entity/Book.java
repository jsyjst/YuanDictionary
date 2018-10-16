package cn.jsyjst.dictionary.entity;

/**
 * Created by 残渊 on 2018/4/22.
 */

/**
 * 生词本实体类
 */
public class Book {
    private String mWordName;

    private String mMeans;

    /**
     * 首字母用来实现单词字母排序
     */
    private String mHeadWord;


    public Book(String wordName,String means){
        mWordName=wordName;
        mMeans=means;
        mHeadWord=wordName.substring(0,1);
    }

    public String getmWordName(){
        return mWordName;
    }

    public String getmMeans(){
        return mMeans;
    }

    public String getmHeadWord(){
        return mHeadWord;
    }
}
