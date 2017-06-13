package com.totrade.spt.mobile.view.customize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.autrade.stage.utility.DateUtility;
import com.totrade.spt.mobile.utility.FormatUtil;
import com.totrade.spt.mobile.view.R;


/**
 * @author Administrator
 *
 */

public class RefreshAndLoadListView extends ListView implements OnScrollListener
{

	public static final int REFRESH = 0;
	public static final int LOAD = 1;
	public View header;
	private int headerHeight;
	public View footer;//底部布局
	private int totalItemCount;
	private int lastVisibleItem;
	private boolean isRequesting=false;//正在请求数据
	private ILoadListener loadListener;
	private boolean isFullLoad;
	private int firstVisibleItem;
	private boolean isRemark;// 标记listView在最顶端时按下
	private int startY;// 按下时候的Y值
	private int state;
	private static final int NONE = 0;// 正常状态
	private static final int PULL = 1;// 提示下拉状态
	private static final int RELESE = 2;// 松开释放的状态
	private static final int REFLASHING = 3;// 刷新状态
	private static int LAST_STATE=NONE;
	private IReflashListener reflashListener;//刷新数据的接口

	public RefreshAndLoadListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public RefreshAndLoadListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public RefreshAndLoadListView(Context context)
	{
		super(context);
		initView(context);
	}

