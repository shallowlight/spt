package com.totrade.spt.mobile.view.im;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.deal.entity.ContractDownEntity;
import com.autrade.spt.deal.service.inf.IContractService;
import com.autrade.spt.master.entity.QueryTemplateUpEntity;
import com.autrade.spt.master.service.inf.ITemplateService;
import com.autrade.spt.nego.entity.GetBuySellIntensionDetailUpEntity;
import com.autrade.spt.nego.entity.QueryBizOpHisUpEntity;
import com.autrade.spt.nego.entity.TblBizOpHisEntity.BizOpType;
import com.autrade.spt.nego.entity.TblBizOpHisEntity.BizType;
import com.autrade.spt.nego.service.inf.IBizOpHisService;
import com.autrade.spt.nego.service.inf.IBuySellIntensionService;
import com.autrade.stage.droid.annotation.Injection;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.DictionaryUtility;
import com.totrade.spt.mobile.utility.DispUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.HostTimeUtility;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.ItemNameValue;

/**
 *  Activity by order detail
 *  Created by iUserName on 2016/8/31.
 *  传入值: Constant.TAG_BIZID 挂单标识
 *          Constant.TAG_NEGOSTATUS 挂单状态
 */
public class OrderInfoActivity extends SptMobileActivityBase implements OnClickListener
{
	@Injection(type = "com.autrade.spt.nego.stub.service.impl.BuySellIntensionServiceStub")
	private IBuySellIntensionService buySellIntensionService;
	@Injection(type ="com.autrade.spt.deal.stub.service.impl.ContractServiceStub")
	private IContractService contractService;

	@Injection(type = "com.autrade.spt.nego.stub.service.impl.BizOpHisServiceStub")
	private IBizOpHisService bizOpHisService;

	@Injection(type = "com.autrade.spt.master.stub.service.impl.TemplateServiceStub")
	private ITemplateService templateService;
	private final String COMPANY_COPYRIGHT_TAG = "匿名企业";

	@BindViewId(R.id.llExpand)
	private LinearLayout llExpand;
	@BindViewId(R.id.title)
	private CommonTitle3 title;

