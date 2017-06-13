package com.totrade.spt.mobile.common;

import android.text.TextUtils;

import com.autrade.spt.common.constants.ConfigKey;
import com.autrade.spt.common.constants.SptConstant;
import com.totrade.spt.mobile.view.R;

import java.util.HashMap;
import java.util.Map;



public class ConstantArray implements SptConstant,ConfigKey
{

	public static final String[][] RUNNINGTYPES=
			{
					{"0","全部"},
					{"1","入金"},
					{"2","出金申请"},
					{"3","出金失败"},
					{"4","出金成功"},
					{"5","支付手续费"},
					{"6","支付货款（保证金）"},
					{"7","收到货款"},
					{"8","通商宝补贴"},
					{"9","通商宝消费"},
					{"10","定金借贷还款"},
					{"11","隔夜收益"},
					{"12","退款业务"},
					{"13","增加支付利息"},
			};
	// 贸易方式
	public static final String[][] TRADEMODE_BUYSELL =
			{
					{"DM#S","内贸销售"},
					{"DM#B","内贸采购"},
					{"FO#S","外贸销售"},
					{"FO#B","外贸采购"}
			};
	// 定金借贷流水类型
	public static final String[][] LOAN_RUNNINGTYPR =
			{
					{"CL","定金借贷"},
					{"CI","计息"},
					{"SR","提前还款"},
					{"FR","到期还款"},
			};


	/**付款方式，贸易方式  */
	public static final String[][] CFG_IMGSTR =
			{
					{ "XHJG", "现" },		 	// 现货交割
					{ "SP", "自" }, 				// 买方自提
					{ "FOB", "FOB" },			// FOB
					{ "EXW", "EXW" }, 			// EXW
					{ "DZCDZY", "电" },			// 电子仓单转移
					{ "DE", "送" },				// 卖方送到
					{ "CT", "含" }, 				// 车板含税
					{ "CIF", "CIF" }, 			// CIF
					{ "CFR", "CFR" },			// CFR
					{ "TCB", "船" },				//二程船舱底
					{ "CRBL", "L/C" },			// 90天信用证（B/L）
					{ "CRD0", "L/C" }, 			// 90天信用证（D/0）
					{ "CRIN", "L/C" }, 			// 即期信用证
					{ "CRIN1", "L/C" },			// 即期信用证（B/L）
					{ "CRIN2", "L/C" }, 		// 即期信用证（D/O）
					{ "NEGO", "协" },			// 双方协商
					{ "T/T", "付" }, 			// T/T付款
					{ "CRAI", "L/C" },			// 90天信用证（开证后）
					{ "CR90", "L/C" }, 			// 90天信用证
					{ "AP", "汇" }, 				// 承兑汇票
					{ "BAP", "汇" }, 			// 银行承兑汇票
					{ "CAP", "汇" }, 			// 商业承兑汇票
					{ "CA", "现" }, 				// 现款
					{ "CR", "L/C" }, 			// 国内信用证
					{ "CR30", "L/C" }, 			// 30天信用证
					{ "CR30BL", "L/C" }, 		// 30天信用证（B/L）
					{ "CR30DO", "L/C" }, 		// 30天信用证（D/O）
					{ "CR60", "L/C" }, 			// 60天信用证
					{ "TT", "电" } 			// 电汇


			};

	public static final String[][] IRON_GRADE =
			{
					{"不限", "0", "100"},
					{"[30-45)", "30", "45"},
					{"[45-50)", "45", "50"},
					{"[50-55)", "50", "55"},
					{"[55-59)", "55", "59"},
					{"[59-61)", "59", "61"},
					{"[61-64)", "61", "64"},
					{"[64-67)", "64", "67"},
					{"67以上", "67", "100"}
			};

	public static final String[][] SUPLEVELS =
			{
					{SUPLEVEL_ZS ,SupLevel.L1.getText() },		// 钻石级
					{SUPLEVEL_HG ,SupLevel.L2.getText() }, 	// 皇冠级
					{SUPLEVEL_XX ,SupLevel.L3.getText() },	// 星星级
					{SUPLEVEL_HD ,SupLevel.L4.getText() }, 	// 花朵级
					{SUPLEVEL_LC ,SupLevel.L5.getText() }, 	// 冷藏级
					{SUPLEVEL_LD ,SupLevel.L6.getText() }, 	// 冷冻级
					{SUPLEVEL_BLACK ,SupLevel.L99.getText()} // 黑名单
			};

