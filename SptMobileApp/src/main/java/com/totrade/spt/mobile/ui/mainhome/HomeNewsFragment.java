package com.totrade.spt.mobile.ui.mainhome;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autrade.spt.master.entity.QueryNotifyUpEntity;
import com.autrade.spt.master.entity.TblNotifyMasterEntity;
import com.autrade.spt.master.service.inf.INotifyService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.homenews.HomeNewsActivity;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.text.SimpleDateFormat;

/**
 * 首页新闻条目
 *
 * @author huangxy
 * @date 2017/4/17
 */
public class HomeNewsFragment extends BaseSptFragment<HomeActivity> implements View.OnClickListener {

    private TblNotifyMasterEntity tblNotifyMasterEntity;

    public HomeNewsFragment() {
        setContainerId(R.id.fl_news);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_homenews;
    }

    @Override
    protected void initView() {

        findView(R.id.fl_news).setOnClickListener(this);
        findView(R.id.tv_more_news).setOnClickListener(this);
        getNews();
    }

    public void getNews() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<TblNotifyMasterEntity>>() {
            @Override
            public PagesDownResultEntity<TblNotifyMasterEntity> requestService() throws DBException, ApplicationException {
                QueryNotifyUpEntity upEntity = new QueryNotifyUpEntity();
                upEntity.setCurrentPageNumber(1);
                upEntity.setNumberPerPage(1);
                upEntity.setNotifyType("0");
                return Client.getService(INotifyService.class).queryNotifyList(upEntity);
            }

            @Override
            public void onDataSuccessfully(final PagesDownResultEntity<TblNotifyMasterEntity> obj) {
                if (obj != null && !CollectionUtility.isNullOrEmpty(obj.getDataList())) {
                    tblNotifyMasterEntity = obj.getDataList().get(0);
                    ((TextView) findView(R.id.tv_news_subject)).setText(tblNotifyMasterEntity.getSubject());
                    ((TextView) findView(R.id.tv_news_time)).setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(tblNotifyMasterEntity.getCreateTime()));
                    String url = Client.getNewsImgUrl(tblNotifyMasterEntity.getImgFileId());
                    Picasso.with(mActivity).load(url).placeholder(R.drawable.news_default).fit().centerCrop().into((ImageView) findView(R.id.iv_news_img));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_news:
                Intent intent = new Intent(mActivity, HomeNewsActivity.class);
                intent.putExtra("newsUrl", Client.getNewsDetailUrl(tblNotifyMasterEntity.getNotifyId()));
                intent.putExtra("newsType", "detail");
                startActivity(intent);
                break;
            case R.id.tv_more_news:
                Intent intent2 = new Intent(mActivity, HomeNewsActivity.class);
                intent2.putExtra("newsType", "list");
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

}
