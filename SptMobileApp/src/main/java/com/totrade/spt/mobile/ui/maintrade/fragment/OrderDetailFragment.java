package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestOfferEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.google.gson.reflect.TypeToken;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.mainhome.LineChatFragment;
import com.totrade.spt.mobile.ui.maintrade.ContractHelper;
import com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity;
import com.totrade.spt.mobile.ui.maintrade.PowerHelper;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.NotifyUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 贸易大厅订单详情
 *
 * @author huangxy
 * @date 2017/4/12
 */
public class OrderDetailFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private OrderDeatilActivity activity;

    private LinearLayout ll_sell;       //卖方列表
    private LinearLayout ll_buy;       //买方列表
    private TextView tv_join_sell;      //加入卖
    private TextView tv_join_buy;      //加入买
    private TextView tv_order_num;      //订单号
    private TextView tv_deliveryplace;      //交货地
    private TextView tv_product_quality;      //质量标准
    private TextView tv_bond;      //定金
    private TextView tv_price;      //价格
    private TextView tv_index;      //最新指数
    private TextView tv_release_time;      //发布时间
    private FrameLayout fl_index_chart;    //折线图
    private int height;    //折线图,做动画用

    private ZoneRequestDownEntity mEntity;

    public OrderDetailFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (OrderDeatilActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_trade_order_detail, container, false);
        initViews();
