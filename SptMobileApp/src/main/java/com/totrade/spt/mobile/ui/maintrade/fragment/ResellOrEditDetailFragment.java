package com.totrade.spt.mobile.ui.maintrade.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.view.R;

/**
 * Created by Administrator on 2017/4/12.
 */

public class ResellOrEditDetailFragment extends BaseFragment {

    private View rootView;
    private Activity activity;

    public ResellOrEditDetailFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_trade_resell_edit, container, false);

        return rootView;
    }
}
