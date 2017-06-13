package com.totrade.spt.mobile.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.autrade.spt.bank.constants.BankConstant;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.LoginUserDto;
import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.bean.Dictionary;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.ui.login.fragment.CompanyBindFragment;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录用户上下文
 *
 * @author wangyilin
 */
public final class LoginUserContext {
    public static boolean hasRequestAllCoList = false;

    public static LoginUserDto getLoginUserDto() {
        return loginUserDto;
    }

    public static void setLoginUserDto(LoginUserDto loginUserDto) {
        LoginUserContext.loginUserDto = loginUserDto;
        if (TextUtils.isEmpty(loginUserDto.getPaySystem())) {
            //如果没获取到则全部当中信银行处理
            loginUserDto.setPaySystem(BankConstant.PAY_SYSTEM_CITIC);
        }
    }

    public static String getLoginUserId() {
        return loginUserDto.getUserId();
    }

    public static LoginUserDto getAnonymousDto() {
        return anonymousDto;
    }

    public static void setAnonymousDto(LoginUserDto anonymousDto) {
        LoginUserContext.anonymousDto = anonymousDto;
    }

    private static LoginUserDto loginUserDto; // 用户dto
    private static LoginUserDto anonymousDto; // 游客用户

    private static String[] companyTag = {AppConstant.COMPANYTAG_HBGT};

    // 当前用户是否匿名用户
    public static boolean isAnonymous() {
        return loginUserDto == null
                || loginUserDto.getUserId().equals(SptConstant.USER_ID_ANONYMOUS);
    }

    //TODO 此处需要修改
    public static boolean hasBindAccount() {
        if (null == loginUserDto) return false;
        else if (null == loginUserDto.getAccountBindFlag()) return false;
        else return !isAnonymous() && loginUserDto.getAccountBindFlag().equals("1");
    }

    /**
     * 是否自由人
     */
    public static boolean isFreeman() {
        return loginUserDto.getConfigGroupId().contains(SptConstant.CONFIGGROUPID_TAG_FREEMAN);
    }

    /**
     * 判断是否是企业管理者
     */
    public static boolean isCompanyMaster() {
        return loginUserDto.getConfigGroupId().contains(SptConstant.CONFIGGROUPID_TAG_MANGER);
    }

    /**
     * 是否交易员
     *
     * @return
     */
    public static boolean isTrader() {
        if (null == loginUserDto) return false;
        return loginUserDto.getConfigGroupId().contains(SptConstant.CONFIGGROUPID_TAG_TRADE);
    }

    /**
     * 是否 资金账户,财务
     *
     * @return
     */
    public static boolean isFundAccount() {
        if (null == loginUserDto) return false;
        return loginUserDto.getConfigGroupId().contains(SptConstant.CONFIGGROUPID_TAG_FUND);
    }

    /**
     * 是否 物管账户,单证
     *
     * @return
     */
    public static boolean isDeliver() {
        if (null == loginUserDto) return false;
        return loginUserDto.getConfigGroupId().contains(SptConstant.CONFIGGROUPID_TAG_DELIVE);
    }

    /**
     * 据用户角色获取对应的显示文字
     *
     * @return
     */
    public static String getDisPlayNameFromUserPermission() {
        if (loginUserDto == null) {
            return Dictionary.CodeToKey(Dictionary.MASTER_USER_DENTITY, SptConstant.USER_ID_ANONYMOUS);
        } else {
            return Dictionary.CodeToKey(Dictionary.MASTER_USER_DENTITY, loginUserDto.getConfigGroupId());
        }
    }

    private static Map<String, Map<String, String>> mapUserInfo; // 云信获取

    public static void setMapIMUserInfo(Map<String, Map<String, String>> mapNIMUserInfo) {
        LoginUserContext.mapUserInfo = mapNIMUserInfo;
    }

    public static Map<String, Map<String, String>> getMapIMUserInfo() {
        if (mapUserInfo == null) {
            mapUserInfo = new HashMap<>();
        }
        return mapUserInfo;
    }

    public static String getLoginUserAccId() {
        return loginUserDto.getImUserAccid();
    }

    private static String getIconOrNameByAccid(String accid, String key) {
        String value = null;

        if (mapUserInfo != null && !mapUserInfo.isEmpty()) {
            Map<String, String> subMap = mapUserInfo.get(accid);
            if (subMap != null && !subMap.isEmpty()) {
                value = subMap.get(key);
            }
        }
        return value;
    }

    public static String getIconUrlByAccid(String accid) {
        if (TextUtils.isEmpty(accid))
            return "";
        String name = getIconOrNameByAccid(accid, AppConstant.ICON_URL);
        if (TextUtils.isEmpty(accid))
            name = accid;
        return name;
    }

    public static String getPickNameByAccid(String accid) {
        if (TextUtils.isEmpty(accid))
            return "";
        String name = getIconOrNameByAccid(accid, AppConstant.TAG_PETNAME);
        if (TextUtils.isEmpty(accid)) {
            name = accid;
        }
        return name;
    }

    public static boolean speicalProduct(String productType) {
        String[] array = SptApplication.context.getResources().getStringArray(R.array.special_product);
        List<String> list = Arrays.asList(array);
        return list.contains(productType);
    }


    /****************************************
     * Deprecated
     *****************************************/
    public static boolean isZoneUser() {
        if (!canDoMatchBusiness()) {
            return false;
        }
        String userCoTag = loginUserDto.getCompanyTag();
        for (String s : companyTag) {
            if (s.equals(userCoTag)) {
                return true;
            }
        }
        return false;
    }

    public static void showErrorUserPermissionMsg() {
        if (LoginUserContext.isAnonymous()) {
            ToastHelper.showMessage("您还未登录，请先登录");
        } else if (!LoginUserContext.getLoginUserDto().isCompanyUser()) {
            ToastHelper.showMessage("个人帐户不允许操作，请升级企业帐户");
        } else if (!LoginUserContext.hasBindAccount()) {
            ToastHelper.showMessage("您还未绑定资金账户，请先绑定资金账号");
        }
    }

    /**
     * 是否可以做撮合业务
     *
     * @return
     */
    public static boolean canDoMatchBusiness() {
        // 不为null,不为匿名，绑定资金账户且企业用户
        return hasBindAccount() && loginUserDto.isCompanyUser();
    }
}
