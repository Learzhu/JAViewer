package io.github.javiewer.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * 文件位置的工具类
 */
public class FileLocationUtil {
    static String SAVE_MOVIE_NAME = "1.mp4";

    static String SAVE_MOVIE_LOCATION = "/download";

    static String MOVIE_FILE_NAME = "/sdcard" + SAVE_MOVIE_LOCATION + File.separator + SAVE_MOVIE_NAME;

    /**
     * 获取当前版本的版本名称
     *
     * @param context 上下文对象
     * @return 返回版本名称 如果无 则返回null
     */
    public static String getAppVersionName(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionName;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getDownloadFilePath() {
        return "/sdcard/download";
    }
}
