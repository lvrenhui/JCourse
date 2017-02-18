package com.aligame.jcourse.library.toast;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lvrh on 17/2/18.
 */

public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
