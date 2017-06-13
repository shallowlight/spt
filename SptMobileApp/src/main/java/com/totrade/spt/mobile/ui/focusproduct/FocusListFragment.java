package com.totrade.spt.mobile.ui.focusproduct;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.autrade.spt.master.service.inf.INegoFocusService;
import com.autrade.spt.zone.dto.QueryPageZoneRequestUpEntity;
import com.autrade.spt.zone.dto.ZoneRequestDownEntity;
import com.autrade.spt.zone.entity.TblZoneRequestFocusEntity;
import com.autrade.spt.zone.service.inf.IZoneRequestFocusService;
import com.autrade.spt.zone.service.inf.IZoneRequestService;
import com.autrade.stage.entity.PagesDownResultEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.ui.HomeActivity;
import com.totrade.spt.mobile.ui.maintrade.OrderDeatilActivity;
import com.totrade.spt.mobile.ui.maintrade.adapter.FocusProductListAdapter;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.CustomDialog;
import com.totrade.spt.mobile.view.customize.swiplist.SwipeMenu;
import com.totrade.spt.mobile.view.customize.swiplist.SwipeMenuCreator;
import com.totrade.spt.mobile.view.customize.swiplist.SwipeMenuItem;
import com.totrade.spt.mobile.view.customize.swiplist.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我关注的商品列表
 *
 * @author huangxy
 * @date 2017/1/16
 */
public class FocusListFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private HomeActivity activity;
    private View view;
    private SwipeMenuListView listView;
    private final List<ZoneRequestDownEntity> focusMasterList = new ArrayList<>();
    private FocusProductListAdapter adapter;

    public FocusListFragment() {
        setContainerId(R.id.fl_body);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (HomeActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_focus_list, container, false);
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);
        view.findViewById(R.id.tv_edit_focus).setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        listView.setPullRefreshEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        // 设置修改Menu和删除Menu的点击事件
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final ZoneRequestDownEntity entity = focusMasterList.get(position);
                if (entity == null) {
                    return false;
                }
                switch (index) {
                    case 0: // 删除
                        CustomDialog.Builder delBuilder = new CustomDialog.Builder(activity, "确定删除这条我关注的产品吗？");
                        delBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFocus(entity);
                            }
                        });
                        delBuilder.create().show();
                        break;
                }
                return false;
            }
        });
        creatMenu();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            initData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 添加item修改和删除Menu
     */
    private void creatMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(activity);
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                deleteItem.setWidth(FormatUtil.dip2px(activity, 80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setTitleSize(14);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
    }

    private void initData() {
        findMyFocusZoneRequestList();
    }

    /**
     * 删除询价关注产品
     */
    private void deleteFocus(final ZoneRequestDownEntity entity) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<Boolean>() {

            @Override
            public Boolean requestService() throws DBException, ApplicationException {
                TblZoneRequestFocusEntity upEntity = new TblZoneRequestFocusEntity();
                upEntity.setSubmitUserId(LoginUserContext.getLoginUserDto().getUserId());
                upEntity.setProductType(entity.getProductType());
                upEntity.setDeliveryTime(entity.getDeliveryTime());
                upEntity.setDeliveryPlace(entity.getDeliveryPlace());
                upEntity.setCompanyTag(LoginUserContext.getLoginUserDto().getCompanyTag());
                upEntity.setTradeMode(entity.getTradeMode());
                upEntity.setProductQuality(entity.getProductQuality());
                Client.getService(IZoneRequestFocusService.class).deleteZoneRequestFocus(upEntity);
                return Boolean.TRUE;
            }

            @Override
            public void onDataSuccessfully(Boolean obj) {
                if (obj != null && obj) {
                    ToastHelper.showMessage("删除成功");
                    findMyFocusZoneRequestList();
                }
            }
        });
    }

    /**
     * 获取我关注列表
     */
    private void findMyFocusZoneRequestList() {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<PagesDownResultEntity<ZoneRequestDownEntity>>() {
            @Override
            public PagesDownResultEntity<ZoneRequestDownEntity> requestService() throws DBException, ApplicationException {
                QueryPageZoneRequestUpEntity upEntity = new QueryPageZoneRequestUpEntity();
                upEntity.setUserId(LoginUserContext.getLoginUserId());
                upEntity.setPageNo(1);
                upEntity.setPageSize(200);
                return Client.getService(IZoneRequestService.class).findMyFocusZoneRequestList(upEntity);
            }

            @Override
            public void onDataSuccessfully(PagesDownResultEntity<ZoneRequestDownEntity> obj) {
                if (obj != null && obj.getDataList() != null) {
                    focusMasterList.clear();
                    focusMasterList.addAll(obj.getDataList());
                    view.findViewById(R.id.empty).setVisibility(focusMasterList.size() > 0 ? View.GONE : View.VISIBLE);
                    if (adapter == null) {
                        adapter = new FocusProductListAdapter(activity);
                        adapter.notifyChanged(focusMasterList);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyChanged(focusMasterList);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
//                ProductTypeUtility.setLstUserFocusDto(focusMasterList);
                activity.finish();
                break;
            case R.id.tv_edit_focus:
                activity.switchAddFocus();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//            ProductTypeUtility.setLstUserFocusDto(focusMasterList);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), OrderDeatilActivity.class);
        intent.putExtra(ZoneRequestDownEntity.class.getName(), JsonUtility.toJSONString(focusMasterList.get(position - 1)));
        startActivity(intent);
    }
}
