package com.totrade.spt.mobile.bean;

import com.autrade.spt.zone.dto.QueryPageZoneCompanyDownEntity;

public class ZoneCompany {
    private String letter;
    private QueryPageZoneCompanyDownEntity queryPageZoneCompanyDownEntity;

    public QueryPageZoneCompanyDownEntity getQueryPageZoneCompanyDownEntity() {
        return queryPageZoneCompanyDownEntity;
    }

    public void setQueryPageZoneCompanyDownEntity(QueryPageZoneCompanyDownEntity queryPageZoneCompanyDownEntity) {
        this.queryPageZoneCompanyDownEntity = queryPageZoneCompanyDownEntity;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public ZoneCompany(String letter, QueryPageZoneCompanyDownEntity queryPageZoneCompanyDownEntity) {
        this.letter = letter;
        this.queryPageZoneCompanyDownEntity = queryPageZoneCompanyDownEntity;
    }

    public ZoneCompany() {
    }
}
