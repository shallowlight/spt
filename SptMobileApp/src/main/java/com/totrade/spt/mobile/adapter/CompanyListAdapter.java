package com.totrade.spt.mobile.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.autrade.spt.master.entity.TblCompanyMasterEntity;
import com.totrade.spt.mobile.base.RecyclerAdapterBase;
import com.totrade.spt.mobile.base.ViewHolderBase;
import com.totrade.spt.mobile.view.R;

import java.util.List;

// 必须实现Filterable接口

public class CompanyListAdapter extends RecyclerAdapterBase<TblCompanyMasterEntity, CompanyListAdapter.CompanyListViewHoler> implements Filterable {

    private CompanyFilter mFilter;
    private List<TblCompanyMasterEntity> list;
    private List<TblCompanyMasterEntity> entityList;

    public CompanyListAdapter(List<TblCompanyMasterEntity> list) {
        super(list);
    }

    @Override
    public CompanyListViewHoler createViewHolderUseData(ViewGroup parent, int viewType) {
        return new CompanyListViewHoler(parent);
    }

    class CompanyListViewHoler extends ViewHolderBase<TblCompanyMasterEntity> {
        public CompanyListViewHoler(ViewGroup parent) {
            super(parent, R.layout.item_match_select2);
            show = (TextView) itemView.findViewById(R.id.tv_name);
        }

        private TextView show;

        @Override
        public void initItemData() {
            show.setText(itemObj.getCompanyName());
        }
    }

    @Override
    public Filter getFilter() {
        if (null == mFilter) {
            mFilter = new CompanyFilter();
        }
        return mFilter;
    }

    // 自定义Filter类
    class CompanyFilter extends Filter {
        @Override
        // 该方法在子线程中执行
        // 自定义过滤规则
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().trim();

            // 如果搜索框内容为空，就恢复原始数据
            if (TextUtils.isEmpty(filterString)) {
                list = entityList;
            } else {
                // 过滤出新数据
                list.clear();
                for (TblCompanyMasterEntity en : entityList) {
                    if (en.getCompanyName().contains(filterString)) {
                        list.add(en);
                    }
                }
            }

//            list是全局变量
//            results.values = list;
//            results.count = list.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
//            listOfAdapter = (List<String>) results.values;

            if (results.count > 0) {
                CompanyListAdapter.this.notifyDataSetChanged();  // 通知数据发生了改变
            } else {
//                CompanyListAdapter.this.notifyDataSetInvalidated(); // 通知数据失效
            }
        }
    }
}
