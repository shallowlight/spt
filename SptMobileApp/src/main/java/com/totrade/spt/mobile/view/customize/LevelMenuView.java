package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * 菜单控件
 * Created by Administrator on 2016/11/14.
 */
public class LevelMenuView extends RelativeLayout {

    private ImageView icon;
    private TextView title;
    private TextView content;
    private ImageView iconRight;

    public LevelMenuView(Context context) {
        this(context, null);
    }

    public LevelMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_menu_list, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs
                , R.styleable.LevelMenuView);

        initLayout();

        initWeight(typedArray);
    }

    private void initLayout() {
        setBackgroundColor(getResources().getColor(R.color.white));
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initWeight(TypedArray typedArray) {

        icon = (ImageView) findViewById(R.id.icon);
        int levelImage = typedArray.getResourceId(R.styleable.LevelMenuView_level_image, 0);
        setLevelImage(levelImage);

        title = (TextView) findViewById(R.id.title);
        String text = typedArray.getString(R.styleable.LevelMenuView_level_text);
        setLevelText(text);
        
        content = (TextView) findViewById(R.id.content);
        String text2 = typedArray.getString(R.styleable.LevelMenuView_level_content);
        setLevelContent(text2);
        
        iconRight = (ImageView)findViewById(R.id.icon_right);
        
        typedArray.recycle();
    }

    public LevelMenuView setLevelText(String text) {
        title.setText(text);
        return this;
    }

    public LevelMenuView setLevelTextSize(int complexUnitSp, int resId) {
        title.setTextSize(complexUnitSp, getResources().getDimension(resId));
        return this;
    }

    public LevelMenuView setLevelTextColor(int resId) {
        title.setTextColor(getResources().getColor(resId));
        return this;
    }

    public LevelMenuView setLevelImage(int resId) {
        icon.setImageResource(resId);
        return this;
    }

    public LevelMenuView setLevelContent(String text) {
        content.setText(text);
        return this;
    }

    public LevelMenuView setLevelContentSize(int complexUnitSp, int resId) {
        content.setTextSize(complexUnitSp, getResources().getDimension(resId));
        return this;
    }

    public LevelMenuView setLevelContentColor(int color) {
        content.setTextColor(getResources().getColor(color));
        return this;
    }

    public LevelMenuView setLevelContentBold() {
        content.getPaint().setFakeBoldText(true);
        return this;
    }

    public LevelMenuView setRightIconGone(){
        iconRight.setVisibility(View.GONE);
        return this;
    }

    public interface onMenuItemClickListener {
        void onItemClick();
    }

    public void setOnMenuItemClick(final onMenuItemClickListener listener) {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onItemClick();
                else
                    throw new NullPointerException("菜单监听事件为空");
            }
        });
    }
}
