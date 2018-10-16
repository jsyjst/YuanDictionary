package cn.jsyjst.dictionary.listener;

import java.net.UnknownHostException;

/**
 * Created by 残渊 on 2018/4/17.
 */

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

    /**
     * 这个抽象方法是在没网时查不到单词或获取不到每日一句时
     * 就可以利用这个回调方法通知用户网络不可用
     * @param e
     */
    void onNetWorkError(UnknownHostException e);
}
