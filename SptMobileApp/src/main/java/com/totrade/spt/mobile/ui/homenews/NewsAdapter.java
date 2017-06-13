package com.totrade.spt.mobile.ui.homenews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.view.R;

/**
 * Created by Administrator on 2017/4/5.
 */

public class NewsAdapter extends RecyclerView.Adapter {
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsViewHolder(View.inflate(parent.getContext(), R.layout.item_home_news, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        public NewsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
