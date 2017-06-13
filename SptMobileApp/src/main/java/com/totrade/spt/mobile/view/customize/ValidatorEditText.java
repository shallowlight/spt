package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.totrade.spt.mobile.common.AppConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ValidatorEditText extends EditText {

    /**
     * EditText可校验类型枚举
     */
    public enum ValidatorType {

        /**
         * 数字验证
         */
        NUMBER("输入的数字不合法"),
        /**
         * 邮箱验证
         */
        EMAIL("邮箱号不合法"),
        /**
         * 手机号码验证
         */
        PHONE("手机号码不合法"),
        /**
         * 网址
         */
        URL("URL地址不合法"),
        /**
         * QQ验证
         */
        QQ("QQ号码不合法"),
        /**
         * 中文验证
         */
        CHINESE("输入了非中文的字"),
        /**
         * 公司名称
         */
        COMPANY_NAME("请输入5-30的合法公司名称"),
        /**
         * 身份证
         */
        ID_CARD("请输入正确的身份证号码");

        private String msg;

        ValidatorType(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    private Context context;

    private static Map<ValidatorType, String> regexMap;

    static {
        regexMap = new HashMap<ValidatorType, String>();
        regexMap.put(ValidatorType.NUMBER, "^\\d+$");
        regexMap.put(ValidatorType.EMAIL, AppConstant.RE_EMAIL);
        regexMap.put(ValidatorType.PHONE, AppConstant.RE_PHONE);//"^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\\\d{8}$");
        regexMap.put(ValidatorType.URL, AppConstant.RE_URL);
        regexMap.put(ValidatorType.QQ, AppConstant.RE_QQ);
        regexMap.put(ValidatorType.CHINESE, AppConstant.RE_CHINESE);
        regexMap.put(ValidatorType.COMPANY_NAME, AppConstant.RE_COMPANYNAME);
        regexMap.put ( ValidatorType.ID_CARD, AppConstant.RE_ID_CARD );
    }

    public ValidatorEditText(Context context) {
        super(context);
        initEditText(context);
    }

    public ValidatorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText(context);
    }

    public ValidatorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void initEditText(Context context) {
        this.context = context;
    }

    /**
     * 验证是否匹配某种类型
     *
     * @param regexType
     * @return
     */
    public boolean checkBody(ValidatorType regexType) {
        return checkBody(regexMap.get(regexType));
    }

    /**
     * 验证某种正则表达式
     *
     * @param regex
     * @return
     */
    public boolean checkBody(String regex) {
        String body = this.getText().toString().trim();
        if (body == null || body.equals("")) {
            Log.e(this.getClass().getSimpleName(), "EditText getText() is null or \"\"");
            return false;
        }
        return body.matches(regex);
    }

    /**
     * 验证某种输入错误后返回错误信息
     *
     * @param validatorType
     * @return
     */
    public String checkBodyErrorMsg(ValidatorType validatorType) {
        if (!checkBody(validatorType)){
            return validatorType.msg;
        }
        else{
            return "";
        }
    }

}
