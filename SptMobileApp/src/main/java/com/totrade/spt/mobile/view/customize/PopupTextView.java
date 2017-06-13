package com.totrade.spt.mobile.view.customize;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.autrade.spt.common.entity.NameValueItem;
import com.autrade.stage.utility.CollectionUtility;
import com.totrade.spt.mobile.adapter.NameValueAdapter;
import com.autrade.spt.common.entity.NameValueItem;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.OnListItemClickListener;

public class PopupTextView extends LinearLayout implements OnClickListener {
    private NameValueItem item;
    private PopupWindow popupWindow;
    private List<NameValueItem> listDate;
    private Context context;
    private String title = "";
    private OnTextChangerListener listener;
    private OnTextChangerReturnPreviousListener previousListener;
    private View viewPop;
    private TextView lblName;
    private TextView lblValue;
    private ImageView imgMore;
    private int gravity = Gravity.BOTTOM;
    private NameValueAdapter adapter;
    Window window;

    /**
     * 带上次值
     */
    public void setOnTextChangerReturnPreviousListener(OnTextChangerReturnPreviousListener previousListener) {
        this.previousListener = previousListener;
    }

    public void setListener(OnTextChangerListener listener) {
        this.listener = listener;
    }

    public PopupTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    public PopupTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public PopupTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }


    public String getValue() {
        if (item == null) return "";
        return item.getValue();
    }

    public String getName() {
        if (item == null) return "";
        return item.getName();
    }

    private void initView() {
        View viewThis = LayoutInflater.from(context).inflate(R.layout.selectitemview, null);
        lblValue = (TextView) viewThis.findViewById(R.id.lblSelectView);
        lblName = (TextView) viewThis.findViewById(R.id.lblName);
        imgMore = (ImageView) viewThis.findViewById(R.id.imgMore);
        viewThis.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(viewThis);
        setOnClickListener(this);
    }

    public List<NameValueItem> getListDate() {
        return listDate;
    }


    /**
     * 比较新的列表中是否有之前的选择值
     */
    private boolean hasSameValue(List<NameValueItem> lst) {
        boolean lstEmpty = CollectionUtility.isNullOrEmpty(lst);
        if (this.item == null || lstEmpty) {
            return this.item == null && lstEmpty;
        }
        for (NameValueItem item : lst) {
            if (nameValueEquals(this.item, item)) {
                return true;
            }
        }
        return false;

    }

    private void setListDate(List<NameValueItem> listDate) {
        this.listDate = listDate;
        imgMore.setVisibility(View.GONE);

        if (listDate != null && listDate.size() > 1) {
            imgMore.setVisibility(View.VISIBLE);
        }

        //如果列表值包含当前值，则不会重新设置当前的值
        if (!hasSameValue(listDate)) {
            if (CollectionUtility.isNullOrEmpty(listDate)) {
                this.setViewTag(null);
                return;
            }
            this.setViewTag(listDate.get(0));
        }
    }

    public void initDate(List<NameValueItem> listDate, String title, int gravity) {
        this.gravity = gravity;
        initDate(listDate, title);
    }

    /**
     * 设置值
     *
     * @param listDate pop关联的List
     * @param title    弹出的pop标题
     */
    public void initDate(List<NameValueItem> listDate, String title) {
        this.title = title;
        lblName.setText(title);
        setListDate(listDate);
        popupWindow = null;        //当数据发生变化时，重新生成pop
        if (CollectionUtility.isNullOrEmpty(listDate)) {
            return;
        }

        if (gravity == Gravity.RIGHT || gravity == Gravity.LEFT || gravity == Gravity.START || gravity == Gravity.END) {
            viewPop = LayoutInflater.from(context).inflate(R.layout.pop_right, null, false);
        } else if (gravity == Gravity.FILL) {
            viewPop = LayoutInflater.from(context).inflate(R.layout.popfullselect, null, false);
        } else {
            viewPop = LayoutInflater.from(context).inflate(R.layout.pop_bottom, null, false);
        }
        if (gravity == Gravity.FILL || gravity == Gravity.LEFT || gravity == Gravity.RIGHT) {
            viewPop.findViewById(R.id.imgBack).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
            });
        }
    }

    public NameValueItem getViewTag() {
        return item;
    }

    public void setTitle(String title) {
        if (title == null) title = "";
        this.title = title;
        lblName.setText(title);
    }

    public void setViewTag(NameValueItem item) {
        if (nameValueEquals(this.item, item))
            return;
        NameValueItem t2 = this.item;
        this.item = item;
        lblValue.setText("");
        if (item != null && getName() != null) {
            lblValue.setText(getName());
        }
        if (listener != null) {
            listener.textChanger(PopupTextView.this);
        }
        if (previousListener != null) {
            previousListener.textChangerReturnPrevious(this, t2);
        }

        if (adapter != null) {
            adapter.setSelectItem(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (listDate == null || listDate.size() == 1) {
            return;
        }
        if (window == null) window = ((Activity) context).getWindow();
        try {
            // 隐藏键盘
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            //
        }
        if (popupWindow == null) {
            final ListView listView = (ListView) viewPop.findViewById(R.id.lvPopBottomCommon);
            ((TextView) viewPop.findViewById(R.id.lblPopBottomCommon)).setText(title);
            adapter = new NameValueAdapter(context, listDate);
            adapter.setSelectItem(item);
            listView.setAdapter(adapter);
            if (gravity == Gravity.BOTTOM) {
                popupWindow = new PopupWindow(viewPop, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            } else if (gravity == Gravity.FILL) {
                popupWindow = new PopupWindow(viewPop, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            } else {
                popupWindow = new PopupWindow(viewPop, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, true);
            }

            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(true);
            adapter.setOnListItemClickListener(new OnListItemClickListener() {
                @Override
                public void onItemClick(View v, Object data) {
                    popupWindow.dismiss();
                    NameValueItem t1 = (NameValueItem) listView.getItemAtPosition((Integer) data);
                    if (!nameValueEquals(item, t1)) {
                        setViewTag(t1);
                        adapter.setSelectItem(t1);
                    }
                }
            });
            popupWindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.alpha = 1f;
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setAttributes(lp);
                }
            });
        }

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(lp);
        popupWindow.showAtLocation(this, gravity, 0, 0);
    }

    public interface OnTextChangerListener {
        void textChanger(PopupTextView view);
    }

    public interface OnTextChangerReturnPreviousListener {
        void textChangerReturnPrevious(PopupTextView view, NameValueItem t);
    }


    public boolean nameValueEquals(NameValueItem o1, NameValueItem o2) {
        if (o1 == null || o2 == null) return false;
        String name = o1.getName();
        String value = o1.getValue();
        Object tag = o1.getTag();
        boolean nameEquals = (name == null && o2.getName() == null) || (name != null && name.equals(o2.getName()));
        boolean valueEquals = (value == null && o2.getValue() == null) || (value != null && value.equals(o2.getValue()));
        boolean tagEquals = (tag == null && o2.getTag() == null) || (tag != null && o2.getTag() != null && tag.equals(o2.getTag()));
        return nameEquals && valueEquals && tagEquals;
    }
}
