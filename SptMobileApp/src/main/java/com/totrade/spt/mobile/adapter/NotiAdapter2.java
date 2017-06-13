package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.entity.NotiEntity;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import java.util.Date;
import java.util.List;

public class NotiAdapter2 extends RecyclerAdapterBase<NotiEntity, NotiAdapter2.ViewHolder> {
    private boolean isEdit;
    private List<NotiEntity> checkList;

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public List<NotiEntity> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<NotiEntity> checkList) {
        this.checkList = checkList;
    }

    public NotiAdapter2(List<NotiEntity> list) {
        super(list);
    }

    public NotiAdapter2(Context context, List<NotiEntity> dataList) {
        super(dataList);
        setEdit(false);
    }
    @Override
    public ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notify, parent, false));
    }

    class ViewHolder extends ViewHolderBase<NotiEntity> {
        TextView tvNotiType, tvNotiTime, tvNotiContent;
        ImageView ivRedPoint;
        CheckBox ivCheck;

        public ViewHolder(View view) {
            super(view);
            tvNotiType = (TextView) view.findViewById(R.id.tv_noti_type);
            tvNotiTime = (TextView) view.findViewById(R.id.tv_noti_time);
            tvNotiContent = (TextView) view.findViewById(R.id.tv_noti_content);
            ivRedPoint = (ImageView) view.findViewById(R.id.iv_red_point);
            ivCheck = (CheckBox) view.findViewById(R.id.iv_check);
        }

        @Override
        public void initItemData() {
            String title = itemObj.getMsgTitle();
            String content = itemObj.getMsgContent();
            Date date = itemObj.getDate();
            String time = FormatUtil.date2String(date, "yyyy-MM-dd HH:mm");
            if (itemObj.getMsgType().equals(SptConstant.USER_PUSH_MSG_TAG_SUBMITBS)) {
                title = "实盘询价";
            }
            tvNotiType.setText(title);
            ivRedPoint.setVisibility("0".equals(itemObj.getReadFlag()) ? View.VISIBLE : View.GONE);    //	"0":未读 "1":已读

            tvNotiContent.setText(content);
            tvNotiTime.setText(time);

            ivCheck.setVisibility(isEdit() ? View.VISIBLE : View.GONE);
            ivCheck.setChecked(checkList.contains(itemObj));
            ivCheck.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        checkList.remove(itemObj);
                    } else {
                        checkList.add(itemObj);
                    }
                    v.setSelected(!v.isSelected());
                }
            });

        }
    }
}
