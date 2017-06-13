package com.totrade.spt.mobile.utility;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;

import com.totrade.spt.mobile.app.SptApplication;
import com.totrade.spt.mobile.view.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    public static File getFileRoot(String fileType) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append("SPT");
        sb.append(File.separator);
        sb.append(fileType);
        // 缓存根目录
        File rootFile = new File(sb.toString());
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return rootFile;

    }

    // 文件缓存目录
    public static File getFileCacheRoot() {
        return getFileRoot("cache");
    }

    // 图片缓存目录
    public static File getPhotoCacheRoot() {
        return getFileRoot("photo");
    }

    // 错误日志缓存目录
    public static File getLogCacheRoot() {
        return getFileRoot("log");
    }

    private static FileWriter fw;
    private static BufferedWriter bw;
    private static FileReader fr;
    private static BufferedReader br;
    private static StringWriter sw;

    /**
     * 缓存文件
     *
     * @param json
     * @param fileType
     */
    public static void saveCache(String json, String fileType) {
        saveCache(json, fileType, false);
    }

    /**
     * 缓存文件
     *
     * @param json
     * @param fileType 文件名
     * @param isAppend 是否追加
     */
    public static void saveCache(String json, String fileType, boolean isAppend) {
        try {
            File dir = FileUtils.getFileCacheRoot();
            File cacheFile = new File(dir, fileType);
            fw = new FileWriter(cacheFile);
            bw = new BufferedWriter(fw);
            if (isAppend) {
                bw.append("----------------------");
                bw.append(json);
            } else {
                bw.write(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取缓存的文件
     *
     * @param fileType
     * @return
     */
    public static String readCache(String fileType) {
        try {
            File dir = FileUtils.getFileCacheRoot();
            File cacheFile = new File(dir, fileType);
            if (cacheFile.exists()) {
                fr = new FileReader(cacheFile);
                br = new BufferedReader(fr);
                String temp = "";

                sw = new StringWriter();
                while ((temp = br.readLine()) != null) {
                    sw.write(temp);
                }
                return sw.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                sw.close();
                br.close();
                fr.close();
            } catch (Exception e) {
            }
        }

    }

    public static String getCacheSize(File... files) {
        long l = 0;
        for (File file : files) {
            l += getFolderSize(file);
        }
        return Formatter.formatFileSize(SptApplication.context, l);
    }

    /**
     * 获取文件size
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) {
        long size = 0;

        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            // 如果下面还有文件
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }

        return size;
    }

    /**
     * 删除文件夹和文件夹里面的文件
     *
     * @param path
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
            } else if (file.isDirectory()) {
                deleteDir(file.getPath()); // 递规的方式删除文件夹
            }
        }
        // dir.delete();// 删除目录本身
    }

    /**
     * Uri转File
     *
     * @param contentUri
     * @return
     */
    public static File getRealPathFromUri(Uri contentUri) {
        String res = null;
        String[] proj =
                {MediaStore.Images.Media.DATA};
        Cursor cursor = SptApplication.context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return new File(res);
    }

    /**
     * 生成文件名
     *
     * @return
     */
    public static String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date cruDate = Calendar.getInstance().getTime();
        String fileName = sdf.format(cruDate) + ".jpg";
        return fileName;
    }

    public static boolean exists(String path) {
        if (path == null) {
            return false;
        }
        return new File(path).exists();
    }

    /**
     * bitmap转file
     *
     * @param bitmap
     * @param fileName
     */
    public static File saveBitmapFile(Bitmap bitmap, String fileName) {
        File file = new File(getPhotoCacheRoot(), fileName);// 将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static boolean hasExtentsion(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > -1) && (dot < (filename.length() - 1));
    }

    public static Bitmap decode(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        /**
         * 在4.4上，如果之前is标记被移动过，会导致解码失败
         */
        try {
            if (is.markSupported()) {
                is.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return BitmapFactory.decodeStream(is, null, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Uri drawable2Uri(int drawableResource) {
        Resources r = SptApplication.context.getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(drawableResource) + "/"
                + r.getResourceTypeName(drawableResource) + "/"
                + r.getResourceEntryName(drawableResource));
    }

}
