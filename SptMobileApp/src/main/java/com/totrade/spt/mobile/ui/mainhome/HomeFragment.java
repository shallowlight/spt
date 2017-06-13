package com.totrade.spt.mobile.ui.mainhome;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.activity.entity.ActivityDownEntity;
import com.autrade.spt.activity.entity.QueryActivityPage;
import com.autrade.spt.activity.service.inf.IActivityService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.adapter.HomeScrollPageAdapter;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.login.LoginActivity;
import com.totrade.spt.mobile.ui.notifycenter.NotifyActivity;
import com.totrade.spt.mobile.utility.IntentUtils;
import com.totrade.spt.mobile.utility.ObjUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CircleImageView;
import com.totrade.spt.mobile.view.customize.GradationScrollView;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;
import com.totrade.spt.mobile.base.BaseSptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页标题栏以及轮播图
 *
 * @author huangxy
 * @date 2017/4/17
 */
public class HomeFragment extends BaseSptFragment<HomeActivity> implements GradationScrollView.ScrollViewListener, View.OnClickListener {

    private GradationScrollView scrollView;
    private ViewPager viewPager;
    private TextView tv_account_name;
    private CircleImageView civ_img_left;
    private TextView iv_message;

    private int imageHeight;

    public HomeFragment() {
        setContainerId(R.id.fl_body);
    }

    @Override
    public int setFragmentLayoutId() {
        return R.layout.fragment_mainhome;
    }

    @Override
    protected void initView() {
        setHead();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        iv_message.setOnClickListener(this);
        setUnReadMsg(mActivity.getUnReadNum());
        initData();
    }

    private void initData() {
        getPortrait();
        if (LoginUserContext.isAnonymous()) {
            iv_message.setSelected(false);
        }
    }

    @Override
    public void setUnReadMsg(boolean unReadMsg) {
        if (mRootView != null) {
            iv_message.setSelected(unReadMsg);
        }
    }

    /**
     * 获取登录名
     * 获取用户头像
     */
    private void getPortrait() {
        List<String> accounts = new ArrayList<>();
        if (LoginUserContext.isAnonymous()) {
            tv_account_name.setText("请登录");
            civ_img_left.setImageResource(R.drawable.home_portrait);
        } else {
            String account = LoginUserContext.getLoginUserDto().getImUserAccid();
            //设置用户昵称
            tv_account_name.setText(LoginUserContext.getLoginUserDto().getUserName());
            accounts.add(account);
            InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts);
            future.setCallback(mFutureRequestCallback);
        }
    }

    private FutureRequestCallback<List<NimUserInfo>> mFutureRequestCallback = new FutureRequestCallback<List<NimUserInfo>>() {
        @Override
        public void onSuccess(List<NimUserInfo> param) {
            if (param != null && param.size() > 0) {
                NimUserInfo nimUserInfo = param.get(0);
                //设置用户头像
                if (ObjUtils.isEmpty(nimUserInfo.getAvatar()))
                    civ_img_left.setImageResource(R.drawable.home_portrait);
                else Picasso.with(getActivity())
                        .load(nimUserInfo.getAvatar())
                        .placeholder(R.drawable.home_portrait)
                        .error(R.drawable.home_portrait)
                        .into(civ_img_left);
            }
        }
    };

    private void setHead() {
        scrollView = findView(R.id.scrollView);
        viewPager = findView(R.id.viewPager);
        tv_account_name = findView(R.id.tv_account_name);
        civ_img_left = findView(R.id.civ_img_left);
        iv_message = findView(R.id.iv_message);

        tv_account_name.setOnClickListener(this);

        viewPager.setFocusable(true);
        viewPager.setFocusableInTouchMode(true);
        viewPager.requestFocus();
        setTitleToIncludeStatusBar();
        initListeners();
        getAction();

        addFragments(new HomeNoticeFragment());
        addFragments(new HomeNewsFragment());
        addFragments(new IndexFragment());
        addFragments(new DealListFragment());
    }

    private void getAction() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<ActivityDownEntity>>() {
            @Override
            public PagesDownResultEntity<ActivityDownEntity> requestService() throws DBException, ApplicationException {
                QueryActivityPage page = new QueryActivityPage();
                page.setPageNo(1);
                page.setPageSize(3);
                return Client.getService(IActivityService.class).getActivityList(page);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<ActivityDownEntity> obj) {
                if (obj != null && !CollectionUtility.isNullOrEmpty(obj.getDataList())) {
                    String[] urls = new String[obj.getDataList().size()];
                    for (int i = 0; i < obj.getDataList().size(); i++) {
                        urls[i] = Client.getActionUrl(String.valueOf(obj.getDataList().get(i).getImgFileId()));
                    }
                    final HomeScrollPageAdapter homeScrollPageAdapter = new HomeScrollPageAdapter(urls, viewPager, (LinearLayout) findView(R.id.ll_point));
                    viewPager.setAdapter(homeScrollPageAdapter);

                    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            homeScrollPageAdapter.selectPoint(position % homeScrollPageAdapter.ids.length);
                        }
                    });
                    homeScrollPageAdapter.selectPoint(0);
                    homeScrollPageAdapter.startRoll();
                    viewPager.setCurrentItem(1024 * homeScrollPageAdapter.ids.length);
                }
            }
        });

    }

    /**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {
        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageHeight = viewPager.getHeight();
                scrollView.setScrollViewListener(HomeFragment.this);
            }
        });
    }

    @Override
    public void onScrollChanged(GradationScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色
            findView(R.id.fl_title).setBackgroundColor(Color.argb(0, 48, 169, 222));
        } else if (y > 0 && y <= imageHeight) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = (float) y / imageHeight;
            float alpha = (255 * scale);
            findView(R.id.fl_title).setBackgroundColor(Color.argb((int) alpha, 48, 169, 222));
        } else {    //滑动到banner下面设置普通颜色
            findView(R.id.fl_title).setBackgroundColor(Color.argb(255, 48, 169, 222));
        }
    }

    public void setTitleToIncludeStatusBar() {
        FrameLayout.LayoutParams mp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.topMargin = SysInfoUtil.getStatusBarHeight();
        findView(R.id.fl_title_inner).setLayoutParams(mp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_message:
                if (!isLogin()) return;
                startActivity(NotifyActivity.class);
                break;
            case R.id.tv_account_name:
                isLogin();
                break;
        }
    }

    private boolean isLogin() {
        if (LoginUserContext.isAnonymous()) {
            IntentUtils.startActivity(getActivity(), LoginActivity.class);
            return false;
        }
        return true;
    }
}
