package com.superlity.test.recyclelistviewtest.utils;

import android.content.Context;
import android.widget.Toast;

import com.superlity.test.recyclelistviewtest.MyApplication;

/**
 * Created by lion on 2015/8/12 0012.
 */
public class UtilTools {

    public static void toast( String msg ){
        if ( msg == null ){
            return;
        }

        Context context = MyApplication.getInstance();
        if ( context != null ){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
