package com.totrade.spt.mobile.entity;

import java.util.List;

/**
 *
 * Created by iUserName on 2017/1/12.
 */

public class GTDeliveryPlaceEntity
{
    private String name;
    private String value;
    private List<GTDeliveryPlaceEntity> childNots;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public List<GTDeliveryPlaceEntity> getChildNots()
    {
        return childNots;
    }

    public void setChildNots(List<GTDeliveryPlaceEntity> childNots)
    {
        this.childNots = childNots;
    }
}
