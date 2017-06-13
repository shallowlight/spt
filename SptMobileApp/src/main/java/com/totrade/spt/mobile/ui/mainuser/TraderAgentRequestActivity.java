package com.totrade.spt.mobile.ui.mainuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.autrade.spt.common.entity.FormConfigEntity;
import com.autrade.spt.common.entity.TblSptDictionaryEntity;
import com.autrade.spt.master.entity.TblCompanyMasterEntity;
import com.autrade.spt.nego.constants.NegoConstant;
import com.autrade.spt.nego.entity.FormConfigDiffUpEntity;
import com.autrade.spt.nego.entity.TblScfApplyDataEntity;
import com.autrade.spt.nego.entity.TblScfSettingEntity;
import com.autrade.spt.nego.service.inf.IRequestFormConfigService;
import com.autrade.spt.nego.service.inf.IScfApplyDataService;
import com.autrade.spt.nego.service.inf.IScfSettingService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.autrade.stage.utility.DateUtility;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.adapter.ZonePostImgsAdapte;
import com.totrade.spt.mobile.base.BaseBottomFlowFragment;
import com.totrade.spt.mobile.base.BaseCameraActivity;
import com.totrade.spt.mobile.bean.Attatch;
import com.totrade.spt.mobile.bean.PostImageBean;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.SearchActivity;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.KeyBoardUtils;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.StringUtils;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.CalendarActivity;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.ComTitleBar;
import com.totrade.spt.mobile.view.customize.CustomGridView;
import com.totrade.spt.mobile.view.customize.FormItemInputView;
import com.totrade.spt.mobile.ui.maintrade.listener.IFormNameValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 贸易代理申请
 * Created by Timothy on 2017/4/24.
 */

