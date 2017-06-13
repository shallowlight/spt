package com.totrade.spt.mobile.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.autrade.stage.utility.CollectionUtility;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.entity.nim.CustomAttachParser;
import com.totrade.spt.mobile.helper.UpdateManager;
import com.totrade.spt.mobile.ui.qiyukf.UILImageLoader;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.view.R;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import cn.jpush.android.api.JPushInterface;

/**
 * @author haungxy
 */
public class SptApplication extends Application implements UncaughtExceptionHandler {
    public static Context context;
    public static Handler appHandler;
    private static boolean isRelease = true;

    @SuppressWarnings("static-access")
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        appHandler = new Handler();
        isRelease = isRelease();       //TODO
        AppConstant.isRelease = isRelease;

        JPushInterface.setDebugMode(!isRelease); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush
        if (isRelease) {
            Thread mainThread = Thread.currentThread();
//			设置一个未捕获的异常的，统一处理
            mainThread.setUncaughtExceptionHandler(this);
        }

        NIMClient.init(this, loginInfo(), options());

        if (inMainProcess(this)) {
            NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser()); // 监听的注册，必须在主进程中。
        }

        //网易七鱼客服SDK
        Unicorn.init(this, "f747798448ab84932e940324087442f7", ysfOptions(), new UILImageLoader());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private YSFOptions ysfOptions() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }

    /**
     * 判断是否是Debug
     */
    public boolean isRelease() {
        try {
            return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0;
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            String logPath = FileUtils.getLogCacheRoot() + "/error_log.txt";            // 创建日志文件
            PrintStream printStream = new PrintStream(new FileOutputStream(logPath));
            Class<?> clazz = Class.forName("android.os.Build");
            Field[] fields = clazz.getFields();                     // 获得所有的成员变量
            for (Field field : fields) {
                String name = field.getName();                      // 变量的名称
                Object value = field.get(null);                     // 获得值
                printStream.println(name + " :" + value);           // 写入手机自身信息
            }
            printStream.println("===============================");
            ex.printStackTrace(printStream);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        com.netease.nimlib.sdk.SDKOptions options = new com.netease.nimlib.sdk.SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
//		StatusBarNotificationConfig config = new StatusBarNotificationConfig();
//
//		// TODO 点击通知栏跳转到该Activity
//		 config.notificationEntrance = SplashActivity.class;
//		 config.notificationSmallIconId = R.drawable.ic_launcher;
//		// 呼吸灯配置
//		config.ledARGB = Color.GREEN;
//		config.ledOnMs = 1000;
//		config.ledOffMs = 1500;
//		// 通知铃声的uri字符串
//		 config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//
//		options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2

        options.thumbnailSize = SystemUtil.getScreenWidth() / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {

            @Override
            public Bitmap getAvatarForMessageNotifier(String arg0) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.drawable.img_headpic_def;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String arg0, String arg1, SessionTypeEnum arg2) {
                return null;
            }

            @Override
            public Bitmap getTeamIcon(String arg0) {
                return null;
            }

            @Override
            public UserInfo getUserInfo(String arg0) {
                return null;
            }
        };
        return null;
        // return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        return null;
    }


    /**
     * 初始化ImageLoad,因为app中大量包含图片加载的页面，故在此配置初始化
     * universal-image-loader-1.9.4.jar 相关 初始化
     */
    public static void initImageLoader(Context context) {
        //缓存文件的目录
//        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // 50 Mb sd卡(本地)缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
//                .diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

    public static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = SystemUtil.getProcessName(context);
        return packageName.equals(processName);
    }
}
