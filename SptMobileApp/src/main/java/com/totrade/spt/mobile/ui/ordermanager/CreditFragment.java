package com.totrade.spt.mobile.ui.ordermanager;

import android.content.Intent;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.autrade.spt.deal.constants.DealConstant;
import com.autrade.spt.deal.dto.OrderFlowUpEntity;
import com.autrade.spt.deal.service.inf.IContractBondService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.adapter.ZonePostImgsAdapte;
import com.totrade.spt.mobile.base.BaseCameraFragment;
import com.totrade.spt.mobile.bean.Attatch;
import com.totrade.spt.mobile.bean.PostImageBean;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.KeyBoardUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SpanStrUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.WebViewActivity;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传贷权凭证(确定发货)
 * Created by Timothy on 2017/5/18.
 */

public class CreditFragment extends BaseCameraFragment<OrderDetailActivity> implements AdapterView.OnItemClickListener {

    private List<PostImageBean> mLicences;
    private ZonePostImgsAdapte mAdapter;
    private int mClickPosition;//记录点击的位置
    private ComTitleBar title;
    private TextView tv_introduction;
    private TextView tv_post;
    private CustomGridView cgv_zone_post_imgs;
    private OrderFlowUpEntity upEntity;
    private Button tv_summit;

    @Override
    public void getCameraData(Attatch t) {
        if (null != t) {
            mLicences.get(mClickPosition).setServerId(t.getAttatchId());
            mLicences.get(mClickPosition).setPath(t.getFilePath());
            mLicences.get(mClickPosition).setHint("已上传");
            if (mClickPosition == 0)
                upEntity.setOwnerFileId(String.valueOf(t.getAttatchId()));
            mAdapter.refreshData(mLicences);
        }
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_credit;
    }

    @Override
    protected void initView() {
        upEntity = new OrderFlowUpEntity();
        title = findView(R.id.title);
        tv_introduction = findView(R.id.tv_introduction);
        cgv_zone_post_imgs = findView(R.id.cgv_zone_post_imgs);
        tv_post = findView(R.id.tv_post);
        tv_summit = findView(R.id.tv_summit);
        tv_introduction.setMovementMethod(LinkMovementMethod.getInstance());
        tv_introduction.setText(SpanStrUtils.createBuilder("请上传对应合同的货权转移凭证 \n")
                .setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .append("注：您已同意").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
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
                .append("故该货权必须先转移至“佰所仟讯(上海)电子商务有限公司”，委托 其代办货权转移事项").setForegroundColor(getResources().getColor(R.color.black_txt_1d))
                .create());
        tv_post.setText("上传货权凭证");
        cgv_zone_post_imgs.setOnItemClickListener(this);

        tv_summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData())
                    confirmDelivery();
            }
        });

        title.setLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity();
            }
        });
        setImgGrid();
    }

    private boolean checkData() {
        if (ObjUtils.isEmpty(upEntity.getOwnerFileId())) {
            ToastHelper.showMessage("请上传贷权凭证");
            return false;
        }
        return true;
    }

    /**
     * 证件上传区
     */
    private void setImgGrid() {
        mLicences = new ArrayList<>();
        PostImageBean bean;
        String[] licence = new String[]{"上传货权凭证"};
        for (int i = 0; i < 1; i++) {
            bean = new PostImageBean(licence[i], null, 0L);
            mLicences.add(bean);
        }

        mAdapter = new ZonePostImgsAdapte(mActivity);
        cgv_zone_post_imgs.setAdapter(mAdapter);
        cgv_zone_post_imgs.setOnItemClickListener(this);
        mAdapter.refreshData(mLicences);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickPosition = position;
        KeyBoardUtils.hideSoftInput(mActivity);
        showCameraPopWindow(true, false);
    }

    private void confirmDelivery() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                if (!StringUtility.isNullOrEmpty(mActivity.action) && mActivity.action.equals(DealConstant.ORDER_STATUS_WAIT_DELIVERY)) {
                    upEntity.setContractId(OrderHelper.getInstance().contractDownEntity.getContractId());
                } else {
                    upEntity.setContractId(OrderHelper.getInstance().entity.getContractId());
                }
                upEntity.setUserId(LoginUserContext.getLoginUserDto().getUserId());
                Client.getService(IContractBondService.class).confirmDelivery(upEntity);
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
        if (!StringUtility.isNullOrEmpty(mActivity.action) && mActivity.action.equals(DealConstant.ORDER_STATUS_WAIT_DELIVERY)) {
            mActivity.finish();
        } else {
            mActivity.popBack();
        }
    }
}
