package com.totrade.spt.mobile.view.im;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.totrade.spt.mobile.utility.ToastHelper;
import com.totrade.spt.mobile.view.R;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.CommonTitle3;
import com.totrade.spt.mobile.view.customize.MyEditText;
import com.totrade.spt.mobile.view.customize.ValidatorEditText;

import java.util.HashMap;
import java.util.Map;

public class IMContractUserRemarkActivity extends SptMobileActivityBase {

    private CommonTitle3 titleView;

    private EditText etNickName;

    private ValidatorEditText etPhoneNum;

    private MyEditText etDescription;

    private static String KEY_PHONE = "phone";
    private static String KEY_DESCRIPTION = "description";

    private String nickName = "";
    private String phontNum = "";
    private String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_remark);
        initView();
        initSet();
    }

    private void initView() {
        titleView = (CommonTitle3) findViewById(R.id.titleView);
        etNickName = (EditText) findViewById(R.id.etNickName);
        etPhoneNum = (ValidatorEditText) findViewById(R.id.etPhoneNum);
        etDescription = (MyEditText) findViewById(R.id.etDescription);
    }


    /**
     * 初始化设置
     */
    private void initSet() {

        titleView.getTitleRightLbl("完成").setOnClickListener(publishClick);
        try {
            Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(getIntent().getStringExtra("TEAM_ACCOUNT"));
            etNickName.setText(friend.getAlias());

            Map<String, Object> friendExtensionMap = friend.getExtension();
            if (friendExtensionMap != null) {
                String phone = (String) friendExtensionMap.get(KEY_PHONE);
                String description = (String) friendExtensionMap.get(KEY_DESCRIPTION);
                etPhoneNum.setText(phone);
                etDescription.setText(description);
            }
        } catch (Exception e) {

        }

        etNickName.addTextChangedListener(new MTextWatcher(etNickName));
        etPhoneNum.addTextChangedListener(new MTextWatcher(etPhoneNum));
        etDescription.addTextChangedListener(new MTextWatcher(etDescription));
    }

    /**
     * 处理编辑完成后发布
     */
    private View.OnClickListener publishClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nickName = etNickName.getText().toString().trim();
            phontNum = etPhoneNum.getText().toString().trim();
            description = etDescription.getText().toString();
            if (phontNum.length() > 0 && !etPhoneNum.checkBody(ValidatorEditText.ValidatorType.PHONE))
                ToastHelper.showMessage("请输入正确的手机号");
            else
                updateFriendFields();
        }
    };

    private class MTextWatcher implements TextWatcher {

        private EditText mEditText;

        public MTextWatcher(EditText editText) {
            this.mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mEditText.getId()) {
                case R.id.etNickName:
                    nickName = s.toString().trim();
//                    if (s.toString().length() == 0)
//                        showMessage("昵称不可为空");
                    break;
                case R.id.etPhoneNum:
                    phontNum = s.toString().trim();
                    if (s.toString().length() >= 11 && !etPhoneNum.checkBody(ValidatorEditText.ValidatorType.PHONE))
                        ToastHelper.showMessage("请输入正确的手机号");
                    break;
                case R.id.etDescription:
                    description = s.toString().trim();
                    break;
            }

        }
    }

    private void updateFriendFields() {

        // 更新备注名
        Map<FriendFieldEnum, Object> map = new HashMap<>();
        map.put(FriendFieldEnum.ALIAS, nickName);

        Map<String, String> mapExtend = new HashMap<>();
        mapExtend.put(KEY_PHONE, phontNum);
        mapExtend.put(KEY_DESCRIPTION, description);
        map.put(FriendFieldEnum.EXTENSION, mapExtend);
        NIMClient.getService(FriendService.class).updateFriendFields(getIntent().getStringExtra("TEAM_ACCOUNT"), map)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int i, Void aVoid, Throwable throwable) {
                        if (ResponseCode.RES_SUCCESS == i) {
                            ToastHelper.showMessage("设置成功");
                            finish();
                        }
                    }
                });
    }
}