public class TraderAgentRequestActivity extends BaseCameraActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ComTitleBar title;
    private LinearLayout lLayout_form_company;
    private LinearLayout lLayout_form_product;
    private LinearLayout lLayout_form_deal;
    private CustomGridView cgv_zone_post_imgs;
    private Button btn_apply;
    private Button btn_cancel;

    private List<PostImageBean> mLicences;
    private ZonePostImgsAdapte mAdapter;

    private int mClickPosition;//记录点击的位置

    private TblScfApplyDataEntity upEntity = new TblScfApplyDataEntity();

    private TblCompanyMasterEntity buyCompany;

    private String curProductType;
    private int curProductTypePosition = 0;

    private String curDeliveryMode;
    private int curDeliveryModePosition = 0;

    private String curProductQulity;
    private int curProductQulityPosition = 0;
    private TblScfSettingEntity settingEntity;

    private FormConfigEntity curProductInfo;
    //交易区
    private final static int REQUEST_CODE_DATE = 0x0010;
    private final static int REQUEST_CODE_COMPANY = 0x0009;
    private final static String DATE_FORMAT = "yyyy/MM/dd";
    private TextView tv_preView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_agent_request);
        initView();
        initData();
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
                    ((IFormNameValue) lLayout_form_deal.getChildAt(0)).setValue(FormatUtil.date2String(date, DATE_FORMAT));
                }
            }
        }
        if (requestCode == REQUEST_CODE_COMPANY) {
            buyCompany = JSON.parseObject(data.getStringExtra("company"), TblCompanyMasterEntity.class);
            ((IFormNameValue) lLayout_form_company.getChildAt(0)).setValue(buyCompany.getCompanyName());
        }
    }


    @Override
    public void getCameraData(Attatch t) {
        if (null != t) {
            mLicences.get(mClickPosition).setServerId(t.getAttatchId());
            mLicences.get(mClickPosition).setPath(t.getFilePath());
            mLicences.get(mClickPosition).setHint("已上传");
            if (mClickPosition == 0)
                upEntity.setLicenseId(String.valueOf(t.getAttatchId()));
            mAdapter.refreshData(mLicences);
        }
    }

    private void initView() {
        title = (ComTitleBar) findViewById(R.id.title);
        tv_preView = (TextView) findViewById(R.id.tv_preView);
        lLayout_form_company = (LinearLayout) findViewById(R.id.lLayout_form_company);
        lLayout_form_product = (LinearLayout) findViewById(R.id.lLayout_form_product);
        lLayout_form_deal = (LinearLayout) findViewById(R.id.lLayout_form_deal);
        cgv_zone_post_imgs = (CustomGridView) findViewById(R.id.cgv_zone_post_imgs);
        btn_apply = (Button) findViewById(R.id.btn_apply);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        tv_preView.setOnClickListener(this);
    }

    private void initData() {
        setFormName();
        setImgGrid();
        setListener();
    }

    private void setListener() {
        //采购方
        lLayout_form_company.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraderAgentRequestActivity.this, SearchActivity.class);
                startActivityForResult(intent, 0x0009);
            }
        });
        //品名
        lLayout_form_product.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllSetting();
            }
        });

        //质量标准
        lLayout_form_product.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(curProductType)) {
                    getFormConfigDiff(1);
                } else {
                    Toast.makeText(TraderAgentRequestActivity.this, "请先选择品名", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //交收期
        lLayout_form_deal.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraderAgentRequestActivity.this, CalendarActivity.class);
                if (StringUtility.isNullOrEmpty(((IFormNameValue) lLayout_form_deal.getChildAt(0)).getValue())) {
                    intent.putExtra("datastr", DateUtility.formatToStr(new Date(), DATE_FORMAT));
                } else {
                    intent.putExtra("datastr", ((IFormNameValue) lLayout_form_deal.getChildAt(0)).getValue());
                }
                intent.putExtra("dateformat", DATE_FORMAT);
                startActivityForResult(intent, REQUEST_CODE_DATE);
            }
        });

        //交货方式
        lLayout_form_deal.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(curProductType)) {
                    getFormConfigDiff(3);
                } else {
                    Toast.makeText(TraderAgentRequestActivity.this, "请先选择品名", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_apply.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
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

        mAdapter = new ZonePostImgsAdapte(this);
        cgv_zone_post_imgs.setAdapter(mAdapter);
        cgv_zone_post_imgs.setOnItemClickListener(this);
        mAdapter.refreshData(mLicences);
    }

    /**
     * 给表单设置名称
     */
    private void setFormName() {
        String[] comNames = getResources().getStringArray(R.array.trade_agent_requst_company_info);
        for (int i = 0; i < comNames.length; i++) {
            ((IFormNameValue) lLayout_form_company.getChildAt(i)).setName(comNames[i]);
        }

        String[] productNames = getResources().getStringArray(R.array.trade_agent_requst_product_info);
        for (int i = 0; i < productNames.length; i++) {
            ((IFormNameValue) lLayout_form_product.getChildAt(i)).setName(productNames[i]);
        }

        String[] dealNames = getResources().getStringArray(R.array.trade_agent_requst_deal_info);
        for (int i = 0; i < dealNames.length; i++) {
            ((IFormNameValue) lLayout_form_deal.getChildAt(i)).setName(dealNames[i]);
        }
        ((FormItemInputView) lLayout_form_deal.getChildAt(2)).getRightTextView().setText("吨");
        ((FormItemInputView) lLayout_form_deal.getChildAt(3)).getRightTextView().setText("元");
        ((FormItemInputView) lLayout_form_deal.getChildAt(4)).getRightTextView().setText("元");

        ((FormItemInputView) lLayout_form_deal.getChildAt(2)).setFormInputTextWatcherListener(textWatcher);
        ((FormItemInputView) lLayout_form_deal.getChildAt(3)).setFormInputTextWatcherListener(textWatcher);

        ((FormItemInputView) lLayout_form_deal.getChildAt(2)).getValueView().setInputType(InputType.TYPE_CLASS_NUMBER);
        ((FormItemInputView) lLayout_form_deal.getChildAt(3)).getValueView().setInputType(InputType.TYPE_CLASS_NUMBER);
        ((FormItemInputView) lLayout_form_deal.getChildAt(4)).getValueView().setInputType(InputType.TYPE_CLASS_NUMBER);

        setHints();
    }

    private void setHints() {
        ((FormItemInputView) lLayout_form_deal.getChildAt(2)).setHint("请输入数量");
        ((FormItemInputView) lLayout_form_deal.getChildAt(3)).setHint("请输入单价");
    }

    private FormItemInputView.FormInputTextWatcherListener textWatcher = new FormItemInputView.FormInputTextWatcherListener() {
        @Override
        public void afterTextChange(Editable editable) {
            BigDecimal price = null;
            BigDecimal num = null;
            try {
                if (!StringUtility.isNullOrEmpty(((FormItemInputView) lLayout_form_deal.getChildAt(3)).getValue())) {
                    num = new BigDecimal(((FormItemInputView) lLayout_form_deal.getChildAt(3)).getValue());
                }
                if (!StringUtility.isNullOrEmpty(((FormItemInputView) lLayout_form_deal.getChildAt(2)).getValue())) {
                    price = new BigDecimal(((FormItemInputView) lLayout_form_deal.getChildAt(2)).getValue());
                }
                if (null != num && null != price) {
                    ((FormItemInputView) lLayout_form_deal.getChildAt(4)).getValueView().setText(num.multiply(price).toPlainString());
                }
            } catch (Exception e) {
                LogUtils.e(this.getClass().getSimpleName(), "数据转换异常");
            }
        }
    };

    /**
     * 获取可选的品名
     */
    private void findAllSetting() {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<List<TblScfSettingEntity>>() {
            @Override
            public List<TblScfSettingEntity> requestService() throws DBException, ApplicationException {
                return Client.getService(IScfSettingService.class).findAllSetting();
            }

            @Override
            public void onDataSuccessfully(List<TblScfSettingEntity> obj) {
                if (obj != null) {
                    showProductNamePop(obj);
                }
            }
        });
    }

    /**
     * 显示提货方式弹窗
     */
    private void showDeliveryModePop(final List<TblSptDictionaryEntity> obj) {
        if (CollectionUtility.isNullOrEmpty(obj)) {
            return;
        }
        final ArrayList<String> deliveryModeList = new ArrayList<String>();
        for (TblSptDictionaryEntity entity : obj) {
            deliveryModeList.add(entity.getDictValue());
        }
        final BaseBottomFlowFragment popup = BaseBottomFlowFragment.newInstance("请选择提货方式", deliveryModeList, curDeliveryModePosition);
        popup.setOnTagSelectListener(new BaseBottomFlowFragment.OnTagSelectListener() {
            @Override
            public void getSelectText(String text, int position) {
                popup.dismiss();
                curDeliveryModePosition = position;
                curDeliveryMode = obj.get(position).getDictKey();
                ((IFormNameValue) lLayout_form_deal.getChildAt(1)).setValue(obj.get(position).getDictValue());
            }
        });
        popup.show(getSupportFragmentManager(), "");
    }

    /**
     * 显示质量标准弹窗
     */
    private void showQulityCfgPop(final List<TblSptDictionaryEntity> obj) {
        if (CollectionUtility.isNullOrEmpty(obj)) {
            return;
        }
        final ArrayList<String> deliveryModeList = new ArrayList<String>();
        for (TblSptDictionaryEntity entity : obj) {
            deliveryModeList.add(entity.getDictValue());
        }
        final BaseBottomFlowFragment popup = BaseBottomFlowFragment.newInstance("请选择质量标准", deliveryModeList, curDeliveryModePosition);
        popup.setOnTagSelectListener(new BaseBottomFlowFragment.OnTagSelectListener() {
            @Override
            public void getSelectText(String text, int position) {
                popup.dismiss();
                curProductQulityPosition = position;
                curProductQulity = obj.get(position).getDictKey();
                ((IFormNameValue) lLayout_form_product.getChildAt(1)).setValue(obj.get(position).getDictValue());
            }
        });
        popup.show(getSupportFragmentManager(), "");
    }

    /**
     * 显示品名弹窗
     */
    private void showProductNamePop(final List<TblScfSettingEntity> obj) {
        if (CollectionUtility.isNullOrEmpty(obj)) {
            return;
        }
        final ArrayList<String> productList = new ArrayList<String>();
        for (TblScfSettingEntity entity : obj) {
            productList.add(ProductTypeUtility.getProductName(entity.getProductType()));
        }
        final BaseBottomFlowFragment popup = BaseBottomFlowFragment.newInstance("请选择商品", productList, curProductTypePosition);
        popup.setOnTagSelectListener(new BaseBottomFlowFragment.OnTagSelectListener() {
            @Override
            public void getSelectText(String text, int position) {
                popup.dismiss();
                curProductTypePosition = position;
                curProductType = obj.get(position).getProductType();
                settingEntity = obj.get(position);
                ((IFormNameValue) lLayout_form_product.getChildAt(0)).setValue(ProductTypeUtility.getProductName(curProductType));
            }
        });
        popup.show(getSupportFragmentManager(), "");
    }

    /**
     * 根据品名获取 商品其它信息
     */
    private void getFormConfigDiff(final int position) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<FormConfigEntity>() {
            @Override
            public FormConfigEntity requestService() throws DBException, ApplicationException {
                FormConfigDiffUpEntity upEntity = new FormConfigDiffUpEntity();
                upEntity.setProductType(curProductType);
                return Client.getService(IRequestFormConfigService.class).getFormConfigDiff(upEntity);
            }

            @Override
            public void onDataSuccessfully(FormConfigEntity obj) {
                if (null != obj) {
                    curProductInfo = obj;
                    if (position == 1) {
                        //获取自量标准
                        List<String> productQulitys = StringUtils.split(obj.getQualityCfg(), ",");
                        List<TblSptDictionaryEntity> productQulityEntity = DictionaryUtility.getListByCategory(AppConstant.DICT_PRODUCT_QUALITY);
                        List<TblSptDictionaryEntity> curProductQulityEntity = new ArrayList<TblSptDictionaryEntity>();
                        for (String productQulity : productQulitys) {
                            for (TblSptDictionaryEntity tblSptDictionaryEntity : productQulityEntity) {
                                if (tblSptDictionaryEntity.getDictKey().equals(productQulity)) {
                                    curProductQulityEntity.add(tblSptDictionaryEntity);
                                }
                            }
                        }
                        showQulityCfgPop(curProductQulityEntity);
                    }

                    if (position == 3) {
//                        //获取提货方式
//                        JSONObject dmObj = null;
//                        List <String> dms = null;
//                        List <TblSptDictionaryEntity> deliveryModes = DictionaryUtility.getListByCategory ( AppConstant.DICT_DELIVERY_MODE );
                        List<TblSptDictionaryEntity> curDeliveryModes = new ArrayList<TblSptDictionaryEntity>();
//                        try {
//                            dmObj = new JSONObject ( obj.getDeliveryModeJsonCfg ( ) );
//                            dms = StringUtils.split ( dmObj.getString ( "DM" ), "," );
//                        } catch (JSONException e) {
//                            e.printStackTrace ( );
//                        }
//                        for ( String dm : dms ) {
//                            for ( TblSptDictionaryEntity deliveryMode : deliveryModes ) {
//                                if ( deliveryMode.getDictKey ( ).equals ( dm ) ) {
//                                    curDeliveryModes.add ( deliveryMode );
//                                }
//                            }
//                        }
                        //提货方式暂时默认为：
                        TblSptDictionaryEntity entity = new TblSptDictionaryEntity();
                        entity.setDictKey("R");
                        entity.setDictValue("转货权");
                        curDeliveryModes.add(entity);
                        showDeliveryModePop(curDeliveryModes);
                    }
                }
            }
        });

    }

    /**
     * 校验数据
     *
     * @return
     */
    private boolean checkForm() {
        if (!checkEmptyIntput(lLayout_form_company, lLayout_form_product, lLayout_form_deal))
            return false;

        if (StringUtility.isNullOrEmpty(upEntity.getLicenseId())) {
            Toast.makeText(this, "请上传所需证件", Toast.LENGTH_SHORT).show();
            return false;
        }

        upEntity.setBuyerCompanyTag(buyCompany.getCompanyTag());//采购方
        upEntity.setSellerCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());//卖方企业
        upEntity.setProductName(ProductTypeUtility.getProductName(curProductType));//品名
        upEntity.setProductType(curProductType);
        upEntity.setProductQuality(curProductQulity);//质量标准
        List<String> productPlaces = StringUtils.split(curProductInfo.getProductPlaceCfg(), ",");
        upEntity.setProductPlace(productPlaces.get(0));//商品产地
        upEntity.setProductNumber(new BigDecimal(((IFormNameValue) lLayout_form_deal.getChildAt(2)).getValue()));//数量
        upEntity.setSellPrice(new BigDecimal(((IFormNameValue) lLayout_form_deal.getChildAt(3)).getValue()));//单价
        List<String> deliveryPlaces = StringUtils.split(curProductInfo.getDeliveryPlaceCfg(), ",");
        upEntity.setDeliveryPlace(deliveryPlaces.get(0));//交货地(默认取第一个交货地)
        upEntity.setDeliveryTimeFrom(new Date());//交货期从
        upEntity.setDeliveryTime(DateUtility.parseToDate(((IFormNameValue) lLayout_form_deal.getChildAt(0)).getValue(), DATE_FORMAT));//交货期
        upEntity.setTemplateId("S");//电子合同模板id
        upEntity.setPriceUnit("T");//价格单位
        upEntity.setNumberUnit("T");//数量单位
        List<String> tradeModes = StringUtils.split(curProductInfo.getTradeModeCfg(), ",");
        upEntity.setTradeMode(tradeModes.get(0));//贸易方式,默认取第一个贸易方式
        upEntity.setWareHouse(((IFormNameValue) lLayout_form_product.getChildAt(2)).getValue());//仓库
        upEntity.setDeliveryMode(curDeliveryMode);//提货方式
        upEntity.setSellBond("100");//销售保证金比例
        upEntity.setBuyPrice(new BigDecimal(((IFormNameValue) lLayout_form_deal.getChildAt(3)).getValue()));//单价
        upEntity.setBuyBond(String.valueOf(settingEntity.getBondRate().doubleValue()));
        upEntity.setGoodsOwner(LoginUserContext.getLoginUserDto().getUserId());//货权归属企业
        upEntity.setBuyerStatus("AB");
        upEntity.setSellerStatus("AB");
        upEntity.setProxyStatus("AB");
        upEntity.setScfStatus("AB");//贸易申请状态
        upEntity.setSubmitUser(LoginUserContext.getLoginUserDto().getUserId());
        upEntity.setSubmitTime(new Date());
        upEntity.setSource(NegoConstant.NegoSource.Android);
        return true;
    }

    private boolean checkEmptyIntput(LinearLayout... lls) {
        for (LinearLayout ll : lls) {
            for (int i = 0; i < ll.getChildCount(); i++) {
                String name = ((IFormNameValue) ll.getChildAt(i)).getName();
                String item = ((IFormNameValue) ll.getChildAt(i)).getValue();
                if (StringUtility.isNullOrEmpty(item.trim())) {
                    Toast.makeText(this, name + "不可为空", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply:
                if (checkForm()) {
                    submitScfApplyData();
                }
                break;
            case R.id.tv_preView:
                if (checkForm()) {
                    Intent intent = new Intent(this, TraderAgentPreViewActivity.class);
                    intent.putExtra("entity", JsonUtility.toJSONString(upEntity));
                    intent.putExtra("companyName", buyCompany.getCompanyName());
                    startActivity(intent);
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    /**
     * 提交贸易代理表单申请
     */
    private void submitScfApplyData() {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                Client.getService(IScfApplyDataService.class).submitScfApplyData(upEntity);
                return true;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (null != obj && obj) {
                    ToastHelper.showMessage("贸易代理申请成功");
                    finish();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mClickPosition = position;
        KeyBoardUtils.hideSoftInput(this);
        showCameraPopWindow(false, false);
    }
}