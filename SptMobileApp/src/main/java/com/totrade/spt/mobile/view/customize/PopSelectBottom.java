package com.totrade.spt.mobile.view.customize;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.totrade.spt.mobile.base.AdapterBase;
import com.totrade.spt.mobile.view.R;

import java.util.List;

/**
 * Created by Timothy on 2017/1/13.
 */
public class PopSelectBottom extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Window window;
    private View view;
    private Context mContext;
    private TextView confirm;
    private TextView cancel;
    private TextView title;
    private ListView listView;
    private PopSelectAdapter mAdapter;

    public PopSelectBottom(Context context) {
        super(context);
        mContext = context;
        initView();
        initSet();
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.view_pop_select_bottom, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        confirm = (TextView) view.findViewById(R.id.confirm);
        title = (TextView) view.findViewById(R.id.title);
        listView = (ListView) view.findViewById(R.id.listView);
    }

    private void initSet() {
        listView.setOnItemClickListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        window = ((Activity) view.getContext()).getWindow();
        setContentView(view);
        setOutsideTouchable(true);
        setFocusable(true);
        setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showAtBottom() {
        super.showAtLocation(window.getDecorView(), Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5f;
        int flag = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.addFlags(flag);
        window.setAttributes(lp);
    }

    public <T> void setData(List<T> data) {
        mAdapter = new PopSelectAdapter(mContext);
        listView.setAdapter(mAdapter);
        mAdapter.refreshData(data);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mOnOnTextForItemCallBack.getSelectItem(mAdapter.getItem(position), position);
        dismiss();
    }

    private OnTextForItemCallBack mOnOnTextForItemCallBack;

    public interface OnTextForItemCallBack<T> {

        void getSelectItem(T object, int position);

        void loadItemData(T t, TextView view);
    }

    public void setOnTextForItemCallBack(OnTextForItemCallBack callBack) {
        mOnOnTextForItemCallBack = callBack;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.3f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.confirm:
                break;
        }
    }

    class PopSelectAdapter<T> extends AdapterBase {

        public PopSelectAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            view.setTextColor(mContext.getResources().getColor(R.color.ltBlack));
            view.setTextSize(16);
            view.setGravity(Gravity.CENTER);
            T t = (T) getItem(position);
            if (t instanceof String) {
                view.setText((String) t);
            } else {
                if (null != mOnOnTextForItemCallBack)
                    mOnOnTextForItemCallBack.loadItemData(t, view);
                else view.setText("请注册 OnTextForItemCallBack 回调自定义显示数据");
            }
            return view;
        }
    }

}
