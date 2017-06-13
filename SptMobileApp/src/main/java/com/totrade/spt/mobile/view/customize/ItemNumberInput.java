package com.totrade.spt.mobile.view.customize;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.utility.AnnotateUtility;
import com.totrade.spt.mobile.view.R;

/**
 *
 * Created by iUserName on 16/12/01.
 */

public class ItemNumberInput extends LinearLayout
{

    @BindViewId(R.id.lblCategory) TextView lblCategory;
    @BindViewId(R.id.txtNumber) DecimalEditText txtNumber;
    @BindViewId(R.id.lblUnit) DecimalEditText lblUnit;

    public ItemNumberInput(Context context)
    {
        super(context);
    }

    public ItemNumberInput(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ItemNumberInput(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemNumberInput(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void initView(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_zone_number,null);
        AnnotateUtility.bindViewFormId(this,view);
        addView(view);
    }


    public void setCategoty(@NonNull String categoty)
    {

    }

}
