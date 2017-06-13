package com.totrade.spt.mobile.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autrade.spt.report.entity.TblIMUserEntity;
import com.autrade.spt.report.im.vo.resp.IMResp;
import com.autrade.spt.report.service.inf.IIMUserService;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.StringUtility;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.totrade.spt.mobile.bean.Attatch;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.common.LoginUserContext;
import com.totrade.spt.mobile.utility.LogUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.PicUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.view.im.common.FutureRequestCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Timothy on 2017/4/24.
 */

public abstract class BaseCameraActivity extends BaseActivity {
    protected ViewGroup mRootView;
    private final SparseArray<View> mViews = new SparseArray<>();
    private final static int REQUEST_CODE_TAKEPHOTO = 0x0001;
    private final static int REQUEST_CODE_DCIM = 0x0002;


    protected View popupView;
    private ImageView ivPreView;
    protected TextView btnCancel;
    protected TextView btnTakePhotos;
    protected TextView btnDcim;
    private LinearLayout llMenu;
    private File tempFile;
    private PopupWindow popup;
    private String filepath;
    private boolean isPreView;//是否显示预览图片页面
    private boolean isUserAvatar;//是否为用户头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCameraView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initCameraView() {

        popupView = LayoutInflater.from(this).inflate(R.layout.view_popup_camera, null);
        btnCancel = (TextView) popupView.findViewById(R.id.btnCancel);
        ivPreView = (ImageView) popupView.findViewById(R.id.iv_preview);
        btnTakePhotos = (TextView) popupView.findViewById(R.id.btnTakePhotos);
        btnDcim = (TextView) popupView.findViewById(R.id.btnDcim);
        llMenu = (LinearLayout) popupView.findViewById(R.id.llMenu);

