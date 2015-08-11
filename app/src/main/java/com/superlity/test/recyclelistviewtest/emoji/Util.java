package com.superlity.test.recyclelistviewtest.emoji;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ??????
 *
 * @author Administrator
 * @version 1.1.0
 * @Copyright Copyright (c) 2012 - 2100
 * @create at 2013-5-9
 */
public class Util {

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strDate = format.format(date);
        return strDate;

    }
}
