package com.totrade.spt.mobile.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.autrade.spt.bank.constants.BankConstant;
import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.constants.DealConstant;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.LoginUserContext;


/**
 * Created by iUserName on 16/11/21.
 * 用于判断业务逻辑类
 */

public class LogicChoicesUtility
{

    /**
     * 需要注意的是selfBuySell 字段为我方意图
     * 判断是否可用使用定金贷
     * @param selfBuySell	我方意图买或者卖
     * @param bond	保证金代号
     * @return  true:可以使用定金贷款，false:不可以使用
     */
    public static boolean canUseLoan(String selfBuySell, String bond)
    {
        if(TextUtils.isEmpty(selfBuySell) || TextUtils.isEmpty(bond))
        {
            return false;
        }
        boolean bondCanUseLoan =! (bond.contains("-") || bond.equals("0") || bond.equals("00") || bond.equals("100"));
        boolean paySystemCanUseLoan = LoginUserContext.getLoginUserDto().getPaySystem().equals(BankConstant.PAY_SYSTEM_CITIC);
        boolean buySellCanUseLoan = selfBuySell.equals(SptConstant.BUYSELL_BUY);
        return bondCanUseLoan && paySystemCanUseLoan && buySellCanUseLoan;
    }


    /**
     * 合同用户可操作按钮
     * @param isBuyOrder 是否买单
     * @param isZoneOrder  是否专区合同
     * @param isZoneMiddle  如果是专区，是否专区中间环节
     * @param declareDelivery  如果专区，是否可“发起交收”
     * @param bondPayStatusTag  合同状态
     * @param buyerArgue  买家异议内容
     * @param sellerArgue  卖家异议内容
     * @return  用户可操作按钮文字
     */
    public static String[] contractUserCanDo(boolean isBuyOrder, boolean isZoneOrder,boolean isZoneMiddle,boolean declareDelivery,
                                             @NonNull String bondPayStatusTag, @Nullable String buyerArgue, @Nullable String sellerArgue)
    {
        boolean isBuyerArgue = !TextUtils.isEmpty(buyerArgue);
        boolean isSellerArgue = !TextUtils.isEmpty(sellerArgue);

        if(bondPayStatusTag.equals(DealConstant.BONDPAYSTATUS_TAG_BOND_PAYING)                      //定金支付中
                || bondPayStatusTag.equals(DealConstant.BONDPAYSTATUS_TAG_BOND_PAYING)              //全款支付中
                ||(isZoneOrder && isZoneMiddle)                                                     //专区中间环节
                ||((isBuyOrder && isSellerArgue) || (!isBuyOrder && isBuyerArgue))                  //对方异议
        )
        {
            return declareDelivery ? new String[]{AppConstant.ACTION_DECLARE_DELIVERY,null} : null; //发起交收，数组统一为2个元素或者null;
        }

        String[] actions = new String[2];

        if(isBuyerArgue || isSellerArgue)       //对方异议已返回，一定是我方异议
        {
            actions[0] = AppConstant.ACTION_UNARGUE;            //解除异议
            actions[1] = AppConstant.ACTION_UPDATEARGUE;         //修改异议
        }
        else
        {
            if(LoginUserContext.getLoginUserDto().isCanPayAll() && isBuyOrder && bondPayStatusTag.equals(DealConstant.BONDPAYSTATUS_TAG_BOND_PAID)) //买单已付保证金
            {
                actions[0] = AppConstant.ACTION_PAY;
            }
            else if(LoginUserContext.getLoginUserDto().isCanReceive() && isBuyOrder && bondPayStatusTag.equals(DealConstant.BONDPAYSTATUS_TAG_SENT))   //买单已发货
            {
                actions[0] = AppConstant.ACTION_RECEIVE;
            }
            else if(LoginUserContext.getLoginUserDto().isCanDelivery() && !isBuyOrder && bondPayStatusTag.equals(DealConstant.BONDPAYSTATUS_TAG_ALL_PAID))  //卖单已付全款
            {
                actions[0] = AppConstant.ACTION_DELIVETY;
            }
//            else
//            {
//                Log.i("bondPayTag", bondPayStatusTag);  //这里是空的
//            }

            if(!isZoneOrder)        //询价可以提出异议
            {
                if(actions[0] == null)
                {
                    actions[0] = AppConstant.ACTION_ARGUE;     //提出异议
                }
                else
                {
                    actions[1] = AppConstant.ACTION_ARGUE;     //提出异议
                }
            }
            else if(declareDelivery)
            {
                if(actions[0]==null)
                {
                    actions[0] = AppConstant.ACTION_DECLARE_DELIVERY;
                }
                else
                {
                    actions[1] = AppConstant.ACTION_DECLARE_DELIVERY;
                }
            }
//            else
//            {
//                Log.i("bondPayTag", bondPayStatusTag);  //这里是空的
//            }
        }
        return actions;
    }

}
