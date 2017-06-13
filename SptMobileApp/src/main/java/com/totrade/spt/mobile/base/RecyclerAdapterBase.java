package com.totrade.spt.mobile.base;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

import java.text.DecimalFormat;
import java.util.List;


/**
 * RecyclerView 对应的adapter 基类
 *
 * @param <T>  List的数据类型
 * @param <VH>
 */
public abstract class RecyclerAdapterBase<T, VH extends ViewHolderBase<T>> extends RecyclerView.Adapter<ViewHolderBase<T>> {
    protected List<T> list;
    protected Context context;
    private String emptyText;
    private final int TYPE_EMPTY = 1;
    private
    @DrawableRes
    int emptyRes;
    protected DecimalFormat format = new DecimalFormat("#0.#######");
    protected DecimalFormat format2 = new DecimalFormat("#0.00");

    public RecyclerAdapterBase(List<T> list) {
        this.list = list;
        emptyRes = R.drawable.img_empty;
    }

    public List<T> getList() {
        return list;
    }

    /**
     * 设置空值提示文字
     *
     * @param emptyText 空值文字
     * @param emptyRes  空值图片
     */
    public void setEmptyRes(String emptyText, @DrawableRes int emptyRes) {
        this.emptyText = emptyText;
        this.emptyRes = emptyRes;
    }

    /**
     * 设置空值提示文字
     *
     * @param emptyText 空值文字
     */
    public void setEmptyRes(String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * 非空值对应的ViewHolder
     *
     * @return VH
     */
    public abstract VH createViewHolderUseData(ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {
        if (list == null || list.isEmpty()) {
            return TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }

    /**
     * Override ViewHolder
     * List为空时显示图标加文字提示
     * List不为空时返回相应的 ViewHolder
     *
     * @return ViewHolderBase
     */
    @Deprecated
    @Override
    public ViewHolderBase<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (viewType == TYPE_EMPTY) {
            //设置空值提示文字
            View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_recy, parent, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.img_empty);
            imageView.setImageResource(emptyRes);
            TextView textView = (TextView) view.findViewById(R.id.lbl_empty);
            if (emptyText == null) {
                emptyText = "暂时没有数据";
            }
            textView.setText(emptyText);
            return new ViewHolder(view);
        } else {
            return createViewHolderUseData(parent, viewType);
        }
    }


    @Nullable
    private T getT(int position) {
        if (list == null || list.size() <= position)
            return null;
        else
            return list.get(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolderBase holder, int position) {
        final int truePosition = position;
        holder.itemObj = getT(truePosition);

        if (holder.itemObj != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(holder.itemObj, truePosition);
                    }
                }
            });
            holder.position = truePosition;
            holder.initItemData();
            holder.initItemData(position);
        }
    }


    @Override
    public int getItemCount() {
        return (list == null || list.isEmpty()) ? 1 : list.size();
    }

    /**
     * 空值提示对应的 ViewHolder
     */
    private class ViewHolder extends ViewHolderBase<T> {
        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initItemData() {
        }
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClick(@NonNull Object obj, int position);
    }
}
