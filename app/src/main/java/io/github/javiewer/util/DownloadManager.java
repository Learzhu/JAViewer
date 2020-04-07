package io.github.javiewer.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import static io.github.javiewer.util.FileLocationUtil.SAVE_MOVIE_LOCATION;
import static io.github.javiewer.util.FileLocationUtil.SAVE_MOVIE_NAME;

/**
 * 管理下载的DownloadManager
 */
public class DownloadManager {
    @SuppressWarnings("unused")
    private static final String TAG = "DownloadManager";
    public static long DOWNLOAD_ID;
    private static android.app.DownloadManager mDownloadManager;

    /**
     * 下载Apk, 并设置Apk地址,
     * 默认位置: /storage/sdcard0/Download
     *
     * @param context     上下文
     * @param downLoadUrl 下载地址
     * @param infoName    通知名称
     * @param description 通知描述
     */
    @SuppressWarnings("unused")
    public static void downloadMovie(
            Context context,
            String downLoadUrl,
            String description,
            String infoName) {
        File file = new File(FileLocationUtil.MOVIE_FILE_NAME);
        if (file.exists()) {//如果已经存在 那就先删除他
            file.delete();
        }

        if (!isDownloadManagerAvailable()) {
            return;
        }

        String appUrl = downLoadUrl;
        if (appUrl == null || appUrl.isEmpty()) {
            return;
        }
        appUrl = appUrl.trim(); // 去掉首尾空格
        if (!appUrl.startsWith("http")) {
            appUrl = "http://" + appUrl; // 添加Http信息
        }

        android.app.DownloadManager.Request request;
        try {
            request = new android.app.DownloadManager.Request(Uri.parse(appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        request.setTitle(infoName);
        request.setDescription(description);
        //增加内容说明
        request.setMimeType("application/vnd.android.package-archive");
        //在通知栏显示下载进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        //sdcard目录下的download文件夹
        request.setDestinationInExternalPublicDir(SAVE_MOVIE_LOCATION, SAVE_MOVIE_NAME);
        Context appContext = context.getApplicationContext();
        mDownloadManager = (android.app.DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DOWNLOAD_ID = mDownloadManager.enqueue(request);
    }

    // 最小版本号大于9
    private static boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }


//    public int[] getBytesAndStatus(long downloadId) {
//        int[] bytesAndStatus = new int[]{-1, -1, 0};
//        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
//        Cursor c = null;
//        try {
//            c = downloadManager.query(query);
//            if (c != null && c.moveToFirst()) {
//                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            }
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
//        return bytesAndStatus;
//    }

    /**
     * 获取指定任务的下载进度
     *
     * @return
     */
    public static int getDownloadPercent(long downloadId) {
        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query().setFilterById(downloadId);
        Cursor c = mDownloadManager.query(query);
        if (c.moveToFirst()) {
            int downloadBytesIdx = c.getColumnIndexOrThrow(
                    android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int totalBytesIdx = c.getColumnIndexOrThrow(
                    android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            long totalBytes = c.getLong(totalBytesIdx);
            long downloadBytes = c.getLong(downloadBytesIdx);
            return (int) (downloadBytes * 100 / totalBytes);
        }
        return 0;
    }

}
