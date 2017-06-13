package com.totrade.spt.mobile.bean;

import com.autrade.spt.common.constants.SptConstant;
import com.totrade.spt.mobile.view.R;


/**
 * 供应商等级枚举
 * Created by Timothy on 2017/1/18.
 */
public enum SupplierLevel {

    SUPLEVEL_ZS("1",SptConstant.SupLevel.L1.getText(), R.drawable.img_level1_gray, R.drawable.img_level1),
    SUPLEVEL_HG("2",SptConstant.SupLevel.L2.getText(), R.drawable.img_level2_gray, R.drawable.img_level2),
    SUPLEVEL_XX("3",SptConstant.SupLevel.L3.getText(), R.drawable.img_level3_gray, R.drawable.img_level3),
    SUPLEVEL_HD("4",SptConstant.SupLevel.L4.getText(), R.drawable.img_level4_gray, R.drawable.img_level4),
    SUPLEVEL_LC("5",SptConstant.SupLevel.L5.getText(), R.drawable.img_level5_gray, R.drawable.img_level5),
    SUPLEVEL_LD("6",SptConstant.SupLevel.L6.getText(), R.drawable.img_level6_gray, R.drawable.img_level6),
    SUPLEVEL_BLACK("99",SptConstant.SupLevel.L99.getText(), R.drawable.img_level7_gray, R.drawable.img_level7);

    private String id;
    private String name;
    private int grayIconId;
    private int multiColorId;

    SupplierLevel(String id,String name, int grayIconId, int multiColorId) {
        this.id = id;
        this.name = name;
        this.grayIconId = grayIconId;
        this.multiColorId = multiColorId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGrayIconId() {
        return grayIconId;
    }

    public int getMultiColorId() {
        return multiColorId;
    }
}
