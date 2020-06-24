package com.handheld.IDCardDemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.IDCard.IDCardManager;
import com.handheld.IDCard.IDCardModel;
import com.handheld.scan.ScanThread;
import com.hdhe.idcarddemo.R;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.EasyPermissions;

public class MatouActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.scanWebView)
    WebView mWebView;
    @BindView(R.id.btn_qc_scan)
    Button btn_qc_scan;

    private static final int REQUEST_CODE = 8859;
    private IDCardManager manager;
    private ReadThread thread;
    private Toast toast;

    private Bitmap photoBitmap = null;
    private View viewToast = null;
    private TextView tv_id_number;
    private ImageView iv_id_photo;

    private void showToast(String info, int type) {
        if (toast != null && toast.getView().isShown()) {
            toast.cancel();
        }
        if (type == 1) {
//            toast = Toasty.success(MatouActivity.this,info,Toast.LENGTH_LONG,true);
            toast = new Toast(this);
            tv_id_number.setText(info);
            iv_id_photo.setImageBitmap(photoBitmap);
            toast.setView(viewToast);
            toast.setDuration(Toast.LENGTH_LONG);

        } else if (type == 0) {
            toast = Toasty.error(MatouActivity.this, info, Toast.LENGTH_LONG, true);
        } else if (type == 2) {
            toast = Toasty.info(MatouActivity.this, info, Toast.LENGTH_LONG, true);
        } else if (type == 3) {
            toast = Toasty.warning(MatouActivity.this, info, Toast.LENGTH_LONG, true);
        }
//        toast.setGravity(Gravity.CENTER,0,50);
        toast.show();
    }

//    private ScanUtil scanUtil ;

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            byte[] data = intent.getByteArrayExtra("data");
//            if (data != null) {
//                String result = new String(data);
//                Log.e("MatouActivity", result);
//                mWebView.loadUrl(index + "check?code=" + result + "");
//            }
//
//        }
//    };

    //红外7.0
    private ScanThread scanThread;
    private KeyReceiver keyReceiver;

    private String index = "http://123.153.98.82:8088/czjp";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Util.play(1, 0);
                    Bundle bundle = msg.getData();
                    //获取身份证信息：姓名、性别、出生年、月、日、住址、身份证号、签发机关、有效期开始、结束、（额外信息新地址（一般情况为空））
                    String id = bundle.getString("id");
                    showToast("身份证号码:\n" + id, 1);
                    mWebView.loadUrl(index + "/check?code=" + id + "");
                    break;
                case 1:
                    showToast("发现身份证!\n正在获取身份证数据...", 2);
                    break;
                case 2:
                    showToast("", 2);
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    };

    private Handler redScanHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {
                String data = msg.getData().getString("data");
//                showToast(data,3);
                if (data != null) {
                    String result = new String(data);
                    Log.e("MatouActivity", result);
                    Util.play(1, 0);
                    mWebView.loadUrl(index + "/check?code=" + result + "");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matou);
        ButterKnife.bind(this);

        String loadUrl = getIntent().getStringExtra("url");
        if(loadUrl != null){
            index = loadUrl;
        }

        initPermission();
        Util.initSoundPool(MatouActivity.this);
        initWebViewSetting();
        loadWebView();
        ZXingLibrary.initDisplayOpinion(this);

        String path = Environment.getExternalStorageDirectory() + "/IDCard";
        File file_paper = new File(path);
        if (!file_paper.exists()) {
            file_paper.mkdirs();
        }
        thread = new ReadThread();
        thread.start();

        initToastView();
        openIdCardScan();

        //红外 5.1
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.rfid.SCAN");
//        registerReceiver(receiver, filter);
//
//        IntentFilter batteryfilter = new IntentFilter();
//        batteryfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        registerReceiver(batteryReceiver, batteryfilter);

        //红外  7.0
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            scanThread = new ScanThread(redScanHandler);
        } catch (Exception e) {
            // 出现异常
            showToast("serialport init fail",3);
            return;
        }
        scanThread.start();
        //注册按键广播接收者
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        registerReceiver(keyReceiver , filter);
    }

    private void initToastView() {
        viewToast = LayoutInflater.from(MatouActivity.this).inflate(R.layout.toast_for_id_card, null);
        tv_id_number = viewToast.findViewById(R.id.tv_id_number);
        iv_id_photo = viewToast.findViewById(R.id.iv_id_photo);
    }

    private void initWebViewSetting() {
        mWebView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        //缓存使用
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
    }

    private void loadWebView() {

        mWebView.loadUrl(index);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                MatouActivity.this.setProgress(progress * 100);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (url.equals(index)) {
                    showToast("请先登录，再扫描", 3);
                }
                return true;
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//                        //表示按返回键时的操作
//                        mWebView.goBack();   //后退
//                        return true;
//                    }

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //表示按返回键时的操作
                        finish();
                        return true;
                    }

                }
                return false;
            }
        });

    }

    private void openIdCardScan() {
        if (manager == null) {
            manager = new IDCardManager(MatouActivity.this);
        }
        startFlag = true;
    }

    private boolean runFlag = true;
    private boolean startFlag = false;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            while (runFlag) {
                if (startFlag && manager != null) {
                    if (manager.findCard(200)) {
                        handler.sendEmptyMessage(1);
                        IDCardModel model = null;
                        //自加
                        //获取身份证信息、图像
                        model = manager.getData(2000);
                        if (model != null) {
                            sendMessage(model.getIDCardNumber(), model.getPhotoBitmap());
                        }
                    }
                }

//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
            super.run();
        }

        private void sendMessage(String id, Bitmap bitmap) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            photoBitmap = bitmap;
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    private void initPermission() {
        //所要申请的权限
        String[] perms =
                {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.VIBRATE
                };

        if (EasyPermissions.hasPermissions(this, perms)) {//检查是否获取该权限
        } else {
            //第二个参数是被拒绝后再次申请该权限的解释
            //第三个参数是请求码
            //第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e("lzan13", "获取成功的权限" + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("lzan13", "获取失败的权限" + perms);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    Util.play(1, 0);
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    mWebView.loadUrl(index + "/check?code=" + result + "");
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    showToast("解析二维码失败", 0);
                }
            }
