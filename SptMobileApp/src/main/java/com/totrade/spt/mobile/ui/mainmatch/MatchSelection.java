package com.totrade.spt.mobile.ui.mainmatch;

import android.graphics.Rect;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
*
* 撮合集市页面-筛选监听
* @author huangxy
* @date 2017/4/14
*
*/
public class MatchSelection {

    private List<View> viewList;

    public void setView(LinearLayout linearLayout) {
        viewList = new ArrayList<>();
        linearLayout.setOnTouchListener(new SelcetionTouchListener());

        TextView tv_name = (TextView) linearLayout.findViewById(R.id.tv_name);
        tv_name.setText("品名");

        TextView tv_delivery_time = (TextView) linearLayout.findViewById(R.id.tv_delivery_time);
        tv_delivery_time.setText("交收期");

        TextView tv_trade_mode = (TextView) linearLayout.findViewById(R.id.tv_trade_mode);
        tv_trade_mode.setText("销售/采购");
        tv_trade_mode.setTag(1);

        viewList.add(tv_name);
        viewList.add(tv_delivery_time);
        viewList.add(tv_trade_mode);
    }

    public void clearSelect() {
        for (View v : viewList) {
            ((View) v.getParent()).setSelected(false);
        }
    }

    class SelcetionTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getRawX(); // 获取相对于屏幕左上角的 x 坐标值
                int y = (int) event.getRawY(); // 获取相对于屏幕左上角的 y 坐标值

                for (int i = 0; i < viewList.size(); i++) {
                    View view = viewList.get(i);
                    if (isTouched(view, x, y)) {
                        if (i == 2) {
                            Integer tag = (Integer) view.getTag();
                            if (tag == 1) {
                                String source1 = "销售/<font color=\"#30a9de\">采购</font>";
                                ((TextView) view).setText(Html.fromHtml(source1));
                                view.setTag(2);

                                setChoose(3);
                            } else if (tag == 2) {
                                String source2 = "<font color=\"#30a9de\">销售</font>/采购";
                                ((TextView) view).setText(Html.fromHtml(source2));
                                view.setTag(1);

                                setChoose(4);
                            }
                        } else {
                            ((TextView) viewList.get(2)).setText(Html.fromHtml("销售/采购"));
                            ((View) viewList.get(i).getParent()).setSelected(true);

                            setChoose(i + 1);
                        }
                    } else {
                        ((View) viewList.get(i).getParent()).setSelected(false);
                    }
                }
            }
            return true;
        }
    }

    /**
     * 是否在View的范围内
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    private static boolean isTouched(View v, int x, int y) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        Rect rect = new Rect(location[0], location[1], location[0] + v.getWidth(), location[1] + v.getHeight());
        return rect.contains(x, y);
    }

    private void setChoose(int i) {
        if (onSelcetionListener != null) {
            onSelcetionListener.selction(i);
        }
    }

    private OnSelcetionListener onSelcetionListener;

    public interface OnSelcetionListener {
        void selction(int code);
    }

    public void setOnSelcetionListener(OnSelcetionListener onSelcetionListener) {
        this.onSelcetionListener = onSelcetionListener;
    }

}
