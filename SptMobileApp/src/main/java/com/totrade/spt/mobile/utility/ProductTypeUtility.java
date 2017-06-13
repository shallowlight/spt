package com.totrade.spt.mobile.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.spt.common.entity.TblNegoFocusMasterEntity;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.stage.utility.CollectionUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理商品类别的工具类
 *
 * @author wangyilin
 */
public final class ProductTypeUtility {
    private static List<ProductTypeDto> listProductDto = new ArrayList<>();                    //所有产品

    private static List<TblNegoFocusMasterEntity> lstUserFocusDto = new ArrayList<>();        //关注产品
    private static boolean openFocus = false;                                                //关注开关
    private static List<ProductTypeDto> listZoneProduct;                                    //专区产品


    private ProductTypeUtility() {
    }

    public static void setListZoneProduct(List<ProductTypeDto> listZoneProduct) {
        ProductTypeUtility.listZoneProduct = listZoneProduct;
    }

    public static List<ProductTypeDto> getListZoneProduct() {
        return listZoneProduct;
    }

    public static List<TblNegoFocusMasterEntity> getLstUserFocusDto() {
        return lstUserFocusDto;
    }

    public static void setListProductDto(List<ProductTypeDto> lst) {
        listProductDto = lst;
    }

    public static void setOpenFocus(boolean openFocus) {
        ProductTypeUtility.openFocus = openFocus;
    }

    public static void setLstUserFocusDto(List<TblNegoFocusMasterEntity> lstUserFocusDto) {
        ProductTypeUtility.lstUserFocusDto = lstUserFocusDto;
    }

    /**
     * @return 开启关注并且关注产品不为空
     */
    public static boolean openFocusAndNotEmpty() {
        return openFocus && !CollectionUtility.isNullOrEmpty(lstUserFocusDto);
    }


    public static ProductTypeDto getProductDtoByProductType(String productType) {
        //先从询价产品中获取
        if (listProductDto == null) {
            return null;
        }
        for (ProductTypeDto dto : listProductDto) {
            if (dto.getProductType().equals(productType)) {
                return dto;
            }
        }
        //未获取到则从专区产品中获取
        if (listZoneProduct == null) {
            return null;
        }
        for (ProductTypeDto dto : listZoneProduct) {
            if (dto.getProductType().equals(productType)) {
                return dto;
            }
        }
        return null;
    }


    /**
     * 根据产品类别获取产品名称
     */
    public static
    @NonNull
    String getProductName(@NonNull String productType) {
        ProductTypeDto dto = getProductDtoByProductType(productType);
        if (dto == null) {
            return productType;
        }
        return dto.getTypeName();
    }


    /**
     * 按大类获取关注的产品，productTop为空时返回全部
     *
     * @param productTop 产品大类
     * @return 已关注的产品集合
     */
    public static
    @Nullable
    List<TblNegoFocusMasterEntity> getFocusListByProductTop(@Nullable String productTop) {
        //productTop 为空，返回全部，关注列表为空,返回null
        if (TextUtils.isEmpty(productTop)) {
            return lstUserFocusDto;
        }
        if (CollectionUtility.isNullOrEmpty(lstUserFocusDto)) {
            return null;
        }

        List<TblNegoFocusMasterEntity> list = new ArrayList<>();
        for (TblNegoFocusMasterEntity entity : lstUserFocusDto) {
            if (entity.getProductType().startsWith(productTop)) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 按大类获取未关注的产品， 一定是3级小类别
     * productTop为空时返回所有未关注的小类别产品
     *
     * @param productTop 产品大类
     * @return 未关注的产品集合(只返回3级)
     */
    public static
    @Nullable
    List<ProductTypeDto> getUnFocusListByProductTop(@Nullable String productTop) {
        if (CollectionUtility.isNullOrEmpty(listProductDto)) {
            return null;
        }
        List<ProductTypeDto> list = new ArrayList<>();
        boolean emptyTop = TextUtils.isEmpty(productTop);
        String productType;
        for (ProductTypeDto dto : listProductDto) {
            //过滤产品大类
            productType = dto.getProductType();
            if (dto.getLevel() != 2 || !(emptyTop || productType.startsWith(productTop))) {
                continue;
            }
            boolean focused = false;
            if (!CollectionUtility.isNullOrEmpty(lstUserFocusDto)) {
                for (TblNegoFocusMasterEntity entity : lstUserFocusDto) {
                    if (entity.getProductType().equals(dto.getProductType())) {
                        focused = true;
                        break;
                    }
                }
            }
            if (!focused) {
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 获取 大类为productTop 的小类(3级)集合
     *
     * @param productTop 产品大类
     * @return 大类为productTop 的小类产品集合
     */
    public static List<ProductTypeDto> getListProductByParentType(String business, @Nullable String productTop) {
        return getListProductByParentAndLevel(business, productTop, 2);
    }

    /**
     * 获取 大类为productTop 的 level级小类集合
     *
     * @param parentType 产品大类
     * @param level      级别
     * @return 大类为productTop 的 level级小类集合
     */
    public static List<ProductTypeDto> getListProductByParentAndLevel(String businessType, @Nullable String parentType, int level) {
        List<ProductTypeDto> listProduct;
        if (SptConstant.BUSINESS_ZONE.equals(businessType)) {
            listProduct = listZoneProduct;
        } else {
            listProduct = listProductDto;
        }

        if (CollectionUtility.isNullOrEmpty(listProduct)) {
            return null;
        }
        List<ProductTypeDto> list = new ArrayList<>();
        boolean isEmptyTop = TextUtils.isEmpty(parentType);
        for (ProductTypeDto dto : listProduct) {
            if (dto.getLevel() == level && (isEmptyTop || dto.getProductType().startsWith(parentType))) {
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * ProductTypeDto 转NameValueItem
     *
     * @param lst 集合
     * @return .
     */
    public static List<NameValueItem> listDto2NameValueItem(List<ProductTypeDto> lst) {
        return listDto2NameValueItem(lst, false);
    }

    public static List<NameValueItem> listDto2NameValueItem(List<ProductTypeDto> lst, boolean addNone) {
        if (lst == null || lst.isEmpty()) return null;
        List<NameValueItem> lstNameValueItem = new ArrayList<>();
        NameValueItem item;
        for (ProductTypeDto dto : lst) {
            item = new NameValueItem();
            item.setName(dto.getTypeName());
            item.setValue(dto.getProductType());
            lstNameValueItem.add(item);
        }
        if (addNone) {   //“不限”条目
            NameValueItem none = new NameValueItem();
            none.setName("不限");
            none.setValue("0");
            none.setTag("100");
            lstNameValueItem.add(0, none);
        }

        return lstNameValueItem;
    }

    /**
     * @param productTop 顶级大类名称
     * @return 大类中关注的产品返回NameValueItem 类型
     */
    public static List<NameValueItem> getListFocus2NameValueItem(@Nullable String productTop) {
        if (CollectionUtility.isNullOrEmpty(lstUserFocusDto)) {
            return null;
        }
        List<NameValueItem> list = new ArrayList<>();
        String productType;
        boolean isEmptyTop = TextUtils.isEmpty(productTop);
        NameValueItem item;
        for (TblNegoFocusMasterEntity entity : lstUserFocusDto) {
            productType = entity.getProductType();
            if (isEmptyTop || productType.startsWith(productTop)) {
                item = new NameValueItem();
                item.setName(getProductName(productType));
                item.setValue(productType);
                list.add(item);
            }
        }
        return list;
    }


}
