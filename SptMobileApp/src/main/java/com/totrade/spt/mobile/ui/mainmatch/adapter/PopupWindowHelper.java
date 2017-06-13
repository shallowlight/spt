package com.totrade.spt.mobile.ui.mainmatch.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ListView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.nego.dto.BsAdditionalRecordUpEntity;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.helper.ProductCfgHelper;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 撮合集市筛选弹窗
 *
 * @author huangxy
 * @date 2017/4/14
 */
public class PopupWindowHelper {

    private PopupWindowHelper() {
    }

    private static class InnerClass {
        static PopupWindowHelper inner;

        static {
            inner = new PopupWindowHelper();
        }
    }

    public static PopupWindowHelper i() {
        return InnerClass.inner;
    }

    private BsAdditionalRecordUpEntity upEntity;
    public BsAdditionalRecordUpEntity filterEntity = new BsAdditionalRecordUpEntity();

    public void setUpEntity(BsAdditionalRecordUpEntity upEntity) {
        this.upEntity = upEntity;
        filterEntity.setProductType(upEntity.getProductType());
    }

    /**
     * 品名视图
     */
    public View initSelectProductSecondType() {
        View view = View.inflate(SptApplication.context, R.layout.pop_match_selection, null);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final ListView listView1 = (ListView) view.findViewById(R.id.listView1);
        final ListView listView2 = (ListView) view.findViewById(R.id.listView2);

        List<ProductTypeDto> topTypeList = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_MATCH, null, 0);
        for (int i = 0; i < topTypeList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(topTypeList.get(i).getTypeName()).setTag(topTypeList.get(i)));
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ProductTypeDto typeDto = (ProductTypeDto) tab.getTag();
                filterEntity.setProductType(typeDto.getProductType());
                setSecontTypeList(listView1, listView2);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //默认选中已经 select index0，这里再次调用 需要重写reselected，不建议
//        tabLayout.getTabAt(1).select();

        setSecontTypeList(listView1, listView2);
        return view;
    }

    private String[] type = new String[]{"醇类", "二甘醇"};

    private void setSecontTypeList(final ListView listView1, final ListView listView2) {
        List<ProductTypeDto> secondTypeList = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_MATCH, filterEntity.getProductType().substring(0, 2), 1);
        ProductTypeDto dto = new ProductTypeDto();
        dto.setProductType(filterEntity.getProductType().substring(0, 2));
        dto.setTypeName("全部");
        secondTypeList.add(0, dto);
        SelectionAdapter selectionAdapter = new SelectionAdapter(secondTypeList);
        listView1.setAdapter(selectionAdapter);

//        点击监听
        selectionAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                ProductTypeDto typeDto = (ProductTypeDto) view.getTag();
                filterEntity.setProductType(typeDto.getProductType());
                setThirdTypeList(listView2);
            }
        });

//        指定默认选项
        int select = 0;
        for (int i = 0; i < secondTypeList.size(); i++) {
            if (upEntity.getProductType().length() == 2) {
                select = 0;
                break;
            }
            if (i > 0 && upEntity.getProductType().substring(0, 5).equals(secondTypeList.get(i).getProductType().substring(0, 5))) {
                select = i;
                break;
            }
        }
        selectionAdapter.setSelection(select, true);
        listView1.setSelection(select);

    }

    private void setThirdTypeList(ListView listView2) {
        String productType = filterEntity.getProductType();
        if (productType.length() >= 5) {
            productType = productType.substring(0, 5);
        }
        List<ProductTypeDto> thirdTypeList = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_MATCH, productType, 2);
        ProductTypeDto dto2 = new ProductTypeDto();
        dto2.setProductType(productType);
        dto2.setTypeName("全部");
        thirdTypeList.add(0, dto2);
        SelectionAdapter2 selectionAdapter2 = new SelectionAdapter2(thirdTypeList);
        listView2.setAdapter(selectionAdapter2);

//        点击监听
        selectionAdapter2.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                ProductTypeDto typeDto = (ProductTypeDto) view.getTag();
                filterEntity.setProductType(typeDto.getProductType());
            }
        });

//        指定默认选项
        int select = 0;
        for (int i = 0; i < thirdTypeList.size(); i++) {
            if (upEntity.getProductType().equals(thirdTypeList.get(i).getProductType())) {
                select = i;
                break;
            }
        }
        selectionAdapter2.setSelection(select, true);
        listView2.setSelection(select);
    }

    String[] deliveryTime = new String[]{"立即交付", "周期交付"};

    /**
     * 交货期视图
     *
     * @return
     */
    public View initDeliveryTime() {
        View view = View.inflate(SptApplication.context, R.layout.pop_match_selection, null);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final ListView listView1 = (ListView) view.findViewById(R.id.listView1);
        final ListView listView2 = (ListView) view.findViewById(R.id.listView2);
        tabLayout.setVisibility(View.GONE);

        List<ProductTypeDto> dtos = new ArrayList<>();
        for (int j = 0; j < deliveryTime.length; j++) {
            ProductTypeDto typeDto = new ProductTypeDto();
            typeDto.setTypeName(deliveryTime[j]);
            dtos.add(typeDto);
        }

        SelectionAdapter selectionAdapter = new SelectionAdapter(dtos);
        listView1.setAdapter(selectionAdapter);
        selectionAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                ProductTypeDto typeDto = (ProductTypeDto) view.getTag();
                boolean isNow = position == 0;
                setDeliveryTime(listView2, isNow);
            }
        });
        selectionAdapter.setSelection(0, true);

        return view;
    }

    private void setDeliveryTime(ListView listView2, boolean isNow) {

        List<String> list = ProductCfgHelper.getZoneDeliveryTime(upEntity.getProductType(), isNow);
        List<ProductTypeDto> dtos = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            ProductTypeDto typeDto = new ProductTypeDto();
            typeDto.setTypeName(list.get(j));
            dtos.add(typeDto);
        }
        SelectionAdapter2 selectionAdapter2 = new SelectionAdapter2(dtos);
        listView2.setAdapter(selectionAdapter2);
        selectionAdapter2.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                ProductTypeDto typeDto = (ProductTypeDto) view.getTag();
                String llDelivertTimeTextValue = typeDto.getTypeName();
                if (LoginUserContext.speicalProduct(upEntity.getProductType())) {
                    upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDateSpeicalProduct(llDelivertTimeTextValue));
                } else {
                    upEntity.setDeliveryTime(FormatUtil.disp2DeliveryDate(llDelivertTimeTextValue));      //交货期
                }


                upEntity.setUpOrDown(caculatorUpOrDown(llDelivertTimeTextValue));
            }
        });

    }

    @NonNull
    private String caculatorUpOrDown(String llDelivertTimeTextValue) {
        String umd = null;
        try {
            if (llDelivertTimeTextValue.endsWith("上")) {
                umd = "UP";
            } else if (llDelivertTimeTextValue.endsWith("中")) {
                umd = "MD";
            } else if (llDelivertTimeTextValue.endsWith("下")) {
                umd = "DOWN";
            } else {
                String day = llDelivertTimeTextValue.substring(llDelivertTimeTextValue.length() - 3, llDelivertTimeTextValue.length() - 1);
                Integer i = Integer.valueOf(day);
                if (i < 11) {
                    umd = "UP";
                } else if (i < 21) {
                    umd = "MD";
                } else {
                    umd = "DOWN";
                }
            }
        } catch (NumberFormatException e) {
            LogUtils.e(PopupWindowHelper.class.getSimpleName(), "caculatorUpOrDown Exception");
            e.printStackTrace();
        }

        return umd;
    }


}
