package com.totrade.spt.mobile.ui.login.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.autrade.spt.master.entity.NewUserRegisterEntity;
import com.autrade.spt.zone.dto.QueryPageZoneFocusUpEntity;
import com.autrade.spt.zone.dto.ZoneFocusUpEntity;
import com.autrade.spt.zone.entity.TblZoneRequestFocusEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestFocusService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.DesUtility;
import com.autrade.stage.utility.JsonUtility;
import com.totrade.spt.mobile.base.BaseActivity;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.FocusContext;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.SysInfoUtil;
import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 添加我关注
 *
 * @author huangxy
 * @date 2017/5/17
 */
public class AddFocusFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private BaseActivity activity;
    private ViewGroup rootView;
    private GridView gridview;

    private int[] bgDrawable;
    private GridViewAdapter adapter;
    private TextView tv_done;
    private ImageView iv_noti;
    private boolean statusBar;
    private List<ProductTypeDto> productTypeDtos;

    public AddFocusFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_add_focus, container, false);
        title();
        gridView();
        initData();
        return rootView;
    }

    private void initData() {
        findZoneRequestFocusList();
    }

    private void gridView() {
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        productTypeDtos = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, null, 2);
        bgDrawable = new int[]{R.drawable.circle_1, R.drawable.circle_2, R.drawable.circle_3, R.drawable.circle_4, R.drawable.circle_5, };
        adapter = new GridViewAdapter();
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
    }

    private void title() {
        ((TextView) rootView.findViewById(R.id.title)).setText("选择关注");
        rootView.findViewById(R.id.iv_back).setVisibility(View.INVISIBLE);
        iv_noti = (ImageView) rootView.findViewById(R.id.iv_noti);
        iv_noti.setVisibility(View.VISIBLE);
        iv_noti.setBackgroundResource(R.drawable.close);
        if (statusBar) {
            setTitleToIncludeStatusBar();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv_noti.getLayoutParams();
            int margin = DensityUtil.dp2px(activity, 5);
            int length = DensityUtil.dp2px(activity, 20);
            params.height = params.width = length;
            params.setMargins(margin, margin, margin, margin);
            iv_noti.setLayoutParams(params);
        }

        iv_noti.setOnClickListener(this);

        tv_done = (TextView) rootView.findViewById(R.id.tv_done);
        tv_done.setOnClickListener(this);
        tv_done.setClickable(false);
        tv_done.setBackgroundResource(R.color.ui_gray_light2);
    }

    public void setStatusBar(boolean statusBar) {
        this.statusBar = statusBar;
    }


    class GridViewAdapter extends BaseAdapter {
        public GridViewAdapter() {
            typeList = new ArrayList<>();
        }

        List<String> typeList;

        @Override
        public int getCount() {
            return productTypeDtos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_focus, parent, false);
            TextView tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            ImageView iv_right_symbol = (ImageView) convertView.findViewById(R.id.iv_right_symbol);
            tv_product_name.setText(productTypeDtos.get(position).getTypeName());
            tv_product_name.setBackgroundResource(bgDrawable[position % bgDrawable.length]);

            iv_right_symbol.setVisibility(typeList.contains(productTypeDtos.get(position).getProductType()) ? View.VISIBLE : View.GONE);
            convertView.setTag(productTypeDtos.get(position).getProductType());

            return convertView;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String productType = (String) view.getTag();
        if (adapter.typeList.contains(productType)) {
            adapter.typeList.remove(productType);
        } else {
            adapter.typeList.add(productType);
        }

        if (adapter.typeList.size() == 0) {
            tv_done.setBackgroundResource(R.color.ui_gray_light2);
            tv_done.setClickable(false);
        } else {
            tv_done.setBackgroundResource(R.color.blue_primary);
            tv_done.setClickable(true);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_noti:
                close();
                break;
            case R.id.tv_done:
                StringBuffer sb = new StringBuffer();
                for (String type : adapter.typeList) {
                    sb.append(type);
                    sb.append("|");
                }
                sb.deleteCharAt(sb.length() - 1);
                addZoneRequestFocus(sb.toString());
                setAliaAndTag(adapter.typeList);
                close();
                break;

        }
    }

    private void close() {
        if (!statusBar){
            activity.finish();      //注册页面
        } else {
            activity.popBack();
        }
    }

    public void setTitleToIncludeStatusBar() {
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.topMargin = SysInfoUtil.getStatusBarHeight();
        mp.bottomMargin = 10;
        rootView.findViewById(R.id.fl_title).setLayoutParams(mp);
    }

    //添加关注
    private void addZoneRequestFocus(final String types) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {
            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                ZoneFocusUpEntity upEntity = new ZoneFocusUpEntity();
                String newUserRegisterEntity;
                String userid;
                if (LoginUserContext.isAnonymous()) {
                    newUserRegisterEntity = SharePreferenceUtility.spGetOut(activity, SharePreferenceUtility.REGISTER_PERSONAL, "");
                    NewUserRegisterEntity entity = JsonUtility.toJavaObject(newUserRegisterEntity, NewUserRegisterEntity.class);
                    userid = entity.getUserId();
                } else {
                    userid = LoginUserContext.getLoginUserId();
                }
                upEntity.setSubmitUserId(userid);
                upEntity.setProductType(types);
                Client.getService(IZoneRequestFocusService.class).addZoneRequestFocus(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                refreshBroadcast(AppConstant.INTENT_ACTION_FOUCS_REFRESH);
                FocusContext.update(activity, adapter.typeList);
                LogUtils.i("", "addZoneRequestFocus Success");
            }
        });
    }

    //更新关注的产品后更新标签
    private void setAliaAndTag(List<String> group) {
        final HashSet<String> hashSet = new HashSet<>();
        // 标签
        for (String type : group) {
            hashSet.add(type);
        }
        // 设置别名 UserId
        String alias = LoginUserContext.getLoginUserDto().getUserId();
        JPushInterface.setAliasAndTags(activity, alias, hashSet, new TagAliasCallback() {
            @Override
            public void gotResult(int responseCode, String alias, Set<String> tags) {
                // 0 表示调用成功。 其他返回码请参考错误码定义。
            }
        });

    }

    //查询关注
    private void findZoneRequestFocusList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<TblZoneRequestFocusEntity>>() {
            @Override
            public PagesDownResultEntity<TblZoneRequestFocusEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneFocusUpEntity upEntity = new QueryPageZoneFocusUpEntity();
                String newUserRegisterEntity;
                String userid;
                if (LoginUserContext.isAnonymous()) {
                    newUserRegisterEntity = SharePreferenceUtility.spGetOut(activity, SharePreferenceUtility.REGISTER_PERSONAL, "");
                    NewUserRegisterEntity entity = JsonUtility.toJavaObject(newUserRegisterEntity, NewUserRegisterEntity.class);
                    userid = entity.getUserId();
                } else {
                    userid = LoginUserContext.getLoginUserId();
                }
                upEntity.setUserId(userid);
                upEntity.setPageNo(1);
                upEntity.setPageSize(100);
                return Client.getService(IZoneRequestFocusService.class).findZoneRequestFocusList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<TblZoneRequestFocusEntity> obj) {
                if ((obj != null) && (obj.getDataList().size() > 0)) {
                    for (TblZoneRequestFocusEntity entity : obj.getDataList()) {
                        adapter.typeList.add(entity.getProductType());
                        adapter.notifyDataSetChanged();
                    }
                    tv_done.setBackgroundResource(R.color.blue_primary);
                    tv_done.setClickable(true);
                }
            }
        });
    }

    /**
     * 发出刷新广播
     *
     * @param refreshAction
     */
    private void refreshBroadcast(String refreshAction) {
        Intent refreshIntent = new Intent();
        refreshIntent.setAction(refreshAction);
        activity.sendBroadcast(refreshIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activity != null) {
                if (!isAdded()) return false;
                close();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
