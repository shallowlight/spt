package com.totrade.spt.mobile.adapter;

import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
 import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 *
 * Created by iUserName on 16/11/25.
 */

public class ObjRecycleAdapter<T> extends RecyclerAdapterBase<T,ViewHolderBase<T>>
{
    private static @ColorInt int colorBlue;       //蓝色，标记选中项文字
    private static @ColorInt int colorDkGray;     //深灰色，标记未被选中项文字
    private static @ColorInt int colorWhite;      //白色，标记未被选中项背景色
    private static @ColorInt int colorLtGray;     //浅灰色，标记选中项背景色
    private static final int itemHeightDp = 40;
    public int getItemHeight()
    {
        return  itemHeightDp;
    }
    private T t;

    public void setT(T t)
    {
        this.t = t;
    }

    public T getT()
    {
        return t;
    }


    public ObjRecycleAdapter(List<T> list)
    {
        super(list);
    }


    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType)
    {
        if(colorBlue ==0)
        {
            colorBlue = context.getResources().getColor(R.color.blue);
            colorDkGray = context.getResources().getColor(R.color.dkgray);
            colorLtGray = context.getResources().getColor(R.color.background);
            colorWhite = context.getResources().getColor(R.color.white);
        }
        return new ViewHolder(creatItemView());
    }


    class ViewHolder extends ViewHolderBase<T>
    {
        TextView textView;
        ViewHolder(View view)
        {
            super(view);
        }

        @Override
        public void initItemData()
        {
            textView = (TextView) ((LinearLayout)itemView).getChildAt(0);
            if(itemObj instanceof NameValueItem)
            {
                textView.setText(((NameValueItem) itemObj).getName());
            }
            else if(itemObj instanceof CharSequence)
            {
                textView.setText((CharSequence) itemObj);
            }
            else
            {
                textView.setText(itemObj.toString());
            }

            if(objEquals(itemObj,t))
            {
                textView.setTextColor(colorBlue);
                textView.setTextSize(16);
                itemView.setBackgroundColor(colorLtGray);
            }
            else
            {
                textView.setTextSize(14);
                textView.setTextColor(colorDkGray);
                itemView.setBackgroundColor(colorWhite);
            }
            textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   t = itemObj;
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * 比较是否相等
     * @param obj1 比较对象1
     * @param obj2 比较对象2
     * @return 对象是否相等
     */
    private boolean objEquals(Object obj1,Object obj2)
    {
        if(obj1 == null || obj2 == null)
        {
            return false;
        }
        if(obj1 instanceof NameValueItem && obj2 instanceof NameValueItem)
        {
            //只比较显示值
            return  ((NameValueItem) obj1).getName()!=null && ((NameValueItem) obj1).getName().equals(((NameValueItem) obj2).getName());
        }
        return obj1.equals(obj2);
    }

    //设置布局
    private ViewGroup.LayoutParams params;
    private LayoutParams params2;
    private ViewGroup.LayoutParams params3;
    private LinearLayout creatItemView()
    {
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        //间隔线
        View view = new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.ltgray_e));
        if(params2 == null)
        {
            params2 = new LayoutParams(LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context,1));
        }
        view.setLayoutParams(params2);
        //TextView
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        if(params3 == null)
        {
            params3 = new LayoutParams(LayoutParams.MATCH_PARENT, FormatUtil.dip2px(context,itemHeightDp) - params2.height);
        }
        textView.setLayoutParams(params3);

        ll.addView(textView);
        ll.addView(view);
        if(params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ll.setLayoutParams(params);
        return ll;
    }

}
