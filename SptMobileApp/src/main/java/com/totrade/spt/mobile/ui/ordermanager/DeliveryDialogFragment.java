package com.totrade.spt.mobile.ui.ordermanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.entity.ZoneContractTransferUpEntity;
import com.autrade.spt.deal.service.inf.IZoneContractService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SpanStrUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.FormItemInputView;
import com.totrade.spt.mobile.view.customize.SuperTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 宣布交收弹出碎片
 * Created by Timothy on 2017/5/17.
 */

public class DeliveryDialogFragment extends DialogFragment {

    private View rootView;
    private ComTitleBar title;
    private TextView tv_summit;
    private SuperTextView stv_ready_date;
    private FormItemInputView fiv_delivery_warehouse;
    private TextView tv_introduction;
    private static ContractDownEntity mEntity;

    public static DeliveryDialogFragment newInstance(ContractDownEntity entity) {
        DeliveryDialogFragment fragment = new DeliveryDialogFragment();
        mEntity = entity;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_delivery_dialog, null);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimRight);
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        title = (ComTitleBar) rootView.findViewById(R.id.title);
        tv_summit = (TextView) rootView.findViewById(R.id.tv_summit);
        tv_introduction = (TextView) rootView.findViewById(R.id.tv_introduction);
        stv_ready_date = (SuperTextView) rootView.findViewById(R.id.stv_ready_date);
        fiv_delivery_warehouse = (FormItemInputView) rootView.findViewById(R.id.fiv_delivery_warehouse);

        tv_introduction.setText(SpanStrUtils.createBuilder("请知悉：\n")
                .setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("您已同意").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("《贸易专区规则》").setForegroundColor(getResources().getColor(R.color.blue_txt_3de)).setUnderline()
                .append("及").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("《委托代办货权转移协议》").setForegroundColor(getResources().getColor(R.color.blue_txt_3de)).setUnderline()
                .append("请务必将货权转移至“佰所仟讯(上海)电子商务有限公司” ,委托其 代办货权转移事项。").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .create());
        fiv_delivery_warehouse.setHint("请输入仓库名");
        fiv_delivery_warehouse.setName("交货仓库");

        stv_ready_date.setOnClickListener(listener);
        tv_summit.setOnClickListener(listener);
        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

    private void initOptionDeliveryTime() {
        List<String> dateList = new ArrayList<>();
        if (null != mEntity && !StringUtility.isNullOrEmpty(mEntity.getDeliveryTimeStr())) {
            if (mEntity.getDeliveryTimeStr().endsWith("上")) {

            } else if (mEntity.getDeliveryTimeStr().endsWith("中")) {

            } else if (mEntity.getDeliveryTimeStr().endsWith("下")) {

            }
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_summit) {
                zoneContractDelivery();
            } else if (view.getId() == R.id.stv_ready_date) {
                initOptionDeliveryTime();
            }
        }
    };

    private void zoneContractDelivery() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneContractTransferUpEntity upEntity = new ZoneContractTransferUpEntity();
                upEntity.setContractId(mEntity.getContractId());
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                Client.getService(IZoneContractService.class).zoneContractDelivery(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != deliveryResult) {
                    deliveryResult.onResult(null != obj && obj);
                    dismiss();
                }
            }
        });
    }

    private IDeliveryResult deliveryResult;

    public void setDeliveryResult(IDeliveryResult deliveryResult) {
        this.deliveryResult = deliveryResult;
    }

    public interface IDeliveryResult {
        void onResult(boolean result);
    }
}