//            else{
//                showToast("请关闭桌面 Scan2DServer 中的 Scan switch 开关",3);
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!pasue && manager != null) {
            pasue = true;
            manager.close();
            manager = null;
            startFlag = false;
        }
//        if (scanUtil != null) {
//            scanUtil.close();
//            scanUtil = null ;
//        }
    }


    //app暂停
    private boolean pasue = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (pasue) {
            pasue = false;
            if (manager == null) {
                manager = new IDCardManager(MatouActivity.this);
            }
            startFlag = true;
        }
//        if (scanUtil == null) {
//            scanUtil = new ScanUtil(this);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startFlag = false;
        if (manager != null) {
            manager.close();
        }
        runFlag = false;
//        unregisterReceiver(receiver);
//        unregisterReceiver(batteryReceiver);

        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }

        //注销广播接收者
        unregisterReceiver(keyReceiver);

    }

    @OnClick({R.id.btn_qc_scan})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_qc_scan:
                Intent intent = new Intent(MatouActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            scanThread.scan();
        }
        return super.onKeyDown(keyCode, event);
    }

    //    private BroadcastReceiver batteryReceiver = new BroadcastReceiver(){
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int level = intent.getIntExtra("level", 0);
//            Log.e("batteryReceiver", "batteryReceiver level =  " + level);
//        }
//    };

    /**
     * 按键广播接收者 用于接受按键广播 触发扫描
     */
    private boolean mIsPressed = false;
    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            // 为兼容早期版本机器
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown && !mIsPressed) {
                // 根据需要在对应的按键的键值中开启扫描,
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:

                    case KeyEvent.KEYCODE_F2:

                    case KeyEvent.KEYCODE_F3:

                    case KeyEvent.KEYCODE_F4:

                    case KeyEvent.KEYCODE_F5:

                    default:
                        //开启扫描
                        mIsPressed = true;
                        scanThread.scan();
                        break;
                }
            }else {
                mIsPressed = false;
            }
        }
    }

}
