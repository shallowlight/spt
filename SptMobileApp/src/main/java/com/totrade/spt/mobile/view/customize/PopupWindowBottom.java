package com.totrade.spt.mobile.view.customize;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindowBottom extends PopupWindow
{
	private Window window;

	public PopupWindowBottom()
	{
		
	}
	
	public PopupWindowBottom(View view)
	{
		creatView(view);
	}

	public void creatView(View view)
	{
		window = ((Activity) view.getContext()).getWindow();
		setContentView(view);
		setOutsideTouchable(true);
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setOnclickLisener(view);
	}

	public void setDismissPopView(View view)
	{
		view.setOnClickListener(dismissListener);
	}
	
//	public void setDismissPopView(int viewId)
//	{
//		getContentView().findViewById(viewId).setOnClickListener(dismissListener);
//	}
	
	private View.OnClickListener dismissListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			dismiss();
		}
	};
	
	
	private void setOnclickLisener(View view)
	{
		ViewGroup vg = (ViewGroup) view;
		for (int i = 0; i < vg.getChildCount(); i++)
		{
			if (vg.getChildAt(i) instanceof ViewGroup)
			{
				setOnclickLisener(vg.getChildAt(i));
			} else
			{
				vg.getChildAt(i).setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (clickViewListener != null)
						{
							clickViewListener.onClickView(v);
						}
					}
				});
			}
		}
	}

	private OnClickViewListener clickViewListener; 

	public void setOnClickViewListener(OnClickViewListener clickViewListener)
	{
		this.clickViewListener = clickViewListener;
	}

	public interface OnClickViewListener
	{
		void onClickView(View v);
	}

	@Override
	public void dismiss()
	{
		super.dismiss();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 1f;
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setAttributes(lp);
	}

	public void showAtBottom()
	{
		super.showAtLocation(window.getDecorView(), Gravity.BOTTOM, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.5f;
		int flag = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.addFlags(flag);
		window.setAttributes(lp);
	}
}
