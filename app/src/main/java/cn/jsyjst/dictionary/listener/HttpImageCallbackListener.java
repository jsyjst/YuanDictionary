package cn.jsyjst.dictionary.listener;

import android.graphics.Bitmap;

/**
 * Created by 残渊 on 2018/4/17.
 */

public interface HttpImageCallbackListener {

    void onFinish(Bitmap bitmap);

    void onError(Exception e);
}
