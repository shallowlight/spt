package com.totrade.spt.mobile.base;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.totrade.spt.mobile.view.R;

/**
 * 平移动画Activity基类
 *
 * @author huangxy
 *
 */
public abstract class AnimationBaseActivity extends SptMobileActivityBase implements OnClickListener
{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.activity_anim_base, null);
		ImageView imgTitleLeft = (ImageView) view.findViewById(R.id.imgTitleLeft);
		TextView lblTitleMid = (TextView) view.findViewById(R.id.lblTitleMid);
		lblTitleMid.setText(setTile());
		imgTitleLeft.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
				overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
			}
		});
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
		linearLayout.addView(view);
		linearLayout.addView(initView(), params);
		this.addContentView(linearLayout, params);
		initData();
	}


	//	初始化数据
	public abstract void initData();
	//	初始化View
	public abstract View initView();
	//	设置标题
	public abstract String setTile();
}
