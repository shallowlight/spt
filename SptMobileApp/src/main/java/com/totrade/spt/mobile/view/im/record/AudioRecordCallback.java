package com.totrade.spt.mobile.view.im.record;

import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;

public abstract class AudioRecordCallback implements IAudioRecordCallback
{

	private static String TAG = "AudioRecordCallback";
	// 定义录音过程回调对象

	@Override
	public void onRecordReady()
	{
		// 初始化完成回调，提供此接口用于在录音前关闭本地音视频播放（可选）
	}

	@Override
	public void onRecordStart(File audioFile, RecordType recordType)
	{
		// 开始录音回调
	}

	@Override
	public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType)
	{
		// 录音结束，成功
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, "file.exists : : : " + audioFile.exists()); // XXX
	}

	@Override
	public void onRecordFail()
	{
		// 录音结束，出错
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, "onRecordFail : : : "); // XXX
	}

	@Override
	public void onRecordCancel()
	{
		// 录音结束， 用户主动取消录音
		android.util.Log.i(TAG, "onRecordCancel : : : "); // XXX
	}

	@Override
	public void onRecordReachedMaxTime(int maxTime)
	{
		// 到达指定的最长录音时间
	}
}
