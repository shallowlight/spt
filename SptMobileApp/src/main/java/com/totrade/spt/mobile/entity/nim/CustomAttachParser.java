package com.totrade.spt.mobile.entity.nim;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;


public class CustomAttachParser implements MsgAttachmentParser {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = object.getInteger(KEY_TYPE);
//            JSONObject data = object.getJSONObject(KEY_DATA);

            switch (type) {
                case CustomAttachmentType.Type:
                    attachment = new OrderAttachment();
                    break;
                default:
                    break;
            }

            if (attachment != null) {
//                attachment.fromJson(data);
                attachment.fromJson(object);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
