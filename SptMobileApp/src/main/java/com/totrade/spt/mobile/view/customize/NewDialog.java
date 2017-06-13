package com.totrade.spt.mobile.view.customize;

import com.totrade.spt.mobile.view.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * 黑色背景，垂直居中
 * @author Administrator
 *
 */
@Deprecated
public class NewDialog extends Dialog
{

	public NewDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public NewDialog(Context context)
	{
		super(context);
	}
	/**
	 * 弹出自定义ProgressDialog
	 *
	 * @param context
	 *            上下文
	 * @param message
	 *            提示内容
	 * @param cancelable
	 *            是否按返回键取消
	 * @param cancelListener
	 *            按下返回键监听
	 * @return
	 */
	public static NewDialog create(Context context, CharSequence message,
								   boolean cancelable, OnCancelListener cancelListener)
	{
		NewDialog dialog = new NewDialog(context, R.style.Custom_Progress);
		dialog.setContentView(R.layout.dialog_layout);
		if (message == null || message.length() == 0)
		{
			dialog.findViewById(R.id.lblMessage).setVisibility(View.GONE);
		}
		else
		{
			TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
			lblMessage.setText(message);
		}
		// 按返回键是否取消
		dialog.setCancelable(cancelable);
		// 监听返回键处理
		dialog.setOnCancelListener(cancelListener);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
//		dialog.show();
		return dialog;
	}
}
