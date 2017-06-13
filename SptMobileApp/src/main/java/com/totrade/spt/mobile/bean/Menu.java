package com.totrade.spt.mobile.bean;

import com.autrade.stage.entity.EntityBase;

/**
 * Created by Timothy on 2017/4/7.
 */

public class Menu extends EntityBase{
    private int drawId;
    private String title;
    private String premissions;
//    private Class<?> clazz;
    private MenuNavigation menuHint;
    private MenuNavigation menuHint2;
    private MenuNavigation menuHint3;

//    private String hint;//一级提示
//    private String hint2;//二级提示
//    private String hint3;//三级提示

//    public Menu(int drawId, String title,String premissions,Class<?> clazz,String hint,String hint2,String hint3) {
//        this.drawId = drawId;
//        this.title = title;
////        this.clazz = clazz;
//        this.premissions = premissions;
//        this.hint = hint;
//        this.hint2 = hint2;
//        this.hint3 = hint3;
//    }

    public Menu(int drawId, String title, String premissions, MenuNavigation menuHint, MenuNavigation menuHint2, MenuNavigation menuHint3) {
        this.drawId = drawId;
        this.title = title;
        this.premissions = premissions;
        this.menuHint = menuHint;
        this.menuHint2 = menuHint2;
        this.menuHint3 = menuHint3;
    }

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPremissions() {
        return premissions;
    }

    public void setPremissions(String premissions) {
        this.premissions = premissions;
    }

    public MenuNavigation getMenuHint() {
        return menuHint;
    }

    public void setMenuHint(MenuNavigation menuHint) {
        this.menuHint = menuHint;
    }

    public MenuNavigation getMenuHint2() {
        return menuHint2;
    }

    public void setMenuHint2(MenuNavigation menuHint2) {
        this.menuHint2 = menuHint2;
    }

    public MenuNavigation getMenuHint3() {
        return menuHint3;
    }

    public void setMenuHint3(MenuNavigation menuHint3) {
        this.menuHint3 = menuHint3;
    }

}