        btnDcim.setOnClickListener(mOnClickListener);
        btnTakePhotos.setOnClickListener(mOnClickListener);
        btnCancel.setOnClickListener(mOnClickListener);
        ivPreView.setOnClickListener(mOnClickListener);
    }

    /**
     * 是否阅览，是否为用户头像上传
     * @param isPreView
     * @param isUserAvatar
     */
    protected void showCameraPopWindow(boolean isPreView, boolean isUserAvatar) {
        this.isPreView = isPreView;
        this.isUserAvatar = isUserAvatar;
        initViewVisiable(isPreView);
        popup = new PopupWindow();
        popup.setContentView(popupView);
        popup.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popup.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        popup.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popup.setBackgroundDrawable(dw);
        popup.setOutsideTouchable(true);
        popup.setAnimationStyle(R.style.AnimBottom);

        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.5f;
        getWindow().setAttributes(wlBackground);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
                ivPreView.setVisibility(View.INVISIBLE);
                ivPreView.setImageDrawable(null);
                llMenu.setVisibility(View.VISIBLE);
                btnCancel.setText("取消");
            }
        });

        popup.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void initViewVisiable(boolean isPreView) {
        if (isPreView) {
            ivPreView.setVisibility(View.VISIBLE);
        } else {
            ivPreView.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnDcim:
                    Intent intentPick = new Intent(Intent.ACTION_PICK);
                    intentPick.setType("image/*");
                    startActivityForResult(intentPick, REQUEST_CODE_DCIM);
                    break;
                case R.id.btnTakePhotos:
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMAN);
                    Date cruDate = Calendar.getInstance().getTime();
                    String fileName = sdf.format(cruDate) + ".jpg";
                    tempFile = new File(path, fileName);
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri u = Uri.fromFile(tempFile);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    if (intentCamera.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intentCamera, REQUEST_CODE_TAKEPHOTO);
                    }
                    break;
                case R.id.iv_preview:
                    dismissPop();
                    break;
                case R.id.btnCancel:
                    if (isPreView) upLoad();
                    else dismissPop();
                    break;
            }
        }
    };

    /**
     * 上传图片到后台服务器
     */
    private void upLoadPhoto(final String picPath) {
        SubAsyncTask.create().setOnDataListener(this, false, new OnDataListener<String>() {
            @Override
            public String requestService() throws DBException, ApplicationException {
                try {
                    return PicUtility.upLoadQualityReport(picPath, PicUtility.TBL_EZONE_REQUEST);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onDataSuccessfully(String value) {
                if (!StringUtility.isNullOrEmpty(value) && value.startsWith("success:")) {
                    String picID = value.substring(8, value.length());
                    Toast.makeText(BaseCameraActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    Attatch attatch = new Attatch(Long.parseLong(picID), picPath, null);
                    getCameraData(attatch);
                    dismissPop();
                } else {
                    Toast.makeText(BaseCameraActivity.this, "上传失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 上传头像到网易云
     */
    @SuppressWarnings("unchecked")
    private void uploadToIM(Uri u) {
        AbortableFuture<String> future = NIMClient.getService(NosService.class).upload(new File(u.getEncodedPath()), AppConstant.MIME_JPEG);
        future.setCallback(new FutureRequestCallback<String>() {
            @Override
            public void onSuccess(String url) {
                super.onSuccess(url);
                saveIMUser(url);
            }
        });
    }

    /**
     * 创建更新云信账户资料
     */
    private void saveIMUser(final String url) {
        SubAsyncTask.create().setOnDataListener(new OnDataListener<IMResp>() {

            @Override
            public IMResp requestService() throws DBException, ApplicationException {
                TblIMUserEntity entity = new TblIMUserEntity();
                try {
                    entity.setAccid(LoginUserContext.getLoginUserDto().getImUserAccid());
                    entity.setIcon(url);
                    return Client.getService(IIMUserService.class).saveIMUser(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onDataSuccessfully(IMResp imResp) {
                if (imResp != null) {
                    dismissPop();
                    Toast.makeText(BaseCameraActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    List<String> accounts = new ArrayList<>();
                    accounts.add(LoginUserContext.getLoginUserDto().getImUserAccid());
                    InvocationFuture<List<NimUserInfo>> future = NIMClient.getService(UserService.class).fetchUserInfo(accounts); // 请求服务器
                    future.setCallback(new FutureRequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> param) {
                            super.onSuccess(param);
                            if (param != null && param.size() > 0) {
                                String url = param.get(0).getAvatar();
                                if (!TextUtils.isEmpty(url)) {
                                    Attatch attatch = new Attatch(0L, filepath, url);
                                    getCameraData(attatch);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data == null) {
            return;
        }
        String picPath = null;
        switch (requestCode) {
            case REQUEST_CODE_TAKEPHOTO:
                picPath = String.valueOf(tempFile);
                break;
            case REQUEST_CODE_DCIM:
                picPath = PicUtility.getRealFilePath(this, data.getData());
                break;
        }
        if (!StringUtility.isNullOrEmpty(picPath)) {
            filepath = picPath;
        } else {
            LogUtils.e(this.getClass().getSimpleName(), "获取图片失败");
            return;
        }

        //是否预览
        if (isPreView) {
            ivPreView.setImageBitmap(PicUtility.getimage(filepath));
            btnCancel.setText("上传");
            llMenu.setVisibility(View.GONE);
        } else {
            btnCancel.setText("取消");
            llMenu.setVisibility(View.VISIBLE);
            upLoad();
        }
    }

    private void upLoad() {
        //头像将上传至云信服务器
        if (isUserAvatar) {
            File tempFile = new File(filepath);
            Uri uri = Uri.fromFile(tempFile);
            uploadToIM(uri);
        } else {
            //非头像，并且不显示预览页面时将直接上传至后台
            upLoadPhoto(filepath);
        }
    }

    protected void dismissPop() {
        if (popup != null) {
            popup.dismiss();
        }
    }

    /**
     * 去掉abstract修饰,有选择进行重写
     *
     * @param t
     */
    public abstract void getCameraData(Attatch t);
}
