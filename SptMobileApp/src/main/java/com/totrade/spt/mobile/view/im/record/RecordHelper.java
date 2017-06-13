package com.totrade.spt.mobile.view.im.record;

import android.media.AudioManager;

import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.totrade.spt.mobile.app.SptApplication;

public class RecordHelper
{
	private static String TAG = "RecordHelper";
	private static int maxDuration = AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND;
	private static AudioRecorder recorder;
	private static AudioRecordCallback audioRecordCallback;

	public static void setAudioRecordCallback(AudioRecordCallback audioRecordCallback)
	{
		RecordHelper.audioRecordCallback = audioRecordCallback;
	}

	public static void ready()
	{
		if (audioRecordCallback != null)
		{
			recorder = new AudioRecorder(SptApplication.context, RecordType.AAC, // 录制音频类型（aac/amr)
					maxDuration, // 最长录音时长，到该长度后，会自动停止录音
					audioRecordCallback // 录音过程回调
			);

			startRecord();
		}
	}

	public static void startRecord()
	{
		// 开始录音
		if (!recorder.startRecord())
		{
			// 开启录音失败。
			com.totrade.spt.mobile.utility.LogUtils.i(TAG, "record failed : : : "); // XXX
		} else
		{
			com.totrade.spt.mobile.utility.LogUtils.i(TAG, "record start : : : "); // XXX
		}
	}

	public static void cancelRecord()
	{
		if (recorder == null)
			return;
		// 结束录音, false正常结束，或者true取消录音
		recorder.completeRecord(true);
	}

	public static void stopRecord()
	{
		com.totrade.spt.mobile.utility.LogUtils.i(TAG, "stop record : : : "); // XXX
		if (recorder == null)
			return;
		recorder.completeRecord(false);
	}

	private static AudioPlayer player;
	private static IMOnPlayListener onPlayListener;

	public static void setOnPlayListener(IMOnPlayListener onPlayListener)
	{
		RecordHelper.onPlayListener = onPlayListener;
	}

	public static void playAudio(String filePath)
	{

		if (onPlayListener != null)
		{
			com.totrade.spt.mobile.utility.LogUtils.i(TAG, "onPlayListener : : : " + onPlayListener); // XXX
			// 构造播放器对象
			player = new AudioPlayer(SptApplication.context, filePath, onPlayListener);
			// 开始播放。需要传入一个 Stream Type 参数，表示是用听筒播放还是扬声器。取值可参见
			// android.media.AudioManager#STREAM_***
			// AudioManager.STREAM_VOICE_CALL 表示使用听筒模式
			// AudioManager.STREAM_MUSIC 表示使用扬声器模式
			player.start(AudioManager.STREAM_MUSIC);

			com.totrade.spt.mobile.utility.LogUtils.i(TAG, "player.start : : : "); // XXX

		}
	}

	public static void pause()
	{
		// 如果中途切换播放设备，重新调用 start，传入指定的 streamType 即可。player 会自动停止播放，然后再以新的
		// streamType 重新开始播放。
		// 如果需要从中断的地方继续播放，需要外面自己记住已经播放过的位置，然后在 onPrepared 回调中调用 seekTo
		// if (player != null) player.seekTo(pausedPosition);
	}

	public static void stop()
	{
		// 主动停止播放
		if (player != null)
			player.stop();

		com.totrade.spt.mobile.utility.LogUtils.i(TAG, "player.stop : : : "); // XXX
	}

	public static boolean isPlaying()
	{
		if (player != null)
		{
			return player.isPlaying();
		} else
		{
			return false;
		}
	}
}
