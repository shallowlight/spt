package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.view.R;

import java.io.File;
import java.net.URL;

/**
 * 用于图片上传
 * Created by Administrator on 2016/12/20.
 */
public class PostImageView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private Context mContext;

    private int mDefaultHint = R.style.txt_main_font_12_ltgray;

    public PostImageView(Context context) {
        this(context, null);
    }

    public PostImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_view_postimageview, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PropertyView);

        init(a);
    }

    private void init(TypedArray a) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        mImageView = (ImageView) findViewById(R.id.iv);
        mTextView = (TextView) findViewById(R.id.tv);

        String title = a.getString(
                R.styleable.PostImageView_hint);
        setHint(title,mDefaultHint);
        a.recycle();
    }

    public void setHint(String hint,int styleId) {
        mTextView.setText(hint);
        mTextView.setTextAppearance(mContext, styleId);
    }

    public void setImageResource(int resId) {
        Picasso.with(mContext).load(resId).placeholder(R.drawable.img_post_default).centerCrop().resizeDimen(R.dimen.image_width, R.dimen.image_width).into(mImageView);
    }

    public void setImageResource(File file) {
        Picasso.with(mContext).load(file).placeholder(R.drawable.img_post_default).centerCrop().resizeDimen(R.dimen.image_width, R.dimen.image_width).into(mImageView);
    }

    public void setImageResource(String path) {
        if (null == path)
            path = "";
        setImageResource(new File(path));
    }
}
