package com.example.demonstrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by think on 2017/10/27.
 *
 */

public class DialogUtil {
    /**
     * 弹出列表对话框.
     * @param activity
     * @param title
     * @param items
     * @param listener
     */
    public static void showListDialog(Activity activity,String title,String[] items,DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(items, listener)
                .create()
                .show();
    }
}
