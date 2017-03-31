package com.colin.blockchain.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Colin on 2017/3/31 11:52.
 * 邮箱：cartier_he@163.com
 * 微信：cartier_he
 */

public class TimeUtil {
    /*
    * 将时间戳转换为时间
    */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String FormetSize(float fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 10000) {
            fileSizeString = df.format((double) fileS) + "";
        } else {
            fileSizeString = df.format((double) fileS / 10000) + "万";
        }
        return fileSizeString;
    }

}
