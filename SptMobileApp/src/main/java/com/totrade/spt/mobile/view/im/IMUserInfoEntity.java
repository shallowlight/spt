package com.totrade.spt.mobile.view.im;

import com.netease.nimlib.sdk.team.constant.TeamMemberType;

/**
 * 群成员
 *
 * @author huangxy
 * @date 2016/11/30
 */
public class IMUserInfoEntity {

    String letter;      //索引首字

//    String signature;     //签名
//
//    GenderEnum gender;     //性别
//
//    String email;     //
//
//    String birthday;     //
//
//    String mobile;     //
//
//    String extension;       //扩展字段
//
//    Map<String, Object> extensionMap;       //扩展字段Map格式

    TeamMemberType type;       //群成员类型

    String account;     //用户帐号

    String name;     //用户名

    String avatar;     //用户头像地址

    public IMUserInfoEntity() {
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public TeamMemberType getType() {
        return type;
    }

    public void setType(TeamMemberType type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
