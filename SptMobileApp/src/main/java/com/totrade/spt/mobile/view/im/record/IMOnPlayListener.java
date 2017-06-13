package com.totrade.spt.mobile.view.im.record;

import com.netease.nimlib.sdk.media.player.OnPlayListener;

public abstract class IMOnPlayListener implements OnPlayListener {


	String TAG = "IMOnPlayListener";

	// 音频转码解码完成，会马上开始播放了
	public void onPrepared() {
		android.util.Log.i(TAG, "play onPrepared : : : "); // XXX
	}

	// 播放结束
	public abstract void onCompletion();

	// 播放被中断了
	public void onInterrupt() {
		android.util.Log.i(TAG, "play interrupt : : : "); // XXX
	}

	// 播放过程中出错。参数为出错原因描述
	public void onError(String error) {
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, "play error : : : " + error); // XXX
	}

	// 播放进度报告，每隔 500ms 会回调一次，告诉当前进度。 参数为当前进度，单位为毫秒，可用于更新 UI
	public abstract void onPlaying(long curPosition);
}
