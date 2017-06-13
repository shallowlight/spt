package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.MyStockDownEntity;
import com.autrade.spt.deal.entity.QueryMyStockUpEntity;
import com.autrade.spt.zone.constants.ZoneConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.dto.ZoneRequestMatchUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestMatchService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.ActivityConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.utility.Temp;
import com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity;
import com.totrade.spt.mobile.ui.maintrade.TradeRuleHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CommonTitleZone;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.PropertyView;
import com.totrade.spt.mobile.ui.maintrade.TransferInventoryActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 买单详情
 *
 * @author huangxy
 * @date 2017/5/8
 */
public class BuyerFragment extends BaseFragment implements View.OnClickListener {

    private static final String BATCH_DELIVERY = "0";

    private OrderDeatilActivity activity;
    private View rootView;
    private CommonTitleZone titleView;
    private PropertyView pv_unit_price;
    private PropertyView pv_number;
    private TextView tv_deal;
    private TextView tv_to_stock;
    private TextView tv_preview;
    private CheckBox cbAgreeContract;
    private TextView lblReadContract;
    private PropertyView pv_select_stock;

    private TblZoneRequestOfferEntity offerEntity;
    private ZoneRequestDownEntity requestEntity;
    private MyStockDownEntity mDataFromStock;
    private TextView tv_buy_hint;
    private CheckBox cb_agree;
    private boolean mIsPartDeal;//是否允许部分成交
    private String title;

    private TextView tv_order_num;      //订单号
    private TextView tv_deliveryplace;      //交货地
    private TextView tv_product_quality;      //质量标准
    private TextView tv_bond;      //定金
    private TextView tv_price;      //价格

    public BuyerFragment() {
        setContainerId(R.id.framelayout);
    }

