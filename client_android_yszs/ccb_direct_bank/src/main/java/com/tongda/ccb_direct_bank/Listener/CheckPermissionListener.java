package com.tongda.ccb_direct_bank.Listener;

/**
 * Created by wutw on 2018/5/28 0028.
 */
public interface CheckPermissionListener {

    public void onPermissionGranted(int requestCode);

    public void onPermissionDeny(int requestCode);

    public void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults);
}