//        initData();

        return rootView;
    }

    private void initViews() {
        ll_sell = (LinearLayout) rootView.findViewById(R.id.ll_sell);
        ll_buy = (LinearLayout) rootView.findViewById(R.id.ll_buy);
        tv_join_sell = (TextView) rootView.findViewById(R.id.tv_join_sell);
        tv_join_buy = (TextView) rootView.findViewById(R.id.tv_join_buy);

        tv_order_num = (TextView) rootView.findViewById(R.id.tv_order_num);
        tv_deliveryplace = (TextView) rootView.findViewById(R.id.tv_deliveryplace);
        tv_product_quality = (TextView) rootView.findViewById(R.id.tv_product_quality);
        tv_bond = (TextView) rootView.findViewById(R.id.tv_bond);
        tv_price = (TextView) rootView.findViewById(R.id.tv_price);

        tv_index = (TextView) rootView.findViewById(R.id.tv_index);
        tv_release_time = (TextView) rootView.findViewById(R.id.tv_release_time);
        fl_index_chart = (FrameLayout) rootView.findViewById(R.id.fl_index_chart);

        tv_join_sell.setOnClickListener(this);
        tv_join_buy.setOnClickListener(this);
        tv_release_time.setOnClickListener(this);
        rootView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        ((TextView) rootView.findViewById(R.id.title)).setText("专区详情");
        parseIntent();
    }

    private void initData() {
        setHead();
        setChart();
        setBuySellList();
    }

    private void setChart() {
        tv_index.setText("最新指数:" + mEntity.getIndexNumber().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
        tv_release_time.setText(new SimpleDateFormat("yyyy/MM/dd HH时发布").format(mEntity.getIndexTime()));
        LineChatFragment lineChatFragment = LineChatFragment.newInstance(mEntity.getProductType());
        getChildFragmentManager().beginTransaction().add(R.id.fl_index_chart, lineChatFragment).commitAllowingStateLoss();
    }

    /**
     * 设置买卖列表数据
     */
    private void setBuySellList() {
        String[] sell = new String[]{"卖一", "卖二", "卖三", "卖四",};
        String[] buy = new String[]{"买一", "买二", "买三", "买四",};

        List<TblZoneRequestOfferEntity> buyerList = new ArrayList<>();
        List<TblZoneRequestOfferEntity> sellerList = new ArrayList<>();
        for (TblZoneRequestOfferEntity offerEntity : mEntity.getOfferList()) {
            if (SptConstant.BUYSELL_SELL.equals(offerEntity.getBuySell())) {
                sellerList.add(offerEntity);
            } else {
                buyerList.add(offerEntity);
            }
        }

        for (int i = 0; i < sell.length * 2; i++) {
            if (i % 2 == 0) {
                fillBuySellList(buy, buyerList, (i / 2) % 4, true);
            } else {
                fillBuySellList(sell, sellerList, (i / 2) % 4, false);
            }
        }
    }

    /**
     * 填充数据到买卖列表
     */
    private void fillBuySellList(final String[] array, List<TblZoneRequestOfferEntity> buySellList, final int p, boolean isBuy) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_order_buysell, null, false);
        final TextView tv_buy_sell = (TextView) view.findViewById(R.id.tv_buy_sell);
        TextView tv_buysell_price = (TextView) view.findViewById(R.id.tv_price);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_num);

        tv_buysell_price.setText("---");
        tv_num.setText("---");

        tv_buy_sell.setText(array[p]);
        tv_buy_sell.setBackgroundColor(isBuy ?
                activity.getResources().getColor(R.color.ui_red) :
                activity.getResources().getColor(R.color.ui_green));

        final String str = array[p];
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PowerHelper.i().hasPower(activity)) {
                    if (str.contains("卖")) {
                        activity.switchJSF(SptConstant.BUYSELL_SELL, mEntity);
                    }
                    else {
                        activity.switchJSF(SptConstant.BUYSELL_BUY, mEntity);
                    }
                }
            }
        });

        if (p < buySellList.size()) {
            final TblZoneRequestOfferEntity offerEntity = buySellList.get(p);
            tv_buysell_price.setText(DispUtility.price2Disp(offerEntity.getProductPrice(), mEntity.getPriceUnit(), mEntity.getNumberUnit()));
            tv_num.setText(DispUtility.productNum2Disp(offerEntity.getRemainNumber(), null, mEntity.getNumberUnit()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PowerHelper.i().hasPower(activity)) {
                        if (SptConstant.BUYSELL_SELL.equals(offerEntity.getBuySell())) {
                            activity.switchBSF(offerEntity, mEntity, "卖方订单详情");
                        } else {
                            activity.switchBBF(offerEntity, mEntity, "买方订单详情");
                        }
                    }
                }
            });
        }

        if (isBuy) {
            ll_buy.addView(view);
        } else {
            ll_sell.addView(view);
        }

    }

    private void setHead() {
        //订单号
        tv_order_num.setText(ContractHelper.instance.getOrderId(mEntity.getZoneOrderNoDto()));
//        交货地
        String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, mEntity.getDeliveryPlace());
        if (mEntity.getProductType().startsWith("SL") && TextUtils.isEmpty(deliveryPlace)) {
            deliveryPlace = mEntity.getDeliveryPlaceName();
        }
        tv_deliveryplace.setText(deliveryPlace);

        // 质量标准字段(塑料显示牌号，钢铁显示铁品位)
        if (mEntity.getProductType().startsWith("GT")) {
            String qualityEx1 = new DecimalFormat("#0.#######").format(mEntity.getProductQualityEx1());
            tv_product_quality.setText(qualityEx1);
        } else if (mEntity.getProductType().startsWith("SL")) {
            tv_product_quality.setText(mEntity.getBrand());
        } else {
            tv_product_quality.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY, mEntity.getProductQuality()));
        }

        //定金
        tv_bond.setText(DictionaryUtility.getValue(SptConstant.SPTDICT_BOND, mEntity.getBond()));

        //价格
        if (mEntity.getDealPrice() == null) {
            tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
            tv_price.setText("暂无");
            tv_price.setCompoundDrawables(null, null, null, null);
        } else {
            String price = DispUtility.price2Disp(mEntity.getDealPrice(), mEntity.getPriceUnit(), mEntity.getNumberUnit());
            if (mEntity.getIncAmount() == null || mEntity.getIncAmount().compareTo(BigDecimal.ZERO) == 0) {
                tv_price.setText(String.format("%s-", price));
                tv_price.setTextColor(activity.getResources().getColor(R.color.ltBlack));
                tv_price.setCompoundDrawables(null, null, null, null);
            } else {
                tv_price.setText(price);
                Drawable drawable;
                if (mEntity.getIncAmount().compareTo(BigDecimal.ZERO) > 0) {
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

    private void parseIntent() {
        String entity = activity.getIntent().getStringExtra(ZoneRequestDownEntity.class.getName());
        Type type = new TypeToken<ZoneRequestDownEntity>() {
        }.getType();
        mEntity = JsonUtility.toJavaObject(entity, type);
    }

    @Override
    public void onResume() {
        super.onResume();
        NotifyUtility.cancelJpush(activity);
        ll_buy.removeAllViews();
        ll_sell.removeAllViews();
        refresh();
    }

    private void refresh() {
        SubAsyncTask.create().setOnDataListener("findZoneRequestList", new OnDataListener<ZoneRequestDownEntity>() {
            @Override
            public ZoneRequestDownEntity requestService() throws DBException, ApplicationException {
                return Client.getService(IZoneRequestService.class).getZoneRequestDetailByRequestId(mEntity.getRequestId());
            }

            @Override
            public void onDataSuccessfully(ZoneRequestDownEntity entity) {
                if (entity != null) {
                    mEntity = entity;
                    initData();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_join_sell:
                if (!PowerHelper.i().hasPower(activity)) return;
                activity.switchJSF(SptConstant.BUYSELL_SELL, mEntity);
                break;
            case R.id.tv_join_buy:
                if (!PowerHelper.i().hasPower(activity)) return;
                activity.switchJSF(SptConstant.BUYSELL_BUY, mEntity);
                break;
            case R.id.tv_release_time:
                v.setSelected(!v.isSelected());
                setHeight(fl_index_chart.getMeasuredHeight());
                if (v.isSelected()) {
                    viewToRotate(0f, 180f);
                    showHiden(height, 0);
                } else {
                    viewToRotate(180f, 0f);
                    showHiden(0, height);
                }
                break;

            default:
                break;
        }
    }

    private void viewToRotate(float start, float end) {
        RotateAnimation rotateAnimation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        rootView.findViewById(R.id.arrow).startAnimation(rotateAnimation);
    }

    public void setHeight(int h) {
        if (height == 0 && h != 0) {
            height = h;
        }
    }

    private void showHiden(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                LogUtils.i(OrderDetailFragment.class.getSimpleName(), String.valueOf(value));
                ViewGroup.LayoutParams layoutParams = fl_index_chart.getLayoutParams();
                layoutParams.height = value;
                fl_index_chart.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(200).start();
    }

}
