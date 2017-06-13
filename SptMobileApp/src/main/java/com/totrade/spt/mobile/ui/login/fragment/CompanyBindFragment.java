package com.totrade.spt.mobile.ui.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autrade.spt.master.dto.CompanyRegUpEntity;
import com.autrade.spt.master.dto.LoginUserDto;
import com.autrade.spt.master.dto.UserBindCompanyUpEntity;
import com.autrade.spt.master.entity.NewUserRegisterEntity;
import com.autrade.spt.master.entity.TblCompanyMasterEntity;
import com.autrade.spt.master.service.inf.ILoginService;
import com.autrade.spt.master.service.inf.IUserService;
import com.autrade.stage.entity.GeneralDownEntity;
import com.autrade.stage.exception.ApplicationException;
import com.autrade.stage.exception.DBException;
import com.autrade.stage.utility.JsonUtility;
import com.autrade.stage.utility.StringUtility;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.totrade.spt.mobile.base.BaseFragment;
import com.totrade.spt.mobile.common.AppConstant;
import com.totrade.spt.mobile.common.Client;
import com.totrade.spt.mobile.ui.SearchActivity;
import com.totrade.spt.mobile.ui.login.RegistActivity;
import com.totrade.spt.mobile.utility.EncryptUtility;
import com.totrade.spt.mobile.utility.FileUtils;
import com.totrade.spt.mobile.utility.OnDataListener;
import com.totrade.spt.mobile.utility.PicUtility;
import com.totrade.spt.mobile.utility.SharePreferenceUtility;
import com.totrade.spt.mobile.utility.SubAsyncTask;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 绑定企业页面
 *
 * @author huangxy
 * @date 2017/4/17
 */
public class CompanyBindFragment extends BaseFragment implements View.OnClickListener {

    private RegistActivity activity;
    private View rootView;

    private FrameLayout fl_upload1;
    private FrameLayout fl_upload2;
    private EditText et_com_name;
    private EditText et_real_name;
    private EditText et_email;
    private EditText et_id_num;

    private PopupWindow mPopupWindow;
    private TextView tvCancel, tvPhoto, tvSelect;
    private TblCompanyMasterEntity tblCompanyMasterEntity;

