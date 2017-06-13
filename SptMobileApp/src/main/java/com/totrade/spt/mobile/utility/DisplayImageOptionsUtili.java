package com.totrade.spt.mobile.utility;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.totrade.spt.mobile.view.R;

/**
 * Created by iUserName on 2016/9/23.
 */
public class DisplayImageOptionsUtili
{
    private static DisplayImageOptions options; // 设置图片显示相关参数

    public static DisplayImageOptions getOptions()
    {
        if(options == null)
        {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.img_headpic_def) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.img_headpic_def) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.img_headpic_def) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    .build(); // 构建完成
        }
        return options;
    }
}
