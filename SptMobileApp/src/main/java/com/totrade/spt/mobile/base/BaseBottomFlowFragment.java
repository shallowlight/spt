package com.totrade.spt.mobile.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.customize.FlowTagLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部标签云弹出Dialog
 * Created by Timothy on 2017/4/13.
 */

public class BaseBottomFlowFragment extends DialogFragment {

    private TextView tv_title;
    private FlowTagLayout flow_tag_layout;
    private TextView tv_cancel;

    private BottomFlowPopupAdapter adapter;
    private Dialog dialog;

    public BaseBottomFlowFragment() {
    }

    public static BaseBottomFlowFragment newInstance(String title, ArrayList<String> list, int position) {
        BaseBottomFlowFragment baseBottomFlowFragment = new BaseBottomFlowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putSerializable("list", list);
        bundle.putInt("initSelect", position);
        baseBottomFlowFragment.setArguments(bundle);

        return baseBottomFlowFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.base_fragment_bottom_flow, null);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true);
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tv_title = (TextView) rootView.findViewById(R.id.title);
        flow_tag_layout = (FlowTagLayout) rootView.findViewById(R.id.flow_tag_layout);
        tv_cancel = (TextView) rootView.findViewById(R.id.tv_cancel);

        tv_title.setText(getArguments().getString("title"));
        adapter = new BottomFlowPopupAdapter(getActivity());

        flow_tag_layout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        flow_tag_layout.setOnTagSelectListener(tagSelectListener);
        flow_tag_layout.setAdapter(adapter);
        adapter.refreshData((List<String>) getArguments().getSerializable("list"));

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    class BottomFlowPopupAdapter extends AdapterBase<String> implements FlowTagLayout.OnInitSelectedPosition {

        public BottomFlowPopupAdapter(Context context) {
            super(context);
            flow_tag_layout.setOnInitSelectedPosition(this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_textview, parent, false);
            AppCompatCheckedTextView tag = (AppCompatCheckedTextView) view.findViewById(R.id.tag);
            tag.setText(getItem(position));
            tag.setChecked(flow_tag_layout.getCheckedArray().get(position));
            return view;
        }

        @Override
        public boolean isSelectedPosition(int position) {
            return position == getArguments().getInt("initSelect");
        }
    }

    private FlowTagLayout.OnTagSelectListener tagSelectListener = new FlowTagLayout.OnTagSelectListener() {
        @Override
        public void onItemSelect(FlowTagLayout parent, View childView, final int position, List<Integer> selectedList, List<Boolean> checkBooleanArray) {
            for (View view : flow_tag_layout.getChildViews()) {
                ((AppCompatCheckedTextView) view.findViewById(R.id.tag)).setChecked(view.isSelected());
            }
            final AppCompatCheckedTextView textView = (AppCompatCheckedTextView) childView.findViewById(R.id.tag);
            if (textView.isSelected()) {
                if (null != onTagSelectListener) {
                    onTagSelectListener.getSelectText(textView.getText().toString(), position);
                }
            }
            if (dialog.isShowing()){
                dismiss();
            }
        }
    };

    private OnTagSelectListener onTagSelectListener;

    public void setOnTagSelectListener(OnTagSelectListener listener) {
        this.onTagSelectListener = listener;
    }

    public interface OnTagSelectListener {
        void getSelectText(String text, int position);
    }
}