	@BindViewId(R.id.llBuyerCo)
	private LinearLayout llBuyerCo;
	@BindViewId(R.id.lblBuyerCoName)
	private TextView lblBuyerCoName;
	@BindViewId(R.id.lblBuyerCo)
	private TextView lblBuyerCo;
	@BindViewId(R.id.llSellerCo)
	private LinearLayout llSellerCo;
	@BindViewId(R.id.lblSellerCoName)
	private TextView lblSellerCoName;
	@BindViewId(R.id.lblSellerCo)
	private TextView lblSellerCo;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_info);
		String bizId = getIntent().getStringExtra(AppConstant.TAG_BIZID);
		//查看合同详情
		quetyContractDetail(bizId);
		findViewById(R.id.imgDealStatus).setVisibility(View.VISIBLE);
	}



	/** 查询合同详细 */
	private void quetyContractDetail(final String contractId)
	{
		SubAsyncTask.create().setOnDataListener(this, true, new OnDataListener<ContractDownEntity>()
		{
			@Override
			public ContractDownEntity requestService() throws DBException,ApplicationException
			{
				return contractService.getContractDetail(contractId, LoginUserContext.getLoginUserDto().getUserId());
			}

			@Override
			public void onDataSuccessfully(ContractDownEntity entity)
			{
				if(entity!=null)
				{
					updateView(entity);
				}
			}
		});
	}

	/** 合同详情 更新界面 */
	private void updateView(ContractDownEntity entity)
	{
		((TextView)findViewById(R.id.tvDealTime)).setText("成交时间:" + FormatUtil.date2String(entity.getUpdateTime(),"yyyy/MM/dd HH:mm"));
		String productType=entity.getProductType();
		boolean isSL=productType.startsWith("SL");
		boolean isGT=productType.startsWith("GT");
		String deliveryTime=DispUtility.deliveryTimeToDisp(entity.getDeliveryTime(),entity.getProductType(),entity.getTradeMode());

		String deliveryPlace = DictionaryUtility.getValue(SptConstant.SPTDICT_DELIVERY_PLACE, entity.getDeliveryPlace());
		if(productType.startsWith("SL") && TextUtils.isEmpty(deliveryPlace))
		{
			deliveryPlace = entity.getDeliveryPlaceName();
		}

		String productQuality=isGT?format.format(entity.getProductQualityEx1()):
				DictionaryUtility.getValue(SptConstant.SPTDICT_PRODUCT_QUALITY,entity.getProductQuality());
		String[][] detailArray=new String[][]
				{
						{"编号",              entity.getContractId()},
						{"品名",              entity.getProductName()},
						{"贸易方式",          entity.getTradeMode(),SptConstant.SPTDICT_TRADE_MODE},
						{isSL?"级别":null,    entity.getProductClass()},
						{isSL?"牌号":null,    entity.getBrand()},
						{isGT?"铁品位":"质量标准",productQuality},
						{"产地",              entity.getProductPlace(),SptConstant.SPTDICT_PRODUCT_PLACE},
						{isSL?"交货港":"交货地",deliveryPlace},
						{isSL?"交货仓库":null,entity.getWareHouseName()},
						{(isGT && entity.getTradeMode().equals(SptConstant.TRADEMODE_FOREIGN))?"最晚提单日":"交货期",deliveryTime},
						{"提货方式",           entity.getDeliveryMode(),SptConstant.SPTDICT_DELIVERY_MODE},
						{"数量",                DispUtility.productNum2Disp(entity.getDealNumber(), null, entity.getNumberUnit())},
						{"价格",                DispUtility.price2Disp(entity.getProductPrice(), entity.getPriceUnit(), entity.getNumberUnit())},
						{"定金",                entity.getBond(),SptConstant.SPTDICT_BOND},
						{"付款方式",            entity.getPayMethod(),SptConstant.SPTDICT_PAY_METHOD},
						{"备注",                entity.getMemo()}
				};
		for(String[] ss : detailArray)
		{
			if(TextUtils.isEmpty(ss[0]) || TextUtils.isEmpty(ss[1])) continue;
			if(ss.length >2)
			{
				llExpand.addView(ItemNameValue.creatItem(this,ss[0],ss[1],ss[2]));
			}
			else  if(ss[0].equals("价格") || ss[0].equals("询价时效"))
			{
				llExpand.addView(ItemNameValue.creatItem(this,ss[0],ss[1],null, 0xffff7829));
			}
			else
			{
				llExpand.addView(ItemNameValue.creatItem(this,ss[0],ss[1]));
			}
		}

		updateCoName(entity);
	}

	private void updateCoName(ContractDownEntity entity)
	{
		if(entity.getBuyerCompanyName().equals(COMPANY_COPYRIGHT_TAG))
		{
			lblBuyerCoName.setText("查看");
			lblBuyerCoName.setTag(entity.getBuyerCompanyTag());
			lblBuyerCoName.setOnClickListener(this);
		}
		else
		{
			lblBuyerCoName.setText(entity.getBuyerCompanyName());
		}

		if(entity.getSellerCompanyName().equals(COMPANY_COPYRIGHT_TAG))
		{
			lblSellerCoName.setText("查看");
			lblSellerCoName.setTag(entity.getSellerCompanyTag());
			lblSellerCoName.setOnClickListener(this);
		}
		else
		{
			lblSellerCoName.setText(entity.getSellerCompanyName());
		}
	}



	/** 查询需要多少通商宝 */
	private void queryTSB(final TextView view)
	{
		SubAsyncTask.create().setOnDataListener(this,true,new OnDataListener<String>()
		{
			@Override
			public String requestService() throws DBException,ApplicationException
			{
				QueryTemplateUpEntity upEntity = new QueryTemplateUpEntity();
				upEntity.setTemplateCat(SptConstant.TEMPLATE_NEGO_CONTRACT_VIEW_CAT);
				upEntity.setTemplateTag(SptConstant.TEMPLATE_NEGO_VIEW_DEDUCT_AMT);
				upEntity.setTemplateId(SptConstant.TEMPLATE_NEGO_VIEW_DEDUCT_AMT);
				return templateService.getTemplateMaster(upEntity).getTemplateContent();
			}

			@Override
			public void onDataSuccessfully(String obj)
			{
				if(!TextUtils.isEmpty(obj))
				{
					requestCoName(obj,view);
				}
			}
		});
	}

	/** 花通商宝查看企业名称 */
	private void requestCoName(String tsb,final TextView view)
	{
		final String bizId = getIntent().getStringExtra(AppConstant.TAG_BIZID);
		String msg = "查看企业信息需要消费" + tsb +"通商宝 , 是否继续？";
		CustomDialog.Builder builder = new CustomDialog.Builder(this, msg);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SubAsyncTask.create().setOnDataListener(OrderInfoActivity.this, true, new OnDataListener<ContractDownEntity>()
				{
					@Override
					public ContractDownEntity requestService() throws DBException,ApplicationException
					{
						QueryBizOpHisUpEntity upEntity = new QueryBizOpHisUpEntity();
						upEntity.setBizId(bizId);
						upEntity.setUserId(LoginUserContext.getLoginUserId());
						upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
						upEntity.setBizType(BizType.Q);
						upEntity.setOpType(BizOpType.V);
						upEntity.setSubmitTime(HostTimeUtility.getTimeOnAsyncTask());
						bizOpHisService.submitBizOpHis(upEntity);

						GetBuySellIntensionDetailUpEntity upEntity2 = new GetBuySellIntensionDetailUpEntity();
						upEntity2.setSubmitId(bizId);
						upEntity2.setUserId(LoginUserContext.getLoginUserId());
						return contractService.getContractDetail(bizId, LoginUserContext.getLoginUserDto().getUserId());

					}

					@Override
					public void onDataSuccessfully(ContractDownEntity entity)
					{
						if(entity!=null)
						{
							updateCoName(entity);
						}
					}
				});
			}
		});
		builder.create().show();
	}


	@Override
	public void onClick(View v)
	{
		if(v == lblBuyerCoName || v == lblSellerCoName)
		{
			if(((TextView)v).getText().toString().equals("查看")
					&& v.getTag()!=null)
			{
				queryTSB((TextView)v);
			}
		}
	}

}