	private static Map<String, Integer> levelMap = new HashMap<>();
	static void initLevelMap()
	{
		levelMap.put(SUPLEVEL_ZS, R.drawable.img_level1_gray);
		levelMap.put(SUPLEVEL_HG, R.drawable.img_level2_gray);
		levelMap.put(SUPLEVEL_XX, R.drawable.img_level3_gray);
		levelMap.put(SUPLEVEL_HD, R.drawable.img_level4_gray);
		levelMap.put(SUPLEVEL_LC, R.drawable.img_level5_gray);
		levelMap.put(SUPLEVEL_LD, R.drawable.img_level6_gray);
		levelMap.put(SUPLEVEL_BLACK, R.drawable.img_level7_gray);
	}
	public static int getLevelGray(String key)
	{
		if (TextUtils.isEmpty(key))
		{
			return 0;
		}
		if (levelMap.isEmpty()) {
			initLevelMap();
		}
		return levelMap.get(key);
	}

	public static final String CATEGORY_JSON = "JSON";

	/**
	 *  获取产品配置的category 与CfgKey 的对应关系
	 */
	public static final String[][] categoryCfgKeys =
	{
			{ SPTDICT_PRODUCT_QUALITY , FORM_CONF_QUALITYCFG }, 									//质量标准配置
			{ SPTDICT_PAY_METHOD , FORM_CONF_PAYMETHODCFG },										//付款方式配置
			{ SPTDICT_NUMBER_UNIT , FORM_CONF_NUMBERUNITCFG },										//数量单位配置
			{ SPTDICT_PRICE_UNIT , FORM_CONF_PRICEUNITCFG }, 										//价格单位配置
			{ SPTDICT_PRODUCT_PLACE , FORM_CONF_PRODUCTPLACECFG },									//商品产地配置
			{ SPTDICT_DELIVERY_PLACE , FORM_CONF_DELIVERYPLACECFG }, 								//交货地配置
			{ AppConstant.CFG_ZONE_BOND , FORM_CONF_BONDCFG },										//专区保证金配置
			{ AppConstant.CFG_NEGO_BOND , FORM_CONF_NEGOBONDCFG }, 									//询价保证金配置
			{ SPTDICT_TRADE_MODE , FORM_CONF_TRADEMODECFG }, 										//贸易方式配置
			{ SPTDICT_DELIVERY_MODE , FORM_CONF_DELIVERYMODECFG },									//提货方式配置
			{ SPTDICT_WAREHOUSE , FORM_CONF_WAREHOUSECFG }, 										//交割仓库配置
			{ SPTDICT_NEGOTIATE_MODE , FORM_CONF_NEGOTIATEMODECFG },								//议价模式配置
			{ AppConstant.CFG_FEE , FORM_CONF_FEECFG },												//手续费配置
			{ AppConstant.CFG_PRODUCTNUMBER , FORM_CONF_NUMBERCFG },								//数量限制配置
			{ AppConstant.CFG_PRODUCTPRICE , FORM_CONF_PRICECFG },									//价格限制配置
			{ AppConstant.CFG_VALIDHOUR , FORM_CONF_HOURCFG },										//时效（时）配置
			{ AppConstant.CFG_VALIDMINUTE , FORM_CONF_MINUTECFG },									//时效（分）配置
			{ AppConstant.CFG_PRODUCTCLASS , FORM_CONF_PRODUCTCLASSCFG },							//商品级别配置
			{ AppConstant.CFG_TKGRADE , FORM_CONF_TPWCFG },											//铁品位限制配置
			{ AppConstant.CFG_ZONEFEE , FORM_CONF_ZONEFEECFG },										//专区手续费配置
			{ AppConstant.CFG_SPOTDAYS , FORM_CONF_SPOTDAYSCFG }, 									//现货天数
//			{ XXXXXXX , FORM_CONF_CREDITPERCENT }, 	//最大使用通商宝的比例（DECIMAL(5,2)）
//			{ XXXXXXX , FORM_CONF_BONDRANGECFG }, //追加保证金跌幅
//			{ XXXXXXX , FORM_CONF_BONDTIMECFG }, //追加保证金时间
//			{ XXXXXXX , FORM_CONF_INTERESTRATECFG }, //利息日利率
//			{ XXXXXXX , FORM_CONF_SELLERBONDCFG },	//卖家保证金比率
								//以下 Json
			{ AppConstant.CFG_ZONE_BOND + CATEGORY_JSON , FORM_CONF_BONDJSONCFG }, 					//专区保证金配置
			{ AppConstant.CFG_NEGO_BOND + CATEGORY_JSON , FORM_CONF_NEGOBONDJSONCFG }, 				//询价保证金配置
			{ SPTDICT_PAY_METHOD + CATEGORY_JSON , FORM_CONF_PAYMETHODJSONCFG }, 					//付款方式配置
			{ SPTDICT_DELIVERY_PLACE + CATEGORY_JSON , FORM_CONF_DELIVERYPLACEJSONCFG }, 			//交货地配置（JSON)
			{ SPTDICT_DELIVERY_MODE + CATEGORY_JSON , FORM_CONF_DELIVERYMODEJSONCFG }				//提货方式配置(JSON)
	};

}
