package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

/**
 * Created by Administrator on 2016/12/2.
 */
public class ListEmptyView extends LinearLayout {

    private TextView mTextView;
    private int emptyHeight = 80;
    private int textSize = 20;
    private int content;

    public ListEmptyView(Context context, int heightDp, int size,String content)
    {
        super(context);
        int heightPx = FormatUtil.dip2px(context,heightDp);
        init(context, heightPx, size, content);
    }

    private void init(Context context, int height, int size, @NonNull String content) {
        TextView textView = new TextView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height <= 0 ? emptyHeight : height);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.ltgray));
        textView.setTextSize(size <= 0 ? textSize : size);
        textView.setText(content);
        setGravity(Gravity.CENTER);
        addView(textView);
    }

    public ListEmptyView setEmptyHeight(int height) {
        this.emptyHeight = height;
        return this;
    }

    public ListEmptyView setTextSize(int size) {
        this.textSize = size;
        return this;
    }

}
