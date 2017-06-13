package com.totrade.spt.mobile.bean;

/**
 * 上传文档类型枚举
 */
public enum DocumentsType {
    /**
     * 营业执照
     */
    COMPANYLICENSE,
    /**
     * 银行账户开户批准
     */
    COMPANYBANK,
    /**
     * 上年审计报表
     */
    COMPANYAUDIT,
    /**
     * 质检单附件
     */
    QUALITY,
    /**
     * 原产地证明
     */
    ORIGIN,
    /**
     * 生产许可证
     */
    LICENSE,
    /**
     * 其他
     */
    OTHER;

    /**
     * 通过枚举序数获取枚举对象
     *
     * @param ordinal 序数
     * @return
     */
    public static DocumentsType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }

}


