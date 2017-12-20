package com.example.demonstrate;

import android.os.Handler;
import android.os.Looper;


/**
 * Created by think on 2017/10/29.
 */

public class HDHelper {

    private Handler handler;
    private static HDHelper hdHelper;

    private HDHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static HDHelper getInstance() {
        if (null == hdHelper) {
            synchronized (HDHelper.class) {
                if (null == hdHelper) {
                    hdHelper = new HDHelper();
                }
            }
        }
        return hdHelper;
    }

    public Handler getHandler() {
        return handler;
    }
}
