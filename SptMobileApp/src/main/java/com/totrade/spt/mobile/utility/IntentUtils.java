package com.totrade.spt.mobile.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.Serializable;
import java.util.Map;


/**
 *
 * Created by Administrator on 2016/11/1.
 */
public class IntentUtils {

    public static void setClass(Intent intent,Context context, Class<?> c){
        intent.setClass(context,c);
    }

    /**
     * 无数据跳转
     * @param context
     * @param c
     */
    public static void startActivity(Context context, Class<?> c){
        Intent intent = new Intent();
        setClass(intent,context,c);
        context.startActivity(intent);
    }

    /**
     * 无数据跳转
     * @param context
     * @param c
     */
    public static void startActivity(Intent intent,Context context, Class<?> c){
        setClass(intent,context,c);
        context.startActivity(intent);
    }

    /**
     * 携带String跳转
     * @param context
     * @param c
     * @param string
     */
    public static void startActivity(Context context, Class<?> c, String tag,String string){
        Intent intent = new Intent();
        intent.putExtra(tag,string);
        startActivity(intent,context,c);
    }

    /**
     * 传递map数据
     * @param context
     * @param c
     * @param map
     */
    public static <T extends Serializable> void startActivity(Context context, Class<?> c, Map<String,T> map){
        Intent intent = new Intent();
        for (Map.Entry<String,T> entry:map.entrySet()){
            if (entry.getValue() instanceof String){
                intent.putExtra(entry.getKey(),(String)entry.getValue());
            }else if (entry.getValue() instanceof Serializable){
                intent.putExtra(entry.getKey(),entry.getValue());
            }else{
                throw new IllegalArgumentException("传入不符合要求的参数");
            }

        }
        startActivity(intent,context,c);
    }

    /**
     * 拨打电话
     * @param context
     * @param telPhone
     */
    public static void startCallPhoneActivity(Context context,String telPhone){
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telPhone));
        context.startActivity(phoneIntent);
    }

}
