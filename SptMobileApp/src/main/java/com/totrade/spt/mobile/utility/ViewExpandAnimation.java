package com.totrade.spt.mobile.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ViewExpandAnimation extends Animation {

	/*************************** 自定义动画 **************************/

	private View mAnimationView = null;
	private LayoutParams mViewLayoutParams = null;
	private int mStart = 0;
	private int mEnd = 0;

	public ViewExpandAnimation() {
	}

	/**
	 * 构造
	 *
	 * @param view
	 *            指定view设置动画
	 */
	public ViewExpandAnimation(View view) {
		animationSettings(view, 200);
	}

	/**
	 * 构造
	 *
	 * @param view
	 *            指定view设置动画
	 * @param duration
	 *            设置动画时长
	 */
	public ViewExpandAnimation(View view, int duration) {
		animationSettings(view, duration);
	}

	private void animationSettings(View view, int duration) {
		setDuration(duration);
		mAnimationView = view;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();
		mStart = mViewLayoutParams.bottomMargin;
		mEnd = (mStart == 0 ? (0 - view.getHeight()) : 0);
		view.setVisibility(View.VISIBLE);
		view.startAnimation(this);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			mViewLayoutParams.bottomMargin = mStart + (int) ((mEnd - mStart) * interpolatedTime);
			// invalidate
			mAnimationView.requestLayout();
		} else {
			mViewLayoutParams.bottomMargin = mEnd;
			mAnimationView.requestLayout();
			if (mEnd != 0) {
				mAnimationView.setVisibility(View.GONE);
			}
		}
	}

	/*************************** 系统动画 **************************/
	private int valueHeight;

	public boolean audioLayout(View view, int valueHeight) {
		this.valueHeight = valueHeight;
		if (view.getVisibility() == View.GONE) {
			// 打开动画
			animateOpen(view);
			return true;
		} else {
			// 关闭动画
			animateClose(view);
			return false;
		}
	}

	public void animateOpen(final View view) {
		view.setVisibility(View.VISIBLE);
		ValueAnimator animator = createDropAnimator(view, 0, valueHeight);
		animator.start();
	}

	public void animateClose(final View view) {
		int origHeight = view.getHeight();
		ValueAnimator animator = createDropAnimator(view, origHeight, 0);
		animator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				view.setVisibility(View.GONE);
			}
		});
		animator.start();
	}

	private ValueAnimator createDropAnimator(final View view, int start, int end) {
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.setDuration(150);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (Integer) valueAnimator.getAnimatedValue();
				 ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
				 layoutParams.height = value; // 不确定View的父类时使用
//				LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
//				layoutParams.bottomMargin = -value;
				view.setLayoutParams(layoutParams);
			}

		});
		return animator;
	}
}