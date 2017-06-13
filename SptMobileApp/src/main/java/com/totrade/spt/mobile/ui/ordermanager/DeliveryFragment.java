package com.totrade.spt.mobile.ui.ordermanager;

import android.content.Intent;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.entity.ZoneContractTransferUpEntity;
import com.autrade.spt.deal.service.inf.IZoneContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SpanStrUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.CalendarActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.FormItemInputView;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * 宣布交收
 * Created by Timothy on 2017/5/22.
 */

public class DeliveryFragment extends BaseSptFragment<OrderDetailActivity> {

    private ComTitleBar title;
    private TextView tv_summit;
    private SuperTextView stv_ready_date;
    private FormItemInputView fiv_delivery_warehouse;
    private TextView tv_introduction;

    private final static int REQUEST_CODE_DATE = 0x0010;
    private final static String DATE_FORMAT = "yyyy/MM/dd";

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_delivery_dialog;
    }

    @Override
    protected void initView() {
        title = findView(R.id.title);
        tv_summit = findView(R.id.tv_summit);
        tv_introduction = findView(R.id.tv_introduction);
        stv_ready_date = findView(R.id.stv_ready_date);
        fiv_delivery_warehouse = findView(R.id.fiv_delivery_warehouse);

        tv_introduction.setMovementMethod(LinkMovementMethod.getInstance());
        tv_introduction.setText(SpanStrUtils.createBuilder("请知悉：\n")
                .setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("您已同意").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("《贸易专区规则》").setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("titleString", "贸易专区规则");
                        intent.putExtra("urlString", Client.getRuleUrl());
                        startActivity(intent);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {}
                }).setForegroundColor(getResources().getColor(R.color.blue)).setUnderline()
                .append("及").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("《委托代办货权转移协议》").setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("titleString", "委托代办货权转移协议");
                        intent.putExtra("urlString", Client.getRuleUrl2());
                        startActivity(intent);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {}
                }).setForegroundColor(getResources().getColor(R.color.blue)).setUnderline()
                .append("请务必将货权转移至“佰所仟讯(上海)电子商务有限公司” ,委托其代办货权转移事项。").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .create());
        fiv_delivery_warehouse.setHint("请输入仓库名");
        fiv_delivery_warehouse.setName("交货仓库");
        stv_ready_date.setRightString(DateUtility.formatToStr(new Date(), "yyyy/MM/dd"));

        stv_ready_date.setOnClickListener(listener);
        tv_summit.setOnClickListener(listener);
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_DATE) {
            String startTime = data.getStringExtra("dateselect");
            if (!TextUtils.isEmpty(startTime)) {
                Calendar c = FormatUtil.string2Calendar(startTime, DATE_FORMAT);
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.SECOND, -1);
                Date date = c.getTime();
                if (date.getTime() - new Date().getTime() < 0) {
                    ToastHelper.showMessage("交货期不得小于当前时间");
                } else {
                    stv_ready_date.setRightString(FormatUtil.date2String(date, DATE_FORMAT));
                }
            }
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_summit) {
                if (checkData()) zoneContractDelivery();
            } else if (view.getId() == R.id.stv_ready_date) {
                Intent intent = new Intent(mActivity, CalendarActivity.class);
                if (StringUtility.isNullOrEmpty(stv_ready_date.getRightString())) {
                    intent.putExtra("datastr", DateUtility.formatToStr(new Date(), DATE_FORMAT));
                } else {
                    intent.putExtra("datastr", stv_ready_date.getRightString());
                }
                intent.putExtra("dateformat", DATE_FORMAT);
                startActivityForResult(intent, REQUEST_CODE_DATE);
            }
        }

    };

    private boolean checkData() {
        if (StringUtility.isNullOrEmpty(stv_ready_date.getRightString())) {
            ToastHelper.showMessage("备货期不可为空");
            return false;
        }
        if (StringUtility.isNullOrEmpty(fiv_delivery_warehouse.getValue())) {
            ToastHelper.showMessage("交货仓库不可为空");
            return false;
        }
        return true;
    }

    private void zoneContractDelivery() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneContractTransferUpEntity upEntity = new ZoneContractTransferUpEntity();
                if (!StringUtility.isNullOrEmpty(mActivity.action) && mActivity.action.equals(DealConstant.ORDER_STATUS_DECLAREDELIVERY)) {
                    upEntity.setContractId(OrderHelper.getInstance().contractDownEntity.getContractId());
                } else {
                    upEntity.setContractId(OrderHelper.getInstance().entity.getContractId());
                }
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setDeliveryRealDate(DateUtility.parseToDate(stv_ready_date.getRightString(), DATE_FORMAT));
                upEntity.setWareHouseName(fiv_delivery_warehouse.getValue());
                Client.getService(IZoneContractService.class).zoneContractDelivery(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("操作成功");
                    toActivity();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toActivity() {
        if (!StringUtility.isNullOrEmpty(mActivity.action) && mActivity.action.equals(DealConstant.ORDER_STATUS_DECLAREDELIVERY)) {
            mActivity.finish();
        } else {
            mActivity.popBack();
        }
    }
}
