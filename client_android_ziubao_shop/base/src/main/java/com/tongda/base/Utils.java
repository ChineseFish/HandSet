package com.tongda.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;
import com.maning.updatelibrary.InstallUtils;

public class Utils {



    /**
     * 得到json文件中的内容
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        // 获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        // 使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName),"utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        return stringBuilder.toString();
    }

    /**
     * 警告
     * @param context
     * @param ifNeedAsync
     * @param title
     * @param content
     */
    public static void alert(final Context context, final boolean ifNeedAsync, final String title, final String content)
    {
        if(ifNeedAsync)
        {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 已在主线程中，可以更新UI
                    new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(content)
                            .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing - it will close on its own
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            });
        }
        else
        {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(content)
                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing - it will close on its own
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * 检测版本更新
     */
    public static int getLocalAppVersion(Context context) {
        try
        {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (Exception e) {
            //
            alert(context, true, "获取app版本号失败", e.toString());

            //
            return Integer.MAX_VALUE;
        }
    }

    public static void checkApkUpdate(final Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();

            //
            if(haveInstallPermission) {
                //
                tryToUpdateApk(context);
            }
            else {
                new AlertDialog.Builder(context)
                    .setTitle("重要提示")
                    .setMessage("自游宝需要自动检测安装更新的权限，\n请务必打开！")
                    .setPositiveButton("现在打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 提示用户去手动打开权限
                            Uri packageURI = Uri.parse("package:" + context.getPackageName());

                            //
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                            context.startActivity(intent);

                            //
                            dialog.dismiss();

                            //
                            tryToUpdateApk(context);
                        }
                    })
                    .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing - it will close on its own
                        }
                    })
                    .setCancelable(false)
                    .show();
            }
        }
    }

    private static void tryToUpdateApk(final Context context) {
        //
        File saveDir = new File(Constants.apkSavePath);
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }

        // downloaded apk
        File file = new File(Constants.apkFilePath);

        //
        if (file.exists()) {
            // fetch downloaded app version code
            int downloadedApkVersionCode = 0;
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(Constants.apkFilePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                downloadedApkVersionCode = info.versionCode;
            }

            // check if app need update
            if (downloadedApkVersionCode > getLocalAppVersion(context)) {
                try {
                    // install apk
                    InstallUtils.installAPK((Activity) context, Constants.apkFilePath, new InstallUtils.InstallCallBack() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "正在安装", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(Exception e) {
                            Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    //
                    file.delete();

                    //
                    alert(context, true, "安装新版本app出现问题", e.toString());
                }
            } else {
                // delete obsoleted downloaded apk
                file.delete();
            }
        } else {
            checkVersion(context);
        }
    }

    private static void checkVersion(final Context context) {

        //
        Thread thread = new Thread() {
            @Override
            public void run() {

                //
                URL url;
                HttpURLConnection connection = null;

                //
                try {
                    //
                    url = new URL(Constants.CheckVersionUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);

                    //
                    InputStream in = connection.getInputStream();

                    // read response data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // translate to JSON format
                    JSONObject json = new JSONObject(response.toString());

                    String serverVersion = json.getString("version");

                    // download apk
                    if (serverVersion != null && !"".equals(serverVersion)) {
                        if (getLocalAppVersion(context) < Integer.valueOf(serverVersion)) {

                            //
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(context)
                                            .setTitle("更新提示")
                                            .setMessage("自游宝有新的版本，尽快下载更新哦！")
                                            .setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //
                                                    dialog.dismiss();

                                                    //
                                                    updateApk(context);
                                                }
                                            })
                                            .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing - it will close on its own
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        }
                    }
                } catch (MalformedURLException e) {
                    Utils.alert(context, true, "更新请求失败", e.toString());
                } catch (IOException | JSONException e) {
                    Utils.alert(context, true, "更新请求失败", e.toString());
                } catch (Exception e) {
                    Utils.alert(context, true, "更新请求失败", e.toString());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        };

        thread.start();
    }

    private static void updateApk(final Context context) {
        /**
         * download dialog
         */
        // create download dialog
        final DownLoadDialog.Builder builder = new DownLoadDialog.Builder(context);
        final DownLoadDialog dialog = builder.create();
        // disable cancel
        dialog.setCanceledOnTouchOutside(false);
        // init download progress bar
        final ZzHorizontalProgressBar pb = builder.getPb();
        pb.setProgress(0);
        // set padding
        pb.setPadding(0);
        // set max value
        pb.setMax(100);
        // show
        dialog.show();

        /**
         * apk update utils
         */
        InstallUtils.with(context)
                // 必须-下载地址
                .setApkUrl(Constants.ApkDownloadUrl)
                // 非必须-下载保存的路径
                .setApkPath(Constants.apkFilePath)
                // 非必须-下载回调
                .setCallBack(new InstallUtils.DownloadCallBack() {
                    @Override
                    public void onStart() {
                        // 下载开始
                        Toast.makeText(context, "开始安装", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(String path) {
                        // 下载完成
                        pb.setProgress(100);

                        //
                        dialog.dismiss();

                        // install apk
                        InstallUtils.installAPK((Activity) context, path, new InstallUtils.InstallCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "正在安装", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLoading(long total, long current) {
                        // 下载中
                        pb.setProgress((int) Math.round(((double) current / (double) total) * 100));
                    }

                    @Override
                    public void onFail(Exception e) {
                        dialog.dismiss();

                        // 下载失败
                        alert(context, true, "下载失败，请重试", e.toString());
                    }

                    @Override
                    public void cancle() {
                        dialog.dismiss();

                        // 下载取消
                        Toast.makeText(context, "下载取消", Toast.LENGTH_SHORT).show();
                    }
                }).startDownload();
    }


    public static JSONObject httpGet(String requestUrl) throws Exception
    {
        //
        Exception exp = null;
        JSONObject resJSON = null;

        //
        HttpURLConnection connection = null;

        //
        try {
            // 创建url资源
            URL url = new URL(requestUrl);
            // 建立http连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置传递方式
            connection.setRequestMethod("GET");
            //
            connection.setInstanceFollowRedirects(true);
            // 设置文件类型
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            // 设置cookie
            connection.setRequestProperty("Cookie", Constants.cookie);

            //
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            // read response data
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            //
            connection.disconnect();

            // translate to JSON format
            resJSON = new JSONObject(response.toString());
        } catch(Exception e)
        {
            exp = e;
        } finally {
            if(connection != null)
            {
                connection.disconnect();
            }
        }

        //
        if(exp != null)
        {
            throw exp;
        }

        //
        return resJSON;
    }

    public static JSONObject httpPostJson(String requestUrl, String postData) throws Exception
    {
        //
        Exception exp = null;
        JSONObject resJSON = null;

        //
        HttpURLConnection connection = null;

        //
        try {
            // 创建url资源
            URL url = new URL(requestUrl);
            // 建立http连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 设置不用缓存
            connection.setUseCaches(false);
            // 设置传递方式
            connection.setRequestMethod("POST");
            // 设置维持长连接
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            connection.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = postData.getBytes();
            // 设置文件类型
            connection.setRequestProperty("Content-Type", "application/json");
            // 设置文件长度
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置cookie
            connection.setRequestProperty("Cookie", Constants.cookie);

            // 开始连接请求
            connection.connect();

            //
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            // 写入请求的字符串
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("utf-8"));
            os.flush();
            os.close();

            // read response data
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            //
            resJSON = new JSONObject(response.toString());
        } catch(Exception e)
        {
            exp = e;
        } finally {
            if(connection != null)
            {
                connection.disconnect();
            }
        }

        //
        if(exp != null)
        {
            throw exp;
        }

        //
        return resJSON;
    }

    public static Bitmap getBitmap(String url) {
        URL imageURL = null;
        Bitmap bitmap = null;

        try {
            imageURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) imageURL
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
