package com.totrade.spt.mobile.utility;

import com.autrade.spt.master.entity.TblErrorCodeMasterEntity;
import com.autrade.stage.utility.CollectionUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 错误字工具类
 *
 * @author wangyilin
 */
public final class ErrorCodeUtility {
    private static Map<Integer, String> errorCodeMap = new HashMap<>(); //错误字映射表

    private ErrorCodeUtility() {
    }

    /**
     * 初始化错误字列表
     *
     * @param dataList
     */
    public static void init(List<TblErrorCodeMasterEntity> dataList) {
        if (!CollectionUtility.isNullOrEmpty(dataList)) {
            for (TblErrorCodeMasterEntity entity : dataList) {
                errorCodeMap.put(entity.getErrorCode(), entity.getErrorMessageCh());
            }
        }
    }

    /**
     * 获取中文错误消息
     *
     * @param errorId 错误id
     * @return 错误消息
     */
    public static String getMessage(int errorId) {
        if (errorCodeMap.containsKey(errorId)) {
            return errorCodeMap.get(errorId);
        } else {
            return "发生异常，未知的异常信息，ErrorId：" + errorId;
        }
    }

    public static boolean isErrorMapEmpty() {
        return errorCodeMap.isEmpty();
    }


    private static Map<Integer, String> imErrorCodeMap = new HashMap<>(); //错误字映射表


    public static String getIMErrorMsg(int errorCode) {
        if (imErrorCodeMap.isEmpty()) {
            initImErrorCode();
        }
        return imErrorCodeMap.get(errorCode);
    }

    private static void initImErrorCode() {
        imErrorCodeMap.clear();
        imErrorCodeMap.put(200, "操作成功");
        imErrorCodeMap.put(201, "客户端版本不对，需升级sdk");
        imErrorCodeMap.put(301, "被封禁");
        imErrorCodeMap.put(302, "用户名或密码错误");
        imErrorCodeMap.put(315, "IP限制");
        imErrorCodeMap.put(403, "非法操作或没有权限");
        imErrorCodeMap.put(404, "对象不存在");
        imErrorCodeMap.put(405, "参数长度过长");
        imErrorCodeMap.put(406, "对象只读");
        imErrorCodeMap.put(408, "客户端请求超时");
        imErrorCodeMap.put(413, "验证失败(短信服务)");
        imErrorCodeMap.put(414, "参数错误");
        imErrorCodeMap.put(415, "客户端网络问题");
        imErrorCodeMap.put(416, "频率控制");
        imErrorCodeMap.put(417, "重复操作");
        imErrorCodeMap.put(418, "通道不可用(短信服务)");
        imErrorCodeMap.put(419, "数量超过上限");
        imErrorCodeMap.put(422, "账号被禁用");
        imErrorCodeMap.put(431, "HTTP重复请求");
        imErrorCodeMap.put(500, "服务器内部错误");
        imErrorCodeMap.put(503, "服务器繁忙");
        imErrorCodeMap.put(509, "无效协议");
        imErrorCodeMap.put(514, "服务不可用");
        imErrorCodeMap.put(801, "群人数达到上限");
        imErrorCodeMap.put(802, "没有权限");
        imErrorCodeMap.put(803, "群不存在");
        imErrorCodeMap.put(804, "用户不在群");
        imErrorCodeMap.put(805, "群类型不匹配");
        imErrorCodeMap.put(806, "创建群数量达到限制");
        imErrorCodeMap.put(807, "群成员状态错误");
        imErrorCodeMap.put(808, "申请成功");
        imErrorCodeMap.put(809, "已经在群内");
        imErrorCodeMap.put(810, "邀请成功");
        imErrorCodeMap.put(998, "解包错误");
        imErrorCodeMap.put(999, "打包错误");
        imErrorCodeMap.put(1000, "本地操作异常");
        imErrorCodeMap.put(9102, "通道失效");
        imErrorCodeMap.put(9103, "已经在他端对这个呼叫响应过了");
        imErrorCodeMap.put(10431, "输入email不是邮箱");
        imErrorCodeMap.put(10432, "输入mobile不是手机号码");
        imErrorCodeMap.put(10433, "注册输入的两次密码不相同");
        imErrorCodeMap.put(10434, "企业不存在");
        imErrorCodeMap.put(10435, "登录密码或账号不对");
        imErrorCodeMap.put(10436, "app不存在");
        imErrorCodeMap.put(10437, "email已注册");
        imErrorCodeMap.put(10438, "手机号已注册");
        imErrorCodeMap.put(10441, "app名字已经存在");
        imErrorCodeMap.put(11001, "通话不可达，对方离线状态");
        imErrorCodeMap.put(13001, "IM主连接状态异常");
        imErrorCodeMap.put(13002, "聊天室状态异常");
        imErrorCodeMap.put(13003, "账号在黑名单中,不允许进入聊天室");
        imErrorCodeMap.put(13004, "在禁言列表中,不允许发言");
    }

}
