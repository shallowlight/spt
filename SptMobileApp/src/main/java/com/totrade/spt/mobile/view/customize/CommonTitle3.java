package com.totrade.spt.mobile.view.customize;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.view.R;

public class CommonTitle3 extends LinearLayout
{
	private ImageView imgBack;
	private TextView lblMid;
	private ImageView imgRight;
	private ImageView imgRight2;
	private TextView lblTitleSub;
	private TextView lblTitleRight;

	public CommonTitle3(Context context)
	{
		super(context);
		initView(context);
	}

	public ImageView getImgBack()
	{
		return imgBack;
	}

	public TextView getTitleRight()
	{
		return lblTitleRight;
	}

	public CommonTitle3(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public CommonTitle3(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	private void initView(final Context context)
	{
		if (isInEditMode())
		{
			return;
		}
		View title = LayoutInflater.from(context).inflate(R.layout.title_second, null);
		lblMid = (TextView) title.findViewById(R.id.lblTitle);
//		((SptMobileActivityBase)context).isChild()
		if (getTag() != null)
		{
			lblMid.setText(this.getTag().toString());
		}
		else if(context instanceof Activity)
		{
			lblMid.setText(((Activity)context).getTitle());
		}
		imgBack = (ImageView) title.findViewById(R.id.imgBack);
		imgRight = (ImageView) title.findViewById(R.id.imgRight);
		imgRight2 = (ImageView) title.findViewById(R.id.imgRight2);
		lblTitleSub = (TextView) title.findViewById(R.id.lblTitleSub);
		lblTitleRight = (TextView) title.findViewById(R.id.lblTitleRight);
		imgBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				((Activity) v.getContext()).finish();
			}

		});
		title.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(title);
	}

	public void setTitle(String str)
	{
		if (str != null)
			lblMid.setText(str);
	}

	// public void setTitleSub(String str)
	// {
	// if(!StringUtility.isNullOrEmpty(str))
	// {
	// lblTitleSub.setText(str);
	// }
	// }
	public void setTitleSubFormat(String str)
	{
		if (!StringUtility.isNullOrEmpty(str))
		{
			str = "-(" + str + ")";
			lblTitleSub.setText(str);
		}
	}

	public ImageView getTitleRightImg(int res)
	{
		lblTitleRight.setVisibility(View.INVISIBLE);
		imgRight.setVisibility(View.VISIBLE);
		imgRight.setImageResource(res);
		return imgRight;
	}

	public ImageView getTitleRight2Img(int res)
	{
		imgRight2.setVisibility(View.VISIBLE);
		imgRight2.setImageResource(res);
		return imgRight2;
	}

	public TextView getTitleRightLbl(String str)
	{
		imgRight.setVisibility(View.INVISIBLE);
		lblTitleRight.setVisibility(View.VISIBLE);
		lblTitleRight.setText(str);
		return lblTitleRight;
	}

}
