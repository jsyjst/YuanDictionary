package cn.jsyjst.dictionary.entity;

/**
 * Created by 残渊 on 2018/5/10.
 */

/**
 * 生词本卡片背词实体类
 */
public class BookCard {
    private String mWordName;

    private String mMeans;

    private String mPhAm;

    private String mPhEn;
    /**
     * 用来实现单词意思隐藏，根据子项的点击来改变这个值
     */
    private boolean isTouch;


    public BookCard(String wordName,String means,String phEn,String phAm){
        mWordName=wordName;
        mMeans=means;
        mPhEn=phEn;
        mPhAm=phAm;
    }

    public String getmWordName(){
        return mWordName;
    }

    public String getmMeans(){
        return mMeans;
    }

    public String getmPhAm(){
        return mPhAm;
    }
    public String getmPhEn(){
        return mPhEn;
    }
    public boolean getIsTouch(){
        return isTouch;
    }
    public void setIsTouch(boolean isTouch){
        this.isTouch=isTouch;
    }

}
