package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/10/26.
 */
public class ViewHolder {

    private Context context;
    private static View convertView;
    private final SparseArray<View> views;
    private int position;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.context = context;
        convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
        this.views = new SparseArray<>();
        this.position = position;
        convertView.setTag(this);
    }

    public static ViewHolder getViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        if (null == convertView){
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 根据ViewId去获取View
     * @param viewId 控件对应的id
     * @param <T> 返回控件实例
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = views.get(viewId);
        if (null == view){
            view = convertView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T) view;
    }

    public View getConvertView(){
        return convertView;
    }

    public int getPosition(){
        return position;
    }

    /**
     * 取文本控件
     * @param viewId
     * @return
     */
    private TextView getTextView(int viewId){
        return getView(viewId);
    }

    /**
     * 取图形控件
     * @param viewId
     * @return
     */
    private ImageView getImageView(int viewId){
        return getView(viewId);
    }

    public ViewHolder setTextView(int viewid,String txt){
        getTextView(viewid).setText(txt);
        return this;
    }

    public ViewHolder setImageViewByDrawable(int viewId,int resId){
        getImageView(viewId).setImageResource(resId);
        return this;
    }

    public ViewHolder setImageViewByUrl(int viewId,String url){
        Picasso.with(context).load(url).into(getImageView(viewId));
        return this;
    }

}
