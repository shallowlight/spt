package com.totrade.spt.mobile.ui.mainuser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.autrade.spt.zone.entity.TblZoneApplyEntity;
import com.autrade.spt.zone.service.inf.IZoneApplyService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.adapter.ZonePostImgsAdapte;
import com.totrade.spt.mobile.base.BaseCameraActivity;
import com.totrade.spt.mobile.bean.Attatch;
import com.totrade.spt.mobile.bean.PostImageBean;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.maintrade.listener.IFormNameValue;
import com.totrade.spt.mobile.utility.KeyBoardUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomGridView;
import com.totrade.spt.mobile.view.customize.SimpleDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 行业申请页面
 * Created by Timothy on 2017/4/24.
 */

public class IndustryRequestActivity extends BaseCameraActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ComTitleBar title;
    private LinearLayout lLayout_form_company;
    private LinearLayout lLayout_form_product;
    private LinearLayout lLayout_form_deal;
    private CustomGridView cgv_zone_post_imgs;
    //    private CheckBox cb_agree;
    private Button btn_apply;
    private Button btn_cancel;

    private List<PostImageBean> mLicences;
    private ZonePostImgsAdapte mAdapter;

    private TblZoneApplyEntity upEntity = new TblZoneApplyEntity();

    private int mClickPosition;//记录点击的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_request);
        initView();
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickPosition = position;
        KeyBoardUtils.hideSoftInput(this);
        showCameraPopWindow(false, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply:
                if (checkForm()) submitZoneApply();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Override
    public void getCameraData(Attatch t) {
        if (null != t) {
            mLicences.get(mClickPosition).setServerId(t.getAttatchId());
            mLicences.get(mClickPosition).setPath(t.getFilePath());
            mLicences.get(mClickPosition).setHint("已上传");
            if (mClickPosition == 0)
                upEntity.setCompanyLicensefileId(t.getAttatchId());
            else if (mClickPosition == 1)
                upEntity.setOriginFileid(t.getAttatchId());
            else if (mClickPosition == 2)
                upEntity.setLicenseFileid(t.getAttatchId());
            else if (mClickPosition == 3)
                upEntity.setOtherFileId(t.getAttatchId());
            mAdapter.refreshData(mLicences);
        }
    }

    private void initView() {
        title = (ComTitleBar) findViewById(R.id.title);
        lLayout_form_company = (LinearLayout) findViewById(R.id.lLayout_form_company);
        lLayout_form_product = (LinearLayout) findViewById(R.id.lLayout_form_product);
        lLayout_form_deal = (LinearLayout) findViewById(R.id.lLayout_form_deal);
        cgv_zone_post_imgs = (CustomGridView) findViewById(R.id.cgv_zone_post_imgs);
//        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
        btn_apply = (Button) findViewById(R.id.btn_apply);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_apply.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initData() {
        String[] comNames = getResources().getStringArray(R.array.industry_requst_company_info);
        for (int i = 0; i < comNames.length; i++) {
            ((IFormNameValue) lLayout_form_company.getChildAt(i)).setName(comNames[i]);
        }

        String[] productNames = getResources().getStringArray(R.array.industry_requst_product_info);
        for (int i = 0; i < productNames.length; i++) {
            ((IFormNameValue) lLayout_form_product.getChildAt(i)).setName(productNames[i]);
        }

        String[] dealNames = getResources().getStringArray(R.array.industry_requst_deal_info);
        for (int i = 0; i < dealNames.length; i++) {
            ((IFormNameValue) lLayout_form_deal.getChildAt(i)).setName(dealNames[i]);
        }

        setHints();

        mLicences = new ArrayList<>();
        PostImageBean bean;
//        String[] licence = new String[]{"请上传商品质检单", "请上传商品原产地证明", "请上传商品生产许可证", "请上传商品其它证明文件"};
        String[] licence = new String[]{"企业三证"};
        for (int i = 0; i < 1; i++) {
            bean = new PostImageBean(licence[i], null, 0L);
            mLicences.add(bean);
        }

        mAdapter = new ZonePostImgsAdapte(this);
        cgv_zone_post_imgs.setAdapter(mAdapter);
        mAdapter.refreshData(mLicences);

        cgv_zone_post_imgs.setOnItemClickListener(this);
    }

    private void setHints() {
        ((IFormNameValue) lLayout_form_company.getChildAt(0)).setHint("请填写(必填)");
        ((IFormNameValue) lLayout_form_company.getChildAt(1)).setHint("请填写(选填)");
        ((IFormNameValue) lLayout_form_company.getChildAt(2)).setHint("请填写(选填)");
        ((IFormNameValue) lLayout_form_company.getChildAt(3)).setHint("请填写(选填)");

        ((IFormNameValue) lLayout_form_product.getChildAt(0)).setHint("如:有色金属(必填)");
        ((IFormNameValue) lLayout_form_product.getChildAt(1)).setHint("如:苯乙稀(选填)");
        ((IFormNameValue) lLayout_form_product.getChildAt(2)).setHint("请输入质量标准，可多填(选填)");
        ((IFormNameValue) lLayout_form_product.getChildAt(3)).setHint("请输入原产地，可多填(选填)");
        ((IFormNameValue) lLayout_form_product.getChildAt(4)).setHint("请输入交货地，可多填(选填)");
        ((IFormNameValue) lLayout_form_product.getChildAt(5)).setHint("如:内贸/外贸(选填)");

        ((IFormNameValue) lLayout_form_deal.getChildAt(0)).setHint("请输入(选填)");
        ((IFormNameValue) lLayout_form_deal.getChildAt(1)).setHint("人民币/美金(选填)");
        ((IFormNameValue) lLayout_form_deal.getChildAt(2)).setHint("请输入定金比例标准(选填)");
    }

    /**
     * 校验数据
     *
     * @return
     */
    private boolean checkForm() {
        if (!checkEmptyIntput(lLayout_form_company, lLayout_form_product, lLayout_form_deal))
            return false;

        if (upEntity.getCompanyLicensefileId() == null || upEntity.getCompanyLicensefileId() == 0l) {
            Toast.makeText(this, "请上传所需证件", Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (!cb_agree.isChecked()) {
//            ToastHelper.showMessage("请勾选同意并阅读申请书");
//            return false;
//        }

        upEntity.setCompanyName(((IFormNameValue) lLayout_form_company.getChildAt(0)).getValue());//企业名称
        upEntity.setCompanyArea(((IFormNameValue) lLayout_form_company.getChildAt(1)).getValue());//企业产品销售区域
        upEntity.setCompanyPrevOutput(((IFormNameValue) lLayout_form_company.getChildAt(2)).getValue());//企业上一年产值
        upEntity.setCompanyPrevProfit(((IFormNameValue) lLayout_form_company.getChildAt(3)).getValue());//企业上一年净利润
        upEntity.setIndustry(((IFormNameValue) lLayout_form_product.getChildAt(0)).getValue());//行业
        upEntity.setProductType(((IFormNameValue) lLayout_form_product.getChildAt(1)).getValue());//品名
        upEntity.setProductQuality(((IFormNameValue) lLayout_form_product.getChildAt(2)).getValue());//质量标准
        upEntity.setProductPlace(((IFormNameValue) lLayout_form_product.getChildAt(3)).getValue());//原产地
        upEntity.setDeliveryPlace(((IFormNameValue) lLayout_form_product.getChildAt(4)).getValue());//交货地
        upEntity.setTradeMode(((IFormNameValue) lLayout_form_product.getChildAt(5)).getValue());//贸易方式
//        upEntity.setCompanyPosition(((IFormNameValue) lLayout_form_product.getChildAt(6)).getValue());//行业地位
        upEntity.setNumberUnit(((IFormNameValue) lLayout_form_deal.getChildAt(0)).getValue());//数据单位于包装
        upEntity.setPriceUnit(((IFormNameValue) lLayout_form_deal.getChildAt(1)).getValue());//计价单位
        upEntity.setBond(((IFormNameValue) lLayout_form_deal.getChildAt(2)).getValue());//定金比例
        upEntity.setSubmitUserId(LoginUserContext.getLoginUserId());
        upEntity.setApplyType("Z");

        return true;
    }

    private boolean checkEmptyIntput(LinearLayout... lls) {
        for (LinearLayout ll : lls) {
            for (int i = 0; i < ll.getChildCount(); i++) {
                boolean if_required = ((IFormNameValue) ll.getChildAt(i)).getIfRequired();
                String name = ((IFormNameValue) ll.getChildAt(i)).getName();
                String item = ((IFormNameValue) ll.getChildAt(i)).getValue();
                if (StringUtility.isNullOrEmpty(item.trim()) && if_required) {
                    ToastHelper.showMessage(name + "不可为空");
                    return false;
                }
            }
        }
        return true;
    }

    private void submitZoneApply() {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener() {
            @Override
            public Object requestService() throws DBException, ApplicationException {
                Client.getService(IZoneApplyService.class).submitZoneApply(upEntity);
                return "";
            }

            @Override
            public void onDataSuccessfully(Object obj) {
                if (null != obj) {
                    showNotice();
//                    Toast.makeText(getApplicationContext(), "申请提交成功", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void showNotice() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout2, null);
        final SimpleDialog dialog = new SimpleDialog(this, R.style.simpledialog, view);
        dialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                IndustryRequestActivity.this.finish();
            }
        });
        dialog.show();
    }

}
