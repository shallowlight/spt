package com.totrade.spt.mobile.view.customize;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.view.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        public Button positiveButton;
        public Button negativeButton;
        private CheckBox box;
        private Context context;
        private String title;
        private String message;
        private String checkBoxStr;        //提交服务器数据带选择提示使用
        private boolean isChecked;
        private String positiveButtonText;
        private String negativeButtonText = "取消";
        private View contentView;
        private boolean isNegative = true;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private boolean dismissDialogOnClickPositive = true;

        public boolean checkBoxChecked() {
            return box != null && box.isChecked();
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context, String title, String message) {
            this.context = context;
            this.title = title;
            this.message = message;
        }

        public Builder(Context context, String message) {
            this.context = context;
            this.message = message;
        }

        public Builder(Context context, String message, String chekedMsg, boolean checked) {
            this.context = context;
            this.message = message;
            this.checkBoxStr = chekedMsg;
            this.isChecked = checked;
        }

        public Builder isNega(boolean isNega) {
            isNegative = isNega;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveClickListener(DialogInterface.OnClickListener listener) {
            if (TextUtils.isEmpty(positiveButtonText)) {
                positiveButtonText = "确定";
            }
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public void setPositiveButtonCancel(boolean cancel) {
            dismissDialogOnClickPositive = cancel;
        }

        public CustomDialog create() {
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = LayoutInflater.from(context).inflate(R.layout.dialog_normal_layout, null);
            positiveButton = (Button) layout.findViewById(R.id.positiveButton);
            negativeButton = (Button) layout.findViewById(R.id.negativeButton);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (!StringUtility.isNullOrEmpty(title)) {
                layout.findViewById(R.id.title).setVisibility(View.VISIBLE);
                ((TextView) layout.findViewById(R.id.title)).setText(title);
            }
            if (!StringUtility.isNullOrEmpty(checkBoxStr)) {
                box = (CheckBox) layout.findViewById(R.id.chkBox);
                box.setChecked(isChecked);
                box.setText(checkBoxStr);
                box.setVisibility(View.VISIBLE);
            }
            if (positiveButtonText != null)    //积极的
            {
                positiveButton.setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                            if (dismissDialogOnClickPositive) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            } else {
                positiveButton.setVisibility(View.GONE);
            }

            if (negativeButtonClickListener == null)    //设置取消按钮默认点击
            {
                negativeButtonClickListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
            }
            if (!isNegative) {
                negativeButton.setVisibility(isNegative ? View.VISIBLE : View.GONE);
                positiveButton.setBackgroundResource(R.drawable.white_button_background_bottom);
            }
            //设置取消按钮点击事件
            if (negativeButtonClickListener != null) {
                negativeButton.setText(negativeButtonText);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            } else {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
                if (message != null && message.length() > 40) {
                    ((TextView) layout.findViewById(R.id.message)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                }
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
