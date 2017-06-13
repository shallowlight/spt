package com.totrade.spt.mobile.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;

import com.autrade.stage.utility.StringUtility;
import com.totrade.mebius.helper.ServiceRegisterHelper;
import com.totrade.spt.mobile.view.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PicUtility {
    public static final String TBL_EZONE_REQUEST = "TBL_EZONE_REQUEST";    //  我要报价图片上传  标识字段
    public static final String TBL_EZONE_OFFER = "TBL_EZONE_OFFER";
    public static final String TBL_RUNNINGACCOUNT_OP = "TBL_RUNNINGACCOUNT_OP";
    public static final String TBL_ZONE_APPLY = "tbl_zone_apply";

    public static String upLoadQualityReport(String filePath, String tableName) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        // String picBase64 = getImageByte(filePath);
        String picBase64 = bitmap2BaseStr(filePath);
        String picUrl = TranscodingUtility.urlEncoder(picBase64);
        map.put("base64Data", picUrl);
        map.put("tableName", tableName);
        String scontent = map2Url(map);
        String result = HttpURLConnectionUtility.HttpURLConnectionRequest(ServiceRegisterHelper.picUrl, scontent, true);
        return result;
    }

    private static String bitmap2BaseStr(String filePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getimage(filePath).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            return TranscodingUtility.base64Encoder(baos.toByteArray());
        } catch (UnsupportedEncodingException e) {
            return "";
        } finally {
        }
    }

    public static String map2Url(Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return "";
        }
        String urlSub = "";
        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
            urlSub += map.getKey() + "=" + map.getValue() + "&";
        }
        return urlSub.isEmpty() ? "" : urlSub.substring(0, urlSub.length() - 1);
    }

    public static String getImageByte(String filePath) throws UnsupportedEncodingException {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TranscodingUtility.base64Encoder(data);
    }


    public static boolean generateImage(String imgStr, String filePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (StringUtility.isNullOrEmpty(imgStr) || StringUtility.isNullOrEmpty(filePath)) {
            return false;
        }
        try {
            // Base64解码
            byte[] b = TranscodingUtility.base64Decoder2Byte(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(filePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();

        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]
                    {ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 800f;
        float ww = 480f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // 压缩好比例大小后，进行质量压缩并旋转角度
        bitmap = rotaingImageView(readPictureDegree(srcPath), compressImage(bitmap));
        return bitmap;
    }

    /**
     * 压缩图片至固定值
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        } catch (Exception e) {
            return image;
        }
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 读取图片旋转的角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
