package com.totrade.spt.mobile.ui.ordermanager;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.deal.entity.PayAllRemainPreviewDownEntity;
import com.autrade.spt.deal.service.inf.IContractBondService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DecimalUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.SuperTextView;
import com.totrade.spt.mobile.view.customize.countdown.TimerUtils;

/**
 * Created by Timothy on 2017/4/11.
 */

public class PayDetailFragment extends DialogFragment {

    private View rootView;
    private ImageView iv_close;
    private SuperTextView stv_total_loan;
    private SuperTextView stv_payment_paid;
    private SuperTextView stv_obligation;
    private SuperTextView stv_need_pay;
    private TextView tv_pay;

    PayAllRemainPreviewDownEntity entity;

    public static PayDetailFragment newInstance(PayAllRemainPreviewDownEntity entity) {
        PayDetailFragment payDetailFragment = new PayDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PayAllRemainPreviewDownEntity.class.getName(), entity.toString());
        payDetailFragment.setArguments(bundle);
        return payDetailFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pay_detail, null);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
        stv_total_loan = (SuperTextView) rootView.findViewById(R.id.stv_total_loan);
        stv_payment_paid = (SuperTextView) rootView.findViewById(R.id.stv_payment_paid);
        stv_obligation = (SuperTextView) rootView.findViewById(R.id.stv_obligation);
        stv_need_pay = (SuperTextView) rootView.findViewById(R.id.stv_need_pay);
        tv_pay = (TextView) rootView.findViewById(R.id.tv_pay);

        entity = JsonUtility.toJavaObject(getArguments().getString(PayAllRemainPreviewDownEntity.class.getName()), PayAllRemainPreviewDownEntity.class);
        LinearLayout ll_count_time = (LinearLayout) rootView.findViewById(R.id.ll_count_time);
        TextView tvCountDown = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, getActivity(), entity.getRemainTimeMillis(), TimerUtils.TIME_STYLE_THREE, 0)
                .getmDateTv();
        tvCountDown.setTextColor(getResources().getColor(R.color.red_txt_d56));
        tvCountDown.setTextSize(11);
        ll_count_time.addView(tvCountDown);

        iv_close.setOnClickListener(listener);
        tv_pay.setOnClickListener(listener);

        if (entity != null) {
            stv_total_loan.setRightString(DecimalUtils.keep2PointStringToCurrency(entity.getPriceTotal()));
            stv_payment_paid.setRightString(DecimalUtils.keep2PointStringToCurrency(entity.getPaidBond()));
            stv_obligation.setRightString(DecimalUtils.keep2PointStringToCurrency(entity.getRemainAmount()));
            stv_need_pay.setRightString(DecimalUtils.keep2PointStringToCurrency(entity.getRemainAmount()));
            TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, getActivity(), entity.getRemainTimeMillis(), TimerUtils.TIME_STYLE_THREE, 0)
                    .getmDateTv();
        }
        return dialog;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == iv_close.getId()) {
                dismiss();
            } else if (view.getId() == tv_pay.getId()) {
                pay();
            }
        }
    };

    private void pay() {
        SubAsyncTask.create().setOnDataListener(getActivity(), false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IContractBondService.class).payAllRemain(LoginUserContext.getLoginUserId(), entity.getContractId(), false); // 确认付款;
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (paySucessCallBack != null) {
                    if (null != obj) {
                        paySucessCallBack.onPaySucess(obj);
                    } else {
                        //todo 此处不做支付失败的处理，因为后台服务已用异常处理
                    }
                }
            }
        });
    }

    public PaySucessCallBack paySucessCallBack;

    public interface PaySucessCallBack {
        void onPaySucess(boolean isSucess);
    }

    public void setPaySucessCallBack(PaySucessCallBack callBack) {
        this.paySucessCallBack = callBack;
    }
}