	//初始化界面，添加顶部布局
	private void initView(Context context)
	{

		if(this.getFooterViewsCount()<=0)
		{
			footer=LayoutInflater.from(context).inflate(R.layout.footer_layout,null);
			footer.findViewById(R.id.lblNoData).setVisibility(View.VISIBLE);
			this.addFooterView(footer);
			this.setOnScrollListener(this);
		}
		this.setFooterDividersEnabled(false);
		this.setHeaderDividersEnabled(false);
		this.setOnScrollListener(this);
	}
	//设置header的上边距
	private void topPadding(int topPadding)
	{
		header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	//通知父布局占用的高度和宽度
	private void measureView(View view)
	{
		ViewGroup.LayoutParams p=view.getLayoutParams();
		if(p==null)
		{
			p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int width=ViewGroup.getChildMeasureSpec(0,0,p.width);
		int height;
		int tempHeight=p.height;
		if(tempHeight>0)
		{
			height=MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
		}
		else
		{
			height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount)
	{
		this.firstVisibleItem = firstVisibleItem;
		this.totalItemCount=totalItemCount;
		this.lastVisibleItem = firstVisibleItem+visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		if(totalItemCount==lastVisibleItem )
		{
			if(!isRequesting && !isFullLoad){
				isRequesting=true;
				footer.findViewById(R.id.loadLayout).setVisibility(View.VISIBLE);
				footer.setVisibility(View.VISIBLE);
				//加载更多数据
				loadListener.onLoad();
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if(isRequesting)
		{
			return super.onTouchEvent(ev);
		}
		switch (ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				LAST_STATE=NONE;
				if(firstVisibleItem==0)
				{
					isRemark=true;
					startY=(int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				onMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				LAST_STATE=NONE;
				if(state==RELESE)
				{
					state=REFLASHING;
					//加载最新数据
					reflashViewByState();
					reflashListener.onRefresh();
				}
				else if(state==PULL)
				{
					state=NONE;
					isRemark=false;
					reflashViewByState();
				}
				break;
			default:
				break;
		}
		return super.onTouchEvent(ev);
	}
	//判断移动过程中的操作
	private void onMove(MotionEvent ev)
	{
		if(!isRemark)
		{
			return;
		}
		int tempY = (int) ev.getY();
		int space = tempY - startY;
		int topPadding = (space - headerHeight)/3;
		switch (state)
		{
			case NONE:
				LAST_STATE=NONE;
				if (space > 0)
				{
					state = PULL;
					reflashViewByState();
				}
				break;
			case PULL:
				LAST_STATE=PULL;
				topPadding(topPadding);
				if (space > headerHeight+30 && state==SCROLL_STATE_TOUCH_SCROLL)
				{
					state = RELESE;
					reflashViewByState();
				}
				break;
			case RELESE:
				LAST_STATE=RELESE;
				topPadding(topPadding);
				if (space < headerHeight) {
					state = PULL;
					reflashViewByState();
				} else if (space <= 0) {
					state = NONE;
					isRemark = false;
					reflashViewByState();
				}
				break;
			case REFLASHING:

				break;

			default:
				break;
		}
	}
	// 根据当前状态改变界面显示
	private void reflashViewByState(){
		TextView lblTip = (TextView) header.findViewById(R.id.tip);
		ImageView imgArrow = (ImageView) header.findViewById(R.id.arrow);
		ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.progress);
		//箭头动画效果
		RotateAnimation animation = new RotateAnimation(0, 180,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500);
		animation.setFillAfter(true);
		RotateAnimation animation2 = new RotateAnimation(180, 1,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation2.setDuration(500);
		animation2.setFillAfter(true);
		switch (state) {
			case NONE:
				imgArrow.clearAnimation();
				topPadding(-headerHeight);
				break;
			case PULL:
				imgArrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				lblTip.setText("下拉可以刷新");
				if(LAST_STATE!=NONE)
				{
					imgArrow.clearAnimation();
					imgArrow.setAnimation(animation2);
				}
				break;
			case RELESE:
				imgArrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				lblTip.setText("松开可以刷新");
				imgArrow.clearAnimation();
				imgArrow.setAnimation(animation);
				break;
			case REFLASHING:
				topPadding(50);
				imgArrow.clearAnimation();
				imgArrow.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				lblTip.setText("正在刷新");
				break;

			default:
				break;
		}
	}

	public void setLoadListener(ILoadListener loadListener)
	{
		this.loadListener=loadListener;
	}

	//加载更多数据回调接口
	public interface ILoadListener
	{
		void onLoad();
	}

	public void setReflashListener(IReflashListener reflashListener)
	{
		this.reflashListener=reflashListener;
		if(this.getHeaderViewsCount()==0)
		{
			header=LayoutInflater.from(getContext()).inflate(R.layout.header_layout,null);
			measureView(header);
			headerHeight=header.getMeasuredHeight();
			topPadding(-headerHeight);
			this.addHeaderView(header);
		}
	}

	public interface IReflashListener
	{
		void onRefresh();
	}

	public void setRequestResult(boolean isRefresh,boolean requestSuccess,boolean isFullLoad,boolean resultListIsEmpty)
	{
		TextView textView = (TextView) footer.findViewById(R.id.lblNoData);
		textView.setVisibility(resultListIsEmpty?View.VISIBLE:View.GONE);
		if(resultListIsEmpty)
		{
			footer.setVisibility(View.VISIBLE);
		}

		this.isFullLoad=isFullLoad;
		isRequesting=false;
		footer.findViewById(R.id.loadLayout).setVisibility(View.GONE);
		if(isRefresh)
		{
			state=NONE;
			isRemark=false;
			reflashViewByState();
			if(requestSuccess)
			{
				String time=FormatUtil.date2String(new Date(), "HH:mm:ss");
				((TextView) header.findViewById(R.id.lastUpdateTime)).setText("上次刷新时间"+time);
			}
		}
	}

	/**
	 *
	 * @param refreshOrLoad			标记是刷新还是加载，
	 * @param requestSuccess		请求数据是否成功（抛异常）
	 * @param isFullLoad			是否获取到所有数据
	 * @param listIsEmpty			最终列表是否为空
	 */
	public void setRequestResult(int refreshOrLoad,boolean requestSuccess,boolean isFullLoad,boolean listIsEmpty,String nodata)
	{
		TextView textView = (TextView) footer.findViewById(R.id.lblNoData);
		textView.setVisibility(listIsEmpty?View.VISIBLE:View.GONE);
		if(nodata!=null)
		{
			textView.setText(nodata);
		}
		this.isFullLoad=isFullLoad;
		isRequesting=false;
		footer.findViewById(R.id.loadLayout).setVisibility(View.GONE);
		state=NONE;
		isRemark=false;
		reflashViewByState();
		if(refreshOrLoad==REFRESH)
		{
			if(requestSuccess)
			{
				SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
				String time=dateFormat.format(DateUtility.getDate());
				((TextView) header.findViewById(R.id.lastUpdateTime)).setText("上次刷新时间"+time);
			}
		}
	}
}
