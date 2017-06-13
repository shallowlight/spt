package com.totrade.spt.mobile.ui.mainhome;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.master.entity.QueryNotifyUpEntity;
import com.autrade.spt.master.entity.TblNotifyMasterEntity;
import com.autrade.spt.master.service.inf.INotifyService;
import com.autrade.spt.zone.dto.ZoneLastMatchInfoDownEntity;
import com.autrade.spt.zone.dto.ZoneLastMatchInfoUpEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestMatchService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.entity.MessageEntity;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.DataSetAdapter;
import com.totrade.spt.mobile.view.customize.VerticalRollingTextView;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 首页公告块
 *
 * @author huangxy
 * @date 2017/4/1
 */
public class HomeNoticeFragment extends BaseSptFragment<HomeActivity> {

    private LinearLayout llProduct;
    private VerticalRollingTextView tvNotice;

    public HomeNoticeFragment() {
        setContainerId(R.id.fl_notice);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_homenotice;
    }

    @Override
    protected void initView() {
        llProduct = findView(R.id.ll_product);
        tvNotice = findView(R.id.tv_notice);
        getIndex();
        getNotice();
    }

    private void getIndex() {
        findLatestMatchInfoList();
    }

    private void setLayoutParams(View... views) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1.0f;
        for (int i = 0; i < views.length; i++) {
            views[i].setLayoutParams(lp);
        }
    }

    //获取公告列表
    private void getNotice() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<TblNotifyMasterEntity>>() {
            @Override
            public PagesDownResultEntity<TblNotifyMasterEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyUpEntity upEntity = new QueryNotifyUpEntity();
                upEntity.setCurrentPageNumber(1);
                upEntity.setNumberPerPage(3);
                upEntity.setNotifyType("1");
                return Client.getService(INotifyService.class).queryNotifyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(final PagesDownResultEntity<TblNotifyMasterEntity> obj) {
                if (obj != null && !CollectionUtility.isNullOrEmpty(obj.getDataList())) {

                    String[] mStrs = {obj.getDataList().get(0).getSubject(),
                            obj.getDataList().get(1).getSubject(),
                            obj.getDataList().get(2).getSubject(),};
                    tvNotice.setDataSetAdapter(new DataSetAdapter<String>(Arrays.asList(mStrs)) {

                        @Override
                        protected String text(String s) {
                            return s;
                        }
                    });
                    tvNotice.setOnItemClickListener(new VerticalRollingTextView.OnItemClickListener() {
                        @Override
                        public void onItemClick(VerticalRollingTextView view, int index) {
                            MessageEntity messageEntity = new MessageEntity();
                            messageEntity.setMsgType("商品通公告");
                            com.totrade.spt.mobile.ui.notifycenter.NotiListActivity.start(mActivity, messageEntity);
                        }
                    });
                    tvNotice.run();
                }
            }
        });
    }

    private void findLatestMatchInfoList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<List<ZoneLastMatchInfoDownEntity>>() {
            @Override
            public List<ZoneLastMatchInfoDownEntity> requestService() throws DBException, ApplicationException {
                ZoneLastMatchInfoUpEntity upEntity = new ZoneLastMatchInfoUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                return Client.getService(IZoneRequestMatchService.class).findLatestMatchInfoList(upEntity);
            }

            @Override
            public void onDataSuccessfully(List<ZoneLastMatchInfoDownEntity> obj) {
                if (obj != null) {
                    for (int i = 0; i < obj.size(); i++) {
                        if (i > 3) break;

                        View view = initItem(obj.get(i));
                        llProduct.addView(view);
                    }
                }
            }
        });
    }

    @NonNull
    private View initItem(ZoneLastMatchInfoDownEntity entity) {
        View view = View.inflate(mActivity, R.layout.item_homepublic, null);
        TextView tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
        TextView tv_product_price = (TextView) view.findViewById(R.id.tv_product_price);
        TextView tv_price_update = (TextView) view.findViewById(R.id.tv_price_update);
        TextView tv_update_num = (TextView) view.findViewById(R.id.tv_update_num);

        tv_product_name.setText(entity.getProductName());

        String price = entity.getDealPrice().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        String incAmount = entity.getIncAmount().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        if (entity.getIncAmount() == null || entity.getIncAmount().compareTo(BigDecimal.ZERO) == 0) {
            tv_product_price.setText(String.format("%s", price));
            tv_product_price.setTextColor(mActivity.getResources().getColor(R.color.ltBlack));
            tv_product_price.setCompoundDrawables(null, null, null, null);
        } else {
            tv_product_price.setText(price);
            Drawable drawable;
            if (entity.getIncAmount().compareTo(BigDecimal.ZERO) > 0) {
                incAmount = "+" + incAmount;
                tv_product_price.setTextColor(mActivity.getResources().getColor(R.color.zone_red_view));
                drawable = ContextCompat.getDrawable(mActivity, R.drawable.zone_arrow_red);
            } else {
//                incAmount = "-" + incAmount;
                tv_product_price.setTextColor(mActivity.getResources().getColor(R.color.zone_green_view));
                drawable = ContextCompat.getDrawable(mActivity, R.drawable.zone_arrow_green);
            }

            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_product_price.setCompoundDrawables(null, null, drawable, null);
        }
        tv_price_update.setText(incAmount);
        tv_update_num.setText(entity.getIncPercent().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%");

        setLayoutParams(view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tvNotice.stop();
    }
}
