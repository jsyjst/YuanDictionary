package cn.jsyjst.dictionary.https;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import cn.jsyjst.dictionary.listener.HttpCallbackListener;
import cn.jsyjst.dictionary.listener.HttpImageCallbackListener;

/**
 * Created by 残渊 on 2018/4/17.
 */

public class HttpUtil {

    /*
    一般的网络操作

     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                    /**
                     * 这个异常用来处理没网时的情况
                     */
                } catch (UnknownHostException e) {
                    if (listener != null) {
                        listener.onNetWorkError(e);
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 图片解析的网络操作
     **/
    public static void getBitmap(final String path, final HttpImageCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        if (listener != null) {
                            listener.onFinish(bitmap);

                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }

        }).start();
    }

    public static void savePlay(final String tts, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = getSDPath()+"/dictionary/play/";
                File dirFile = new File(path);
                File file = null;
                OutputStream out = null;
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(tts);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    InputStream in = connection.getInputStream();
                    file = new File(path, fileName );
                    out = new FileOutputStream(file);
                    byte[] buffer =new byte[1*1024];
                    while(in.read(buffer)!=-1){
                        out.write(buffer);
                    }
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public static void saveBitmap(Bitmap bitmap, String fileName) {
        String path = getSDPath()+"/dictionary/dayword/";
        File dirFile = new File(path);
        BufferedOutputStream bos = null;
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File dayFile = new File(path, fileName + ".jpg");
            bos = new BufferedOutputStream(new FileOutputStream(dayFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }


}
