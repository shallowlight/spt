package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
 

/**
 *
 * Created by iUserName on 2016/8/31.
 */
public class ItemNameValue
{
    private static LinearLayout.LayoutParams params;
    private static LinearLayout.LayoutParams params2;
    private static LinearLayout.LayoutParams params3;

    public static LinearLayout creatItem(Context context,String name,String value)
    {
        return creatItem(context,name,value,null);
    }
    public static LinearLayout creatItem(Context context,String name,String value,String category)
    {
        return creatItem(context,name,value,category,0xffa5a5a5);
    }

    public static LinearLayout creatItem(Context context,String name,String value,String category, int valueColor)
    {
        if(params == null || params2 ==null || params3 == null)
        {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
            params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context,35));
        }
        TextView textView = new TextView(context);
        textView.setTextColor(0xff1d1d1d);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(name);
        textView.setLayoutParams(params);
        TextView tvValue = new TextView(context);
        tvValue.setTextColor(valueColor);
        tvValue.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        if(!TextUtils.isEmpty(category))
        {
            value = DictionaryUtility.getValue(category,value);
        }
        if(value == null) value ="";
        tvValue.setText(value);
        tvValue.setLayoutParams(params2);
        LinearLayout ll = new LinearLayout(context);
        ll.addView(textView);
        ll.addView(tvValue);
        ll.setLayoutParams(params3);
        return ll;
    }
}