    public CompanyBindFragment() {
        setContainerId(R.id.framelayout);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (RegistActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_reigst_company_bind, container, false);

        et_com_name = (EditText) rootView.findViewById(R.id.et_com_name);
        et_real_name = (EditText) rootView.findViewById(R.id.et_real_name);
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        et_id_num = (EditText) rootView.findViewById(R.id.et_id_num);
        fl_upload1 = (FrameLayout) rootView.findViewById(R.id.fl_upload1);
        fl_upload2 = (FrameLayout) rootView.findViewById(R.id.fl_upload2);
        et_com_name.setOnClickListener(this);
        fl_upload1.setOnClickListener(this);
        fl_upload2.setOnClickListener(this);
        rootView.findViewById(R.id.iv_back).setOnClickListener(this);
        rootView.findViewById(R.id.iv_company).setOnClickListener(this);
        rootView.findViewById(R.id.btn_done).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.title)).setText("绑定企业");

        return rootView;
    }

    private void showPopWindow(int i) {
        id = i;
        View view = View.inflate(activity, R.layout.menu_tabinfo, null);
        mPopupWindow = new PopupWindow(view, -1, -1, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvPhoto = (TextView) view.findViewById(R.id.tv_edit);
        tvPhoto.setText(R.string.photo);
        tvSelect = (TextView) view.findViewById(R.id.tv_markAll);
        tvSelect.setText(R.string.select_from_photos);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                tempFile = new File(path, FileUtils.getFileName());
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri u = Uri.fromFile(tempFile);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, u);
                if (intentCamera.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intentCamera, OPEN_CAMERA_CODE);
                }
                mPopupWindow.dismiss();
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentPick = new Intent(Intent.ACTION_PICK);
                intentPick.setType("image/*");
                startActivityForResult(intentPick, OPEN_PICTURE_CODE);
                mPopupWindow.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(rootView.findViewById(R.id.view), Gravity.BOTTOM, 0, 0);
    }

    private File tempFile;
    private String picPath;
    private static final int OPEN_CAMERA_CODE = 10;// 打开相机requestCode
    private static final int OPEN_PICTURE_CODE = 11;// 打开相机requestCode
    private static final int QUERY_COMPANY = 12;// 查询公司名称

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data == null) {
            return;
        }
        switch (requestCode) {
            case OPEN_CAMERA_CODE:
                picPath = String.valueOf(tempFile);
                submitPop();
                break;
            case OPEN_PICTURE_CODE:
                picPath = PicUtility.getRealFilePath(activity, data.getData());
                submitPop();
                break;
            case QUERY_COMPANY:
                String companyStr = data.getStringExtra("company");
                tblCompanyMasterEntity = JsonUtility.toJavaObject(companyStr, new TypeToken<TblCompanyMasterEntity>() {
                }.getType());
                et_com_name.setText(tblCompanyMasterEntity.getCompanyName());
                break;

            default:
                break;
        }
    }

    /**
     * 图片预览及上传
     */
    private void submitPop() {
        View view = View.inflate(activity, R.layout.popwindow_photo, null);
        mPopupWindow = new PopupWindow(view, -1, -1, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView ivPreview = (ImageView) view.findViewById(R.id.ivPreview);
        ivPreview.setImageBitmap(PicUtility.getimage(picPath));
        view.findViewById(R.id.tvSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadPhoto();
            }
        });
        mPopupWindow.showAtLocation(rootView.findViewById(R.id.view), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 上传图片
     */
    private void upLoadPhoto() {
        SubAsyncTask.create().setOnDataListener(activity, false, new OnDataListener<String>() {

            private String s;

            @Override
            public String requestService() throws DBException, ApplicationException {
                try {
                    s = PicUtility.upLoadQualityReport(picPath, PicUtility.TBL_RUNNINGACCOUNT_OP);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            public void onDataSuccessfully(String value) {
                if (!StringUtility.isNullOrEmpty(s) && s.startsWith("success:")) {
                    ImageView iv1 = new ImageView(activity);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    iv1.setLayoutParams(layoutParams);
                    if (id == 0) {
                        fl_upload1.removeAllViews();
                        fl_upload1.addView(iv1);
                    } else {
                        fl_upload2.removeAllViews();
                        fl_upload2.addView(iv1);
                    }
                    Picasso.with(activity).load(new File(picPath)).into(iv1);

                    ids[id] = s.substring(8, s.length());
                    id = -1;
                    ToastHelper.showMessage("上传成功");
                } else {
                    ToastHelper.showMessage("上传失败，请检查网络后重试");
                }
                mPopupWindow.dismiss();
            }
        });
    }

    private String[] ids = new String[2];
    private int id = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                activity.finish();
                break;
            case R.id.fl_upload1:
                showPopWindow(0);
                break;
            case R.id.fl_upload2:
                showPopWindow(1);
                break;
            case R.id.iv_company:
            case R.id.et_com_name:
                Intent intent = new Intent(activity, SearchActivity.class);
                startActivityForResult(intent, QUERY_COMPANY);
                break;
            case R.id.btn_done:
                if (!valid())
                    break;
                bindCompanySave();
                break;
        }
    }

    /**
     * 绑定企业保存
     * <p>用户快速注册完成后，需要成为企业用户可进行绑定</p>
     */
    private void bindCompanySave() {
        SubAsyncTask.create().setOnDataListener(activity, false, new OnDataListener<GeneralDownEntity>() {
            @Override
            public GeneralDownEntity requestService() throws DBException, ApplicationException {
                UserBindCompanyUpEntity upEntity = new UserBindCompanyUpEntity();

                String newUserRegisterEntityStr = SharePreferenceUtility.spGetOut(activity, SharePreferenceUtility.REGISTER_PERSONAL, null);
                NewUserRegisterEntity newUserRegisterEntity = JsonUtility.toJavaObject(newUserRegisterEntityStr, new TypeToken<NewUserRegisterEntity>() {
                }.getType());
                upEntity.setUserId(newUserRegisterEntity.getUserId());

                upEntity.setCompanyTag(tblCompanyMasterEntity.getCompanyTag());
                upEntity.setRealName(et_real_name.getText().toString());
                upEntity.setIdNumber(et_id_num.getText().toString());
                upEntity.setAttacheFileId(ids[0]);
                upEntity.setAttacheFileId(ids[1]);
                return Client.getService(IUserService.class).bindCompanySave(upEntity);
            }

            @Override
            public void onDataSuccessfully(GeneralDownEntity obj) {
                if (obj != null && obj.getParamBool1()) {
                    activity.switchSuccess(activity.company);
                }
            }
        });
    }


    private String[] hints = new String[]{
            "请输入企业名称",
            "请输入真实姓名",
            "请设置正确的邮箱",
            "请输入正确的身份证号码",
            "", "", "", "", "", "",};

    private boolean valid() {
        String companyName = et_com_name.getText().toString();
        if (TextUtils.isEmpty(companyName) || !companyName.matches(AppConstant.RE_COMPANYNAME)) {
            ToastHelper.showMessage(hints[0]);
            return false;
        }
        String realName = et_real_name.getText().toString();
        if (TextUtils.isEmpty(realName) || !realName.matches(AppConstant.RE_RELNAME)) {
            ToastHelper.showMessage(hints[1]);
            return false;
        }

//        String email = et_email.getText().toString();
//        if (TextUtils.isEmpty(email) || !email.matches(AppConstant.RE_EMAIL)) {
//            ToastHelper.showMessage(hints[2]);
//            return false;
//        }

        String idNum = et_id_num.getText().toString();
        if (TextUtils.isEmpty(idNum)|| !idNum.matches(AppConstant.RE_ID_CARD)) {
            ToastHelper.showMessage(hints[3]);
            return false;
        }

        return true;
    }

}
