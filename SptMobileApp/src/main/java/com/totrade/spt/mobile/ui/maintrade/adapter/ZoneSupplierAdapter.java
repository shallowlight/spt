package com.totrade.spt.mobile.ui.maintrade.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.zone.dto.QueryPageZoneCompanyDownEntity;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iUserName on 16/12/13.
 */

public class ZoneSupplierAdapter extends RecyclerAdapterBase<QueryPageZoneCompanyDownEntity, ZoneSupplierAdapter.ViewHolder> {

    private List<QueryPageZoneCompanyDownEntity> selectEntity;

    public ZoneSupplierAdapter(List<QueryPageZoneCompanyDownEntity> list, final List<String> companyTags) {
        super(list);
        initSelectList(companyTags);
    }

    /**
     * 初始化选中项,将上次选中项标记到新的列表中
     */
    private void initSelectList(List<String> companyTags) {
        selectEntity = new ArrayList<>();
        if (companyTags != null && !companyTags.isEmpty() && getList() != null) {
            for (QueryPageZoneCompanyDownEntity entity : getList()) {
                if (companyTags.contains(entity.getCompanyTag())) {
                    selectEntity.add(entity);
                }
            }
        }
    }


    public List<QueryPageZoneCompanyDownEntity> getSelectItems() {
        return selectEntity;
    }

    @Override
    public ZoneSupplierAdapter.ViewHolder createViewHolderUseData(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    class ViewHolder extends ViewHolderBase<QueryPageZoneCompanyDownEntity> {
        @BindViewId(R.id.img_check)
        private ImageView img_check;
        @BindViewId(R.id.lblSupName)
        private TextView lblSupName;

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_zone_supplier);
        }

        @Override
        public void initItemData() {
            lblSupName.setText("【" + itemObj.getBusinessCode() + "】" + itemObj.getCompanyName());
            img_check.setImageResource(selectEntity.contains(itemObj) ? R.drawable.check : R.drawable.unsigned);

        }
    }

    public void selectItem(QueryPageZoneCompanyDownEntity itemObj, int position) {
//        if (selectEntity.contains(itemObj)) {
//            selectEntity.remove(itemObj);
//        } else {
//            selectEntity.add(itemObj);
//        }
        selectEntity.clear();
        selectEntity.add(itemObj);
        notifyItemChanged(position);
    }
}
