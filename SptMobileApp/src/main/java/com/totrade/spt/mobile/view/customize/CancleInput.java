package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.view.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CancleInput {
    public static void cancleInput(Context context, EditText target) {
        new InputCancleView(context, target);
    }

    public static void cancleInput(Context context, EditText... targets) {
        for (EditText target : targets) {
            new InputCancleView(context, target);
        }
    }


    static class InputCancleView extends FrameLayout {
        private ImageView ivCancle;

        public InputCancleView(Context context, AttributeSet attrs, int defStyleAttr, EditText target) {
            super(context, attrs, defStyleAttr);
            initView(target);
        }

        public InputCancleView(Context context, AttributeSet attrs, EditText target) {
            super(context, attrs);
            initView(target);
        }

        public InputCancleView(Context context, EditText target) {
            super(context);
            initView(target);
        }

        private void initView(EditText target) {
            ivCancle = new ImageView(getContext());
            ivCancle.setImageResource(R.drawable.cancle_input_gray);
            ivCancle.setVisibility(View.GONE);

            setTargetView(target);
        }

        /**
         * 设置目标EdittText
         *
         * @param target
         */
        private void setTargetView(EditText target) {
            if (target == null) {
                return;
            }
            if (target.getParent() instanceof ViewGroup) {

                ViewGroup parentContainer = (ViewGroup) target.getParent();
                int groupIndex = parentContainer.indexOfChild(target);
                parentContainer.removeView(target);

                ViewGroup.LayoutParams parentLayoutParams = target.getLayoutParams();

                setLayoutParams(parentLayoutParams);
                parentContainer.addView(this, groupIndex, parentLayoutParams);

                addView(target);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dip2Px(15), dip2Px(15));
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                ivCancle.setLayoutParams(params);
                addView(ivCancle);

            } else if (target.getParent() == null) {
                Log.e(getClass().getSimpleName(), "ParentView is needed");
            }

            cancleInput(target, ivCancle);
        }

        /**
         * 取消输入
         *
         * @param editText
         * @param view
         */
        private void cancleInput(final EditText editText, final View view) {

            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    view.setVisibility((s != null && s.toString().length() > 0) ? View.VISIBLE : View.GONE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    editText.setText("");
                }
            });
        }

        /*
         * converts dip to px
         */
        private int dip2Px(float dip) {
            return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
        }

    }
}
