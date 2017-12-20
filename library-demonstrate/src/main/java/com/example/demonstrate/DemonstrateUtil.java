package com.example.demonstrate;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by think on 2017/10/25.
 * 展示结果数据.
 */

public class DemonstrateUtil {
    static String STR_TAG = "--->***";
    static String TAG = "DemonstrateUtil";

    public static void showAllResult(String s,TextView tv) {
        showTextResult(s,tv);
        showToastResult(tv.getContext(),s);
        showLogResult(s);
    }

    public static void showTextResult(String s, TextView tv) {
        tv.setText(s);
    }

    public static void showToastResult(Context context,String s) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    public static void showLogResult(String str) {
        Log.i(TAG, str+ STR_TAG);
    }

    public static void showLogResult(String tag,String str) {
        Log.i(tag, str+ STR_TAG);
    }
}
