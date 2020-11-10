package com.tongda.ccb_direct_bank.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.IOException;


public class CameraApi {
    public static final int REQUEST_CODE_CAMERA = 1147;
    public static final int REQUEST_CODE_OPEN_ALBUM = 1148;

    /**
     * 打开摄像头进行拍照,并将文件保持到指定位置
     * 
     * @param act 当前活动
     * @param requestCode 请求码
     */
    public static void requestCameraTakePhoto(Activity act, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        act.startActivityForResult(intent, requestCode);
    }
    
    /**
     * 打开摄像头进行拍照,并将文件保持到指定位置
     * 
     * @param act 当前活动
     * @param requestCode 请求码
     * @param imageUri 拍照后照片存储的位置
     */
    public static void requestCameraTakePhoto(Activity act, int requestCode, Uri imageUri) {
        Intent intent = getCameraTakePhotoIntent(imageUri);
        act.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent getCameraTakePhotoIntent(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return intent;
    }

    public static void requestCameraTakePhotoWithCheck(Activity act, int requestCode, Uri imageUri) throws Exception{
        Intent intent = getCameraTakePhotoIntent(imageUri);
        if (intent.resolveActivity(act.getPackageManager()) == null) {
            throw new IllegalStateException("camera is not support now");
        } else {
            act.startActivityForResult(intent, requestCode);
        }
    }


    /**
     * 获取摄像头拍摄的图片资源
     */
    public static Uri getTakenPhoto(Context context ,File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            String authority = context.getPackageName() + ".fileprovider";
            Log.d("TAG:","packname =" + context.getPackageName());
            uri = FileProvider.getUriForFile(context.getApplicationContext(),authority , file);
            //content://包名.fileprovider/files_path/files/xxx.pdf
            //content://com.ccb.a31androidcamera.fileprovider/lgc/storage/emulated/0/camera_test2.png
            //和xml配置文件中的对应
            //name对应到链接中的files_path
            //path对应到链接中的 files ,当然files是在honjane/目录下
        } else {
            uri = Uri.fromFile(file);
        }
        Log.d("TAG:","FileProvider get uri= "+uri);
        return uri;
    }

    /**
     * 打开相册获取图片
     * 
     * @param act 当前活动
     * @param requestCode 请求码
     */
    public static void requestOpenAlbum(Activity act, int requestCode) {
        Intent intent = getOpenAlbumIntent();
        act.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent getOpenAlbumIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    /**
     * 打开相册获取图片
     *
     * @param act 当前活动
     * @param requestCode 请求码
     */
    public static void requestOpenAlbumWithCheck(Activity act, int requestCode) throws Exception{
        Intent intent = getOpenAlbumIntent();
        if (intent.resolveActivity(act.getPackageManager()) == null) {
            throw new IllegalStateException("photo gallery is not support now");
        } else {
            act.startActivityForResult(intent, requestCode);
        }
    }


    /**
     * 打开相册获取图片
     * 
     * @param act 当前活动
     * @param requestCode 请求码
     */
    public static void requestOpenAlbum2(Activity act, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        act.startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪图片方法实现,需要改进
     * 
     * @param uri
     */
    public static void startPhotoZoom(Uri uri, Activity act, int requestCode) {
        Intent intent = getPhotoZoomIntent(act,uri);
        act.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent getPhotoZoomIntent(Activity act, Uri uri) {

        //修复6.0截图相册crash的问题
		String s = null;
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), uri);
			s = MediaStore.Images.Media.insertImage(act.getContentResolver(), bitmap, null, null);
		} catch (IOException e) {
			LogUtils.e(e.toString());
		}

		if (!TextUtils.isEmpty(s)) {
			uri = Uri.parse(s);
		}
        
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * 裁剪图片方法实现,需要改进
     *
     * @param uri
     */
    public static void startPhotoZoomWithCheck(Context context, Uri uri, Activity act, int requestCode) throws Exception {
        Intent intent = getPhotoZoomIntent(act,uri);
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            throw new IllegalStateException("camera crop is not support now");
        } else {
            act.startActivityForResult(intent, requestCode);
        }
    }

    public static void startPhotoZoomBg(Uri uri, Activity act, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", true);
        act.startActivityForResult(intent, requestCode);
    }
    /**
     * 从 intent中获取图片数据
     */
    public static Bitmap getBmpFromIntent(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            return extras.getParcelable("data");
        }
        return null;
    }
}