    public void setData(TblZoneRequestOfferEntity offerEntity, ZoneRequestDownEntity requestEntity, String title) {
        this.offerEntity = offerEntity;
        this.requestEntity = requestEntity;
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (OrderDeatilActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_zone_buy, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cbAgreeContract.setChecked(false);
        initData();
    }

    private void initView() {
        titleView = (CommonTitleZone) rootView.findViewById(R.id.title);
        pv_select_stock = (PropertyView) rootView.findViewById(R.id.pv_select_stock);
        pv_unit_price = (PropertyView) rootView.findViewById(R.id.pv_unit_price);
        pv_number = (PropertyView) rootView.findViewById(R.id.pv_number);
        tv_deal = (TextView) rootView.findViewById(R.id.tv_deal);
        tv_preview = (TextView) rootView.findViewById(R.id.tv_preview);
        tv_to_stock = (TextView) rootView.findViewById(R.id.tv_to_stock);
        tv_buy_hint = (TextView) rootView.findViewById(R.id.tv_buy_hint);
        cb_agree = (CheckBox) rootView.findViewById(R.id.cb_agree);

        tv_order_num = (TextView) rootView.findViewById(R.id.tv_order_num);
        tv_deliveryplace = (TextView) rootView.findViewById(R.id.tv_deliveryplace);
        tv_product_quality = (TextView) rootView.findViewById(R.id.tv_product_quality);
        tv_bond = (TextView) rootView.findViewById(R.id.tv_bond);
        tv_price = (TextView) rootView.findViewById(R.id.tv_price);
        cbAgreeContract = (CheckBox) rootView.findViewById(R.id.cbAgreeContract);
        lblReadContract = (TextView) rootView.findViewById(R.id.lblReadContract);

        lblReadContract.setText(TradeRuleHelper.i.getClickableSpan(activity));
        lblReadContract.setMovementMethod(LinkMovementMethod.getInstance());        //超链接可点击
    }

    private void initData() {
        setHead();
        titleView.setTitle(null != title ? title : "");
        cb_agree.setText("部分成交");

        //设置初始化数据
        String unitPrice = FormatUtil.format.format(offerEntity.getProductPrice());
        pv_unit_price.setDisplayText(unitPrice);
//        boolean bo = offerEntity.getOfferStatus().equals(ZoneConstant.OFFER_STATUS_PART_DEAL);
//        String increases = FormatUtil.format.format(bo ? offerEntity.getRemainNumber() : offerEntity.getProductNumber());
        pv_number.getmValueView().setText(FormatUtil.format.format(offerEntity.getRemainNumber()));
        if (null != activity.getCurDataFromStock()) {
            mDataFromStock = (MyStockDownEntity) activity.getCurDataFromStock();
            pv_select_stock.setDisplayViewTextSmall(mDataFromStock.getProductName()
                    + " " + DispUtility.price2Disp(mDataFromStock.getProductPrice(), mDataFromStock.getPriceUnit(), mDataFromStock.getNumberUnit())
                    + " " + DispUtility.productNum2Disp(mDataFromStock.getDealNumber(), null, mDataFromStock.getNumberUnit()));
        } else
            pv_select_stock.setDisplayViewHint("请选择");

        //设置初始化View的状态
        initViewState();

        titleView.getImgBack().setOnClickListener(this);
        tv_deal.setOnClickListener(this);
        tv_preview.setOnClickListener(this);
        tv_to_stock.setOnClickListener(this);

        pv_select_stock.setOnPropertyClickListener(mOnPropertyClickListener);
        cb_agree.setOnCheckedChangeListener(mOnCheckedChangeListener2);
        pv_number.setAfterTextChangeListener(mAfterTextChangeListener);
    }

    private void setHead() {
        // 订单号
        tv_order_num.setText(ContractHelper.instance.getOrderId(requestEntity.getZoneOrderNoDto()));
        // 交货地
        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, requestEntity.getDeliveryPlace());
        if (requestEntity.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
            deliveryPlace = requestEntity.getDeliveryPlaceName();
        }
        tv_deliveryplace.setText(deliveryPlace);

        // 质量标准字段(塑料显示牌号，钢铁显示铁品位)
        if (requestEntity.getProductType().startsWith("GT")) {
            String qualityEx1 = new DecimalFormat("#0.#######").format(requestEntity.getProductQualityEx1());
            tv_product_quality.setText(qualityEx1);
        } else if (requestEntity.getProductType().startsWith("SL")) {
            tv_product_quality.setText(requestEntity.getBrand());
        } else {
            tv_product_quality.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, requestEntity.getProductQuality()));
        }

        //定金
        tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, requestEntity.getBond()));

        //价格
        if (requestEntity.getDealPrice() == null) {
            tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
            tv_price.setText("暂无");
            tv_price.setCompoundDrawables(null, null, null, null);
        } else {
            String price = DispUtility.price2Disp(requestEntity.getDealPrice(), requestEntity.getPriceUnit(), requestEntity.getNumberUnit());
            if (requestEntity.getIncAmount() == null || requestEntity.getIncAmount().compareTo(BigDecimal.ZERO) == 0) {
                tv_price.setText(String.format("%s-", price));
                tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
                tv_price.setCompoundDrawables(null, null, null, null);
            } else {
                tv_price.setText(price);
                Drawable drawable;
                if (requestEntity.getIncAmount().compareTo(BigDecimal.ZERO) > 0) {
                    tv_price.setTextColor(activity.getResources().getColor(R.color.zone_red_view));
                    drawable = ContextCompat.getDrawable(activity, R.drawable.zone_arrow_red);
                } else {
                    tv_price.setTextColor(activity.getResources().getColor(R.color.zone_green_view));
                    drawable = ContextCompat.getDrawable(activity, R.drawable.zone_arrow_green);
                }

                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_price.setCompoundDrawables(null, null, drawable, null);
            }

        }
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener2 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mIsPartDeal = isChecked;
            isEnAbleEdit(pv_number.getmValueView(), mIsPartDeal);
        }
    };

    private PropertyView.OnPropertyClickListener mOnPropertyClickListener = new PropertyView.OnPropertyClickListener() {
        @Override
        public void OnPropertyClick(PropertyView v) {
            activity.switchSOFSF(null, offerEntity, requestEntity);
        }
    };

    private PropertyView.AfterTextChangeListener mAfterTextChangeListener = new PropertyView.AfterTextChangeListener() {

        @Override
        public void afterTextChange(Editable editable) {

            BigDecimal curProductNums;
            if (!StringUtility.isNullOrEmpty(editable.toString().trim()))
                curProductNums = new BigDecimal(editable.toString().trim());
            else
                curProductNums = new BigDecimal("0");

            if (mIsPartDeal && offerEntity.getProductNumber().compareTo(curProductNums) < 0) {
                tv_buy_hint.setText("数量超出");
                tv_buy_hint.setVisibility(View.VISIBLE);
                enBtnClickState(tv_deal, R.drawable.shape_half_circle_gray, false);
            } else if (mIsPartDeal && null != offerEntity.getMinNumber() && offerEntity.getMinNumber().compareTo(curProductNums) > 0) {
                tv_buy_hint.setVisibility(View.VISIBLE);
                enBtnClickState(tv_deal, R.drawable.shape_half_circle_gray, false);
                tv_buy_hint.setText("不得小于最小成交量");
            } else {
                tv_buy_hint.setVisibility(View.GONE);
                enBtnClickState(tv_deal, R.drawable.btn_bg_blue2, true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
            case R.id.tv_screen:
                activity.popBack();
                break;
            case R.id.tv_deal:
                if (valid()) {
                    showDealDialog();
                }
                break;
            case R.id.tv_to_stock:
                Temp.i().put(BuyerFragment.class, offerEntity.getOfferId());
                QueryMyStockUpEntity upEntity = new QueryMyStockUpEntity();
                upEntity.setBond(offerEntity.getBond());
                upEntity.setProductType(offerEntity.getProductType());
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setBondPayStatusTagCondition("1|9");
                upEntity.setBuyerZoneStatusCondition("O");
                upEntity.setDeliveryPlace(offerEntity.getDeliveryPlace());
                upEntity.setDeliveryTime(offerEntity.getDeliveryTime());
                upEntity.setProductPlace(requestEntity.getProductPlace());
                upEntity.setProductQuality(requestEntity.getProductQuality());
                upEntity.setTradeMode(offerEntity.getTradeMode());
                upEntity.setContractZoneStatus("U");

                Intent intent = new Intent(activity, TransferInventoryActivity.class);
                intent.putExtra(QueryMyStockUpEntity.class.getName(), JsonUtility.toJSONString(upEntity));
                intent.putExtra(ActivityConstant.SPTDICT_BUY_SELL, ActivityConstant.BUYSELL_SELL);
                intent.putExtra(ActivityConstant.Zone.KEY_ACTION, ActivityConstant.Zone.STOCK_BUY_DETAIL);
                intent.putExtra(ActivityConstant.Zone.PRODUCT_TYPE, requestEntity.getProductType());
                activity.startActivity(intent);
                break;
            case R.id.tv_preview:
                ContractHelper.instance.readContract(requestEntity, offerEntity,
                        pv_number.getmValueView().getText().toString(), pv_unit_price.getmValueView().getText().toString(),
                        SptConstant.BUYSELL_SELL.equals(offerEntity.getBuySell()));
                break;
            default:

                break;
        }
    }

    private void showDealDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity, "确定要成交吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dealZoneOffer();
            }
        });
        builder.create().show();
    }

    private void dealZoneOffer() {
        SubAsyncTask.create().setOnDataListener(activity, true, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneRequestMatchUpEntity upEntity = new ZoneRequestMatchUpEntity();
                upEntity.setSource(ZoneConstant.ZoneSource.Android);
                upEntity.setDealNumber(new BigDecimal(pv_number.getmValueView().getText().toString().trim()));
                upEntity.setSubmitUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setMatchOfferId(offerEntity.getOfferId());
                upEntity.setBatchMode(offerEntity.getBatchMode());
                if (null != mDataFromStock) {
                    upEntity.setContractId(mDataFromStock.getContractId());
                } else {
                    upEntity.setContractId(offerEntity.getContractId());
                }
                upEntity.setOfferType("N");
                upEntity.setMatchOfferId(offerEntity.getOfferId());
                Client.getService(IZoneRequestMatchService.class).dealZoneOffer(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("成交成功");
                    activity.popBack();
                }
            }
        });
    }


    /**
     * 初始化View显示或隐藏，可编辑与不可编辑的状态
     */
    private void initViewState() {
        pv_select_stock.setVisibility(View.GONE);
        if (offerEntity.getBatchMode().equals(BATCH_DELIVERY)) {
            cb_agree.setChecked(false);
            cb_agree.setVisibility(View.GONE);
        } else {
            cb_agree.setVisibility(View.VISIBLE);
        }
        isEnAbleEdit(pv_number.getmValueView(), cb_agree.isChecked());
    }

    private void isEnAbleEdit(EditText editText, boolean bo) {
        editText.setFocusable(bo);
        editText.setEnabled(bo);
        editText.setClickable(bo);
        editText.setFocusableInTouchMode(bo);
        if (bo)
            editText.requestFocus();
    }

    private void enBtnClickState(TextView textView, int backgroundId, boolean bo) {
        textView.setBackgroundResource(backgroundId);
        textView.setClickable(bo);
        textView.setEnabled(bo);
        textView.setFocusable(bo);
        textView.setSaveEnabled(false);
    }

    /**
     * 是否阅读协议
     * 是否自己的报价
     *
     * @return
     */
    private boolean valid() {
        if (!cbAgreeContract.isChecked()) {
            ToastHelper.showMessage("请阅读并同意规则及协议");
            return false;
        }
        if (LoginUserContext.getLoginUserDto().getUserId().equals(offerEntity.getOfferUserId())) {
            ToastHelper.showMessage("不允许自成交");
            return false;
        }

        return true;
    }

}
