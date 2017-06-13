package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.entity.NotiEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import java.util.Date;
import java.util.List;

/**
 * 消息列表适配器
 * Created by Timothy on 2017/5/8.
 */

public class NoticeListAdapter extends RecyclerAdapterBase<NotiEntity, NoticeListAdapter.ViewHolder> {

    public NoticeListAdapter(List<NotiEntity> list) {
        super(list);
    }

    public NoticeListAdapter(Context context, List<NotiEntity> dataList) {
        super(dataList);
    }

    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notice_list, parent, false));
    }

    class ViewHolder extends ViewHolderBase<NotiEntity> {
        TextView tvNotiTime, tvNotiContent;
        ImageView iv_red_point;

        public ViewHolder(View view) {
            super(view);
            tvNotiTime = (TextView) view.findViewById(R.id.tv_noti_time);
            tvNotiContent = (TextView) view.findViewById(R.id.tv_noti_content);
            iv_red_point = (ImageView) view.findViewById(R.id.iv_red_point);
        }

        @Override
        public void initItemData() {
            String content = itemObj.getMsgContent();
            Date date = itemObj.getDate();
            String time = FormatUtil.date2String(date, "yyyy-MM-dd HH:mm");
            tvNotiContent.setText(content);
            tvNotiTime.setText(time);
            iv_red_point.setVisibility("1".equals(itemObj.getReadFlag()) ? View.GONE : View.VISIBLE);
        }
    }
}
