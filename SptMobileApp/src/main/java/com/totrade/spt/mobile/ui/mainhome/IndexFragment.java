package com.totrade.spt.mobile.ui.mainhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.spt.common.constants.SptConstant;
import com.autrade.spt.master.dto.ProductTypeDto;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.ui.maintrade.IndicatorHelper;
import com.totrade.spt.mobile.utility.DensityUtil;
import com.totrade.spt.mobile.utility.ProductTypeUtility;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.Indicator;

import java.util.List;

/**
 * Created by Timothy on 2017/4/5.
 */

public class IndexFragment extends BaseFragment {

    private LinearLayout view;
    private LineChatFragment lineChatFragment;
    private FragmentActivity activity;
    private TabLayout tabL;
    private List<ProductTypeDto> levels;

    public IndexFragment() {
        setContainerId(R.id.fl_chat);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        view = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_index, container, false);
        tabL = (TabLayout) view.findViewById(R.id.tabLayout);

        levels = ProductTypeUtility.getListProductByParentAndLevel(SptConstant.BUSINESS_ZONE, null, 2);
        if (levels != null) {
            for (int i = 0; i < levels.size(); i++) {
                tabL.addTab(tabL.newTab().setText(levels.get(i).getTypeName()).setTag(levels.get(i)));
            }
            tabL.setTabMode(TabLayout.MODE_FIXED);
            tabL.setOnTabSelectedListener(new SelectedListener());
            lineChatFragment = LineChatFragment.newInstance(levels.get(0).getProductType());
            getChildFragmentManager().beginTransaction().add(R.id.fl_index_chat, lineChatFragment).commitAllowingStateLoss();
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
    }

    class SelectedListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            ProductTypeDto dto = (ProductTypeDto) tab.getTag();
            lineChatFragment.refreshLineChat(dto.getProductType());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
