package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.totrade.spt.mobile.adapter.ObjRecycleAdapter;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by iUserName on 16/12/08.
 */

public class BottomPopSelect extends PopupWindowBottom
{
    private ObjRecycleAdapter<Object> adapterObjcet;
    private RecyclerView recyclerView;      //显示筛选项的列表控件
    private List<Object> listPopSelect;      //显示筛选项的列表控件

    public Object getViewTag()
    {
        if(adapterObjcet!=null)
        {
            return adapterObjcet.getT();
        }
        return null;
    }

    /**
     *
     * @param list 数据列表
     * @param tag 默认选中的item  --  数据实体
     * @param <T> 实体类
     */
    public <T> void updateList(@Nullable List<T> list,@Nullable Object tag)
    {
        if(listPopSelect == null)
        {
            listPopSelect = new ArrayList<>();
        }
        listPopSelect.clear();
        if (list != null)
        {
            listPopSelect.addAll(list);
        }
        adapterObjcet.setT(tag);
        adapterObjcet.notifyDataSetChanged();
    }

    @Override
    public void showAtBottom()
    {
        //最多取7个条目的高度
        int size = Math.min(adapterObjcet.getItemCount()+1,7);
        int height = size * adapterObjcet.getItemHeight();
        recyclerView.getLayoutParams().height = FormatUtil.dip2px(recyclerView.getContext(),height);
        super.showAtBottom();
    }

    public BottomPopSelect(Context context)
    {
        if(adapterObjcet == null)
        {
            listPopSelect = new ArrayList<>();
            adapterObjcet = new ObjRecycleAdapter<>(listPopSelect);
            adapterObjcet.setEmptyRes("未获取到可用数据,请检查网络或稍后再试",R.drawable.img_empty_s);
        }
        else
        {
            listPopSelect.clear();
            adapterObjcet.notifyDataSetChanged();
        }

        adapterObjcet.setT(null);
        adapterObjcet.notifyDataSetChanged();
        View view = LayoutInflater.from(context).inflate(R.layout.lalayout_popbottom_select,null,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapterObjcet);
        creatView(view);
        setDismissPopView(view.findViewById(R.id.lblConfirm));
    }

}
