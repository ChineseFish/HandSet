package cn.highwillow.iddemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.TwoIdInfoParam;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, ILotusCallBack {

    @BindView(R.id.scanWebView)
    WebView mWebView;
//    @BindView(R.id.btn_qc_scan)
//    Button btn_qc_scan;

    private static final int REQUEST_CODE = 8859;

    private Toast toast;

    private Bitmap photoBitmap = null;
    private View viewToast = null;
    private TextView tv_id_number;
    private ImageView iv_id_photo;

    private String index = "http://123.153.98.82:8088/czjp";

    /**
     * 二维码识别部分
     */
    static final String TAG = "ScannerApiTest";
    public static final String ACTION = "seuic.android.scanner.scannertestreciever";
    static final int SCANNER_KEYCODE = 142;
    public ScanReceiver receiver;
    public IntentFilter filter;

    /**
     * 身份证部分
     */
    private static final int REQUEST_READ_PHONE_STATE = 0; // 请求码
    private UsbDeviceConnection m_UsbDeviceConnection = null;
    private UsbEndpoint m_InEndpoint = null;
    private UsbEndpoint m_OutEndpoint = null;

    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private LotusCardDriver mLotusCardDriver;
    private NfcAdapter m_NfcAdpater;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Handler m_Handler = null;
    private TextView m_txtCommandIndex;
    private ImageView m_imgIdPhoto;
    private Activity m_MainActivity = null;
    private UsbManager m_UsbManager = null;
    private UsbDevice m_LotusCardDevice = null;
    private UsbInterface m_LotusCardInterface = null;
    private UsbDeviceConnection m_LotusCardDeviceConnection = null;
    private final int m_nVID = 1306;
    private final int m_nPID = 20763;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private Boolean m_bUseUsbHostApi = true;
    private Boolean m_bCanUseUsbHostApi = true;
    private String m_strDeviceNode;
    private long m_nDeviceHandle = -1;
    private int m_nSystemVersion = -1;
    private static final String Activity_TAG = "IdDemo";

    private void showToast(String info, int type) {
        if (toast != null && toast.getView().isShown()) {
            toast.cancel();
        }
        if (type == 1) {
            toast = Toasty.success(MainActivity.this, info, Toast.LENGTH_LONG, true);
            toast = new Toast(this);
            tv_id_number.setText(info);
            iv_id_photo.setImageBitmap(photoBitmap);
            toast.setView(viewToast);
            toast.setDuration(Toast.LENGTH_LONG);

        } else if (type == 0) {
            toast = Toasty.error(MainActivity.this, info, Toast.LENGTH_LONG, true);
        } else if (type == 2) {
            toast = Toasty.info(MainActivity.this, info, Toast.LENGTH_LONG, true);
        } else if (type == 3) {
            toast = Toasty.warning(MainActivity.this, info, Toast.LENGTH_LONG, true);
        } else if (type == 4) {
            toast = Toasty.error(MainActivity.this, info, Toast.LENGTH_SHORT, true);
        }
//        toast.setGravity(Gravity.CENTER,0,50);
        toast.show();
    }

    public void AddLog(String strLog) {
        final String strText = strLog;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("ADDLog",strText);
            }
        });

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Util.play(1, 0);
                    Bundle bundle = msg.getData();
                    //获取身份证信息：姓名、性别、出生年、月、日、住址、身份证号、签发机关、有效期开始、结束、（额外信息新地址（一般情况为空））
                    String id = bundle.getString("id");
                    mWebView.loadUrl(index + "/check?code=" + id + "");
                    showToast("身份证号码:\n" + id, 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String loadUrl = getIntent().getStringExtra("url");
        if (loadUrl != null) {
            index = loadUrl;
        }

        initPermission();
        Util.initSoundPool(MainActivity.this);
        initWebViewSetting();
        loadWebView();
        ZXingLibrary.initDisplayOpinion(this);

        initToastView();

        /**
         * 身份证部分
         */
        m_MainActivity = this;

        try {
            m_NfcAdpater = NfcAdapter.getDefaultAdapter(this);
            if (m_NfcAdpater == null) {
                Toast.makeText(this, "Not Found NfcAdapter!", Toast.LENGTH_SHORT)
                        .show();
                // finish();
                // return;
            } else if (!m_NfcAdpater.isEnabled()) {
                Toast.makeText(this, "Please Enabled NfcAdapter",
                        Toast.LENGTH_SHORT).show();
                // finish();
                // return;
            }
        } catch (java.lang.NullPointerException e) {
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addCategory("*/*");
        mFilters = new IntentFilter[]{ndef};// 过滤器
        mTechLists = new String[][]{
                new String[]{MifareClassic.class.getName()},
                new String[]{NfcB.class.getName()},
                new String[]{IsoDep.class.getName()},
                new String[]{NfcA.class.getName()}};// 允许扫描的标签类型

        // 设置USB读写回调 串口可以不用此操作
        m_bCanUseUsbHostApi = SetUsbCallBack();
        if (m_bCanUseUsbHostApi) {
            AddLog("Find  IC Reader!");
            AddLog("Device Node:" + m_strDeviceNode);
        } else {
            AddLog("Not Find  IC Reader!");
        }

        mLotusCardDriver = new LotusCardDriver();


        mLotusCardDriver.m_lotusCallBack = this;
        m_Handler = new Handler() {
            public void handleMessage(Message msg) {
                AddLog(msg.obj.toString());
                super.handleMessage(msg);
            }
        };
        //区分系统版本
        m_nSystemVersion = Integer.parseInt(Build.VERSION.SDK);
//        if(m_nSystemVersion<19)
//            onNewIntent(getIntent());
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }
//        requestPermission();
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        //String tel = mTelephonyMgr.getLine1Number();
        String tel = mTelephonyMgr.getLine1Number();
        if (tel == null) tel = "";
        if (tel.equals("")) {
            tel = mTelephonyMgr.getSubscriberId();
        }
        if (tel == null) tel = "";
        String iccid = mTelephonyMgr.getSimSerialNumber();  //取出ICCID

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
                MainActivity.this.setProgress(progress * 100);
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

    private void initToastView() {
        viewToast = LayoutInflater.from(MainActivity.this).inflate(R.layout.toast_for_id_card, null);
        tv_id_number = viewToast.findViewById(R.id.tv_id_number);
        iv_id_photo = viewToast.findViewById(R.id.iv_id_photo);
    }

    private Boolean SetUsbCallBack() {
        Boolean bResult = false;
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        m_UsbManager = (UsbManager) getSystemService(USB_SERVICE);
        if (null == m_UsbManager)
            return bResult;

        HashMap<String, UsbDevice> deviceList = m_UsbManager.getDeviceList();
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                if ((m_nVID == device.getVendorId())
                        && (m_nPID == device.getProductId())) {
                    m_LotusCardDevice = device;
                    m_strDeviceNode = m_LotusCardDevice.getDeviceName();
                    break;
                }
            }
        }
        if (null == m_LotusCardDevice)
            return bResult;
        m_LotusCardInterface = m_LotusCardDevice.getInterface(0);
        if (null == m_LotusCardInterface)
            return bResult;
        if (false == m_UsbManager.hasPermission(m_LotusCardDevice)) {
            m_UsbManager.requestPermission(m_LotusCardDevice, pendingIntent);
        }
        UsbDeviceConnection conn = null;
        if (m_UsbManager.hasPermission(m_LotusCardDevice)) {
            conn = m_UsbManager.openDevice(m_LotusCardDevice);
        }

        if (null == conn)
            return bResult;

        if (conn.claimInterface(m_LotusCardInterface, true)) {
            m_LotusCardDeviceConnection = conn;
        } else {
            conn.close();
        }
        if (null == m_LotusCardDeviceConnection)
            return bResult;
        // 把上面获取的对性设置到接口中用于回调操作
        m_UsbDeviceConnection = m_LotusCardDeviceConnection;
        if (m_LotusCardInterface.getEndpoint(1) != null) {
            m_OutEndpoint = m_LotusCardInterface.getEndpoint(1);
        }
        if (m_LotusCardInterface.getEndpoint(0) != null) {
            m_InEndpoint = m_LotusCardInterface.getEndpoint(0);
        }
        bResult = true;
        return bResult;
    }

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    @TargetApi(19)
    private void enableReaderMode() {
        if (m_nSystemVersion < 19)
            return;
        Bundle options = new Bundle();
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
        //int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        if (m_NfcAdpater != null) {
            m_NfcAdpater.enableReaderMode(this, new IdReaderCallback(), READER_FLAGS, options);
        }
    }

    @TargetApi(19)
    public class IdReaderCallback implements NfcAdapter.ReaderCallback {

        @Override
        public void onTagDiscovered(Tag tag) {
            boolean bResult = false;
            boolean bWlDecodeResult = false;
            String temp;
            int nErrorCode = 0;

            NfcB nfcbId = NfcB.get(tag);
            byte[] arrRequest = new byte[3];
            byte[] arrSelect = new byte[9];
            byte[] arrResult;
            byte[] arrBmpAndWl = null;
            arrRequest[0] = (byte) 0x5;
            arrRequest[1] = (byte) 0x0;
            arrRequest[2] = (byte) 0x0;

            arrSelect[0] = (byte) 0x1D;//1D 00 00 00 00 00 08 01 08
            arrSelect[1] = (byte) 0x0;
            arrSelect[2] = (byte) 0x0;
            arrSelect[3] = (byte) 0x0;
            arrSelect[4] = (byte) 0x0;
            arrSelect[5] = (byte) 0x0;
            arrSelect[6] = (byte) 0x8;
            arrSelect[7] = (byte) 0x1;
            arrSelect[8] = (byte) 0x8;

            if (nfcbId != null) {
                try {
                    nfcbId.connect();
                    if (nfcbId.isConnected())
                        AddLog("connect");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    AddLog("身份证放置时间过短");
                    e.printStackTrace();
                }
                if (nfcbId.isConnected()) {
                    TwoIdInfoParam tTwoIdInfo = new TwoIdInfoParam();
                    try {
                        arrResult = nfcbId.transceive(arrRequest);
                        AddLog("length" + arrResult.length);
                        arrResult = nfcbId.transceive(arrSelect);
                        AddLog("length" + arrResult.length);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (m_nDeviceHandle == -1) {
                        m_nDeviceHandle = mLotusCardDriver.OpenDevice("", 0, 0,
                                0, 0,// 使用内部默认超时设置
                                true);

                    }
                    bResult = mLotusCardDriver.GetTwoIdInfoByMcuServer(m_MainActivity, nfcbId,
                            m_nDeviceHandle, "119.29.18.30", 10019,
                            //m_nDeviceHandle, "192.168.1.21", 10019,
                            "15601582869", tTwoIdInfo, Long.valueOf(400000), 0, 3, false);
                    if (!bResult) {
                        nErrorCode = mLotusCardDriver.GetTwoIdErrorCode(m_nDeviceHandle);
                        AddLog("Call GetTwoIdInfoByMcuServer Error! ErrorCode:" + nErrorCode);
                        AddLog("ErrorInfo:" + mLotusCardDriver.GetIdErrorInfo(nErrorCode));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("身份证放置时间过短！",4);
                            }
                        });
                        return;
                    }
                    AddLog("Call GetTwoIdInfoByMcuServer Ok!");
                    //处理照片  网络解码
                    if (0x00 == tTwoIdInfo.unTwoIdPhotoJpegLength) {
                        bWlDecodeResult = mLotusCardDriver.WlDecodeByServer(m_nDeviceHandle, "119.29.18.30", tTwoIdInfo);
                        if (!bWlDecodeResult) {
                            AddLog("Call WlDecodeByServer Error! ");
                        } else {
                            AddLog("Call WlDecodeByServer Ok!");
                        }

                    }
                    if (true == bResult) {
//                        // 姓名
                        try {
//                            temp = new String(tTwoIdInfo.arrTwoIdName, 0, 30,
//                                    "UTF-16LE").trim();
//                            if (temp.equals("")) {
//                                AddLog("数据为空");
//                                return;
//                            }
//                            AddLog("姓名:" + temp);
//
//                            // 性别
//                            temp = new String(tTwoIdInfo.arrTwoIdSex, 0, 2,
//                                    "UTF-16LE").trim();
//                            if (temp.equals("1"))
//                                temp = "男";
//                            else
//                                temp = "女";
//                            AddLog("性别:" + temp);
//                            // 民族
//                            temp = new String(tTwoIdInfo.arrTwoIdNation, 0, 4,
//                                    "UTF-16LE").trim();
//                            try {
//                                int code = Integer.parseInt(temp.toString());
//                                temp = decodeNation(code);
//                            } catch (Exception e) {
//                                temp = "";
//                            }
//                            AddLog("民族:" + temp);
//                            // 出生日期
//                            temp = new String(tTwoIdInfo.arrTwoIdBirthday, 0,
//                                    16, "UTF-16LE").trim();
//                            AddLog("出生日期:" + temp);
//                            // 住址
//                            temp = new String(tTwoIdInfo.arrTwoIdAddress, 0,
//                                    70, "UTF-16LE").trim();
//                            AddLog("住址:" + temp);
//                            // 签发机关
//                            temp = new String(
//                                    tTwoIdInfo.arrTwoIdSignedDepartment, 0, 30,
//                                    "UTF-16LE").trim();
//                            AddLog("签发机关:" + temp);
//                            // 有效期起始日期
//                            temp = new String(
//                                    tTwoIdInfo.arrTwoIdValidityPeriodBegin, 0,
//                                    16, "UTF-16LE").trim();
//                            AddLog("有效期起始日期:" + temp);
//                            // 有效期截止日期 UNICODE YYYYMMDD 有效期为长期时存储“长期”
//                            temp = new String(
//                                    tTwoIdInfo.arrTwoIdValidityPeriodEnd, 0,
//                                    16, "UTF-16LE").trim();
//                            AddLog("有效期截止日期:" + temp);
//                            if (tTwoIdInfo.unTwoIdPhotoJpegLength > 0) {
//                                Bitmap photo = BitmapFactory.decodeByteArray(
//                                        tTwoIdInfo.arrTwoIdPhotoJpeg, 0,
//                                        tTwoIdInfo.unTwoIdPhotoJpegLength);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        m_imgIdPhoto.setBackgroundDrawable(new BitmapDrawable(photo));
//                                    }
//                                });
//                            }

//                            //显示BMP
//
//                        final Bitmap photo = BitmapFactory.decodeByteArray(
//                                arrBmpAndWl, 0,
//                                38862);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        m_imgIdPhoto.setBackgroundDrawable(new BitmapDrawable(photo));
//                                    }
//                                });
//                          身份证号码
                            temp = new String(tTwoIdInfo.arrTwoIdNo, 0, 36,
                                    "UTF-16LE").trim();
                            AddLog("身份证号码:" + temp);
                            String id = temp;
                            Bitmap photo = null;
                            if (tTwoIdInfo.unTwoIdPhotoJpegLength > 0) {
                                photo = BitmapFactory.decodeByteArray(
                                        tTwoIdInfo.arrTwoIdPhotoJpeg, 0,
                                        tTwoIdInfo.unTwoIdPhotoJpegLength);
                            }
                            if(photo!=null){
                                sendMessage(id, photo);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("照片为空",0);
                                    }
                                });
                            }
                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    } else {
                        AddLog("GetTwoIdInfoByMcuServer执行失败");
                    }

                }
            }
        }
    }

    private void sendMessage(String id, Bitmap bitmap) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        photoBitmap = bitmap;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean bResult = false;
        boolean bWlDecodeResult = false;
        String temp;
        int nErrorCode = 0;
        Log.d(Activity_TAG, intent.getAction());
        Log.i(Activity_TAG, "onNewIntent");
        if (m_nSystemVersion >= 19)
            return;

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcB nfcbId = NfcB.get(tagFromIntent);
            if (nfcbId != null) {
                try {
                    nfcbId.connect();
                    if (nfcbId.isConnected())
                        AddLog("connect");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (nfcbId.isConnected()) {
                    TwoIdInfoParam tTwoIdInfo = new TwoIdInfoParam();
                    if (m_nDeviceHandle == -1) {
                        m_nDeviceHandle = mLotusCardDriver.OpenDevice("", 0, 0,
                                0, 0,// 使用内部默认超时设置
                                true);
                    }
                    bResult = mLotusCardDriver.GetTwoIdInfoByMcuServer(this, nfcbId,
                            m_nDeviceHandle, "119.29.18.30", 10019,
                            "15601582869", tTwoIdInfo, 400000, 0, 2, false);

                    if (!bResult) {
                        nErrorCode = mLotusCardDriver.GetTwoIdErrorCode(m_nDeviceHandle);
                        AddLog("Call GetTwoIdInfoByMcuServer Error! ErrorCode:" + nErrorCode);
                        AddLog("ErrorInfo:" + mLotusCardDriver.GetIdErrorInfo(nErrorCode));
                        return;
                    }
                    AddLog("Call GetTwoIdInfoByMcuServer Ok!");
                    //处理照片
                    if (0x00 == tTwoIdInfo.unTwoIdPhotoJpegLength) {
                        bWlDecodeResult = mLotusCardDriver.WlDecodeByServer(m_nDeviceHandle, "119.29.18.30", tTwoIdInfo);
                        if (!bWlDecodeResult) {
                            AddLog("Call WlDecodeByServer Error! ");
                        } else {
                            AddLog("Call WlDecodeByServer Ok!");
                        }

                    }
                    if (true == bResult) {
                        // 姓名
                        try {
                            temp = new String(tTwoIdInfo.arrTwoIdName, 0, 30,
                                    "UTF-16LE").trim();
                            if (temp.equals("")) {
                                AddLog("数据为空");
                                return;
                            }
                            AddLog("姓名:" + temp);

                            // 性别
                            temp = new String(tTwoIdInfo.arrTwoIdSex, 0, 2,
                                    "UTF-16LE").trim();
                            if (temp.equals("1"))
                                temp = "男";
                            else
                                temp = "女";
                            AddLog("性别:" + temp);
                            // 民族
                            temp = new String(tTwoIdInfo.arrTwoIdNation, 0, 4,
                                    "UTF-16LE").trim();
                            try {
                                int code = Integer.parseInt(temp.toString());
                                temp = decodeNation(code);
                            } catch (Exception e) {
                                temp = "";
                            }
                            AddLog("民族:" + temp);
                            // 出生日期
                            temp = new String(tTwoIdInfo.arrTwoIdBirthday, 0,
                                    16, "UTF-16LE").trim();
                            AddLog("出生日期:" + temp);
                            // 住址
                            temp = new String(tTwoIdInfo.arrTwoIdAddress, 0,
                                    70, "UTF-16LE").trim();
                            AddLog("住址:" + temp);
                            // 身份证号码
                            temp = new String(tTwoIdInfo.arrTwoIdNo, 0, 36,
                                    "UTF-16LE").trim();
                            AddLog("身份证号码:" + temp);
                            // 签发机关
                            temp = new String(
                                    tTwoIdInfo.arrTwoIdSignedDepartment, 0, 30,
                                    "UTF-16LE").trim();
                            AddLog("签发机关:" + temp);
                            // 有效期起始日期
                            temp = new String(
                                    tTwoIdInfo.arrTwoIdValidityPeriodBegin, 0,
                                    16, "UTF-16LE").trim();
                            AddLog("有效期起始日期:" + temp);
                            // 有效期截止日期 UNICODE YYYYMMDD 有效期为长期时存储“长期”
                            temp = new String(
                                    tTwoIdInfo.arrTwoIdValidityPeriodEnd, 0,
                                    16, "UTF-16LE").trim();
                            AddLog("有效期截止日期:" + temp);
                            if (tTwoIdInfo.unTwoIdPhotoJpegLength > 0) {
                                Bitmap photo = BitmapFactory.decodeByteArray(
                                        tTwoIdInfo.arrTwoIdPhotoJpeg, 0,
                                        tTwoIdInfo.unTwoIdPhotoJpegLength);
//                                m_imgIdPhoto.setBackgroundDrawable(new BitmapDrawable(photo));
                            }

                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    } else {
                        AddLog("GetTwoIdInfoByMcuServer执行失败");
                    }
                }
            }
        }
    }

    @TargetApi(19)
    private void disableReaderMode() {
        if (m_nSystemVersion < 19)
            return;

        if (m_NfcAdpater != null) {
            m_NfcAdpater.disableReaderMode(this);
        }
    }

    private String decodeNation(int code) {
        String nation;
        switch (code) {
            case 1:
                nation = "汉";
                break;
            case 2:
                nation = "蒙古";
                break;
            case 3:
                nation = "回";
                break;
            case 4:
                nation = "藏";
                break;
            case 5:
                nation = "维吾尔";
                break;
            case 6:
                nation = "苗";
                break;
            case 7:
                nation = "彝";
                break;
            case 8:
                nation = "壮";
                break;
            case 9:
                nation = "布依";
                break;
            case 10:
                nation = "朝鲜";
                break;
            case 11:
                nation = "满";
                break;
            case 12:
                nation = "侗";
                break;
            case 13:
                nation = "瑶";
                break;
            case 14:
                nation = "白";
                break;
            case 15:
                nation = "土家";
                break;
            case 16:
                nation = "哈尼";
                break;
            case 17:
                nation = "哈萨克";
                break;
            case 18:
                nation = "傣";
                break;
            case 19:
                nation = "黎";
                break;
            case 20:
                nation = "傈僳";
                break;
            case 21:
                nation = "佤";
                break;
            case 22:
                nation = "畲";
                break;
            case 23:
                nation = "高山";
                break;
            case 24:
                nation = "拉祜";
                break;
            case 25:
                nation = "水";
                break;
            case 26:
                nation = "东乡";
                break;
            case 27:
                nation = "纳西";
                break;
            case 28:
                nation = "景颇";
                break;
            case 29:
                nation = "柯尔克孜";
                break;
            case 30:
                nation = "土";
                break;
            case 31:
                nation = "达斡尔";
                break;
            case 32:
                nation = "仫佬";
                break;
            case 33:
                nation = "羌";
                break;
            case 34:
                nation = "布朗";
                break;
            case 35:
                nation = "撒拉";
                break;
            case 36:
                nation = "毛南";
                break;
            case 37:
                nation = "仡佬";
                break;
            case 38:
                nation = "锡伯";
                break;
            case 39:
                nation = "阿昌";
                break;
            case 40:
                nation = "普米";
                break;
            case 41:
                nation = "塔吉克";
                break;
            case 42:
                nation = "怒";
                break;
            case 43:
                nation = "乌孜别克";
                break;
            case 44:
                nation = "俄罗斯";
                break;
            case 45:
                nation = "鄂温克";
                break;
            case 46:
                nation = "德昂";
                break;
            case 47:
                nation = "保安";
                break;
            case 48:
                nation = "裕固";
                break;
            case 49:
                nation = "京";
                break;
            case 50:
                nation = "塔塔尔";
                break;
            case 51:
                nation = "独龙";
                break;
            case 52:
                nation = "鄂伦春";
                break;
            case 53:
                nation = "赫哲";
                break;
            case 54:
                nation = "门巴";
                break;
            case 55:
                nation = "珞巴";
                break;
            case 56:
                nation = "基诺";
                break;
            case 97:
                nation = "其他";
                break;
            case 98:
                nation = "外国血统中国籍人士";
                break;
            default:
                nation = "";
        }

        return nation;
    }

    public String toHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;

        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    public String toHexStringR(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];

        int x = 0;
        for (int i = s + n - 1; i >= s; --i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 红外二维码识别部分
         */
        receiver = new ScanReceiver();
        filter = new IntentFilter(ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);

        /**
         * 身份证部分
         */
        Log.i(Activity_TAG, "This is Information");
        if (m_NfcAdpater != null) {
            m_NfcAdpater.enableForegroundDispatch(this, pendingIntent, mFilters,
                    mTechLists);
            enableReaderMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
        * 二维码部分
        */
        unregisterReceiver(receiver);
        /**
         * 身份证部分
         */
        if (m_NfcAdpater != null) {
            m_NfcAdpater.disableForegroundDispatch(this);
            disableReaderMode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    @OnClick({R.id.btn_qc_scan})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_qc_scan:
//                break;
//        }
//    }

    private void initPermission() {
        //所要申请的权限
        String[] perms =
                {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
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
    public boolean callBackExtendIdDeviceProcess(Object objUser, byte[] arrBuffer) {
        // TODO Auto-generated method stub
        boolean bResult = false;
        NfcB nfcbId = (NfcB) objUser;
        if (null == nfcbId)
            return false;
        byte[] arrCommnad = new byte[arrBuffer[0]];
        System.arraycopy(arrBuffer, 1, arrCommnad, 0, arrBuffer[0]);
        //AddLog("执行指令"+arrCommnad.length);
        try {
            //Log.i("LOTUS_CARD_DRIVER","Send   "+toHexString(arrCommnad, 0, arrCommnad.length));
            byte[] arrResult = nfcbId.transceive(arrCommnad);
            //Log.i("LOTUS_CARD_DRIVER","Receive "+toHexString(arrResult, 0, arrResult.length));
            if (LotusCardDriver.isZero(arrResult)) {
                AddLog("读取卡片数据全部为0");

            } else {
                if (arrResult.length > 2) {
                    if (((byte) 0x90 == arrResult[arrResult.length - 3]) &&
                            ((byte) 0x00 == arrResult[arrResult.length - 2]) &&
                            ((byte) 0x00 == arrResult[arrResult.length - 1])) {
                        arrBuffer[0] = (byte) (arrResult.length - 1);
                    } else {
                        arrBuffer[0] = (byte) (arrResult.length);
                    }
                } else {
                    arrBuffer[0] = (byte) (arrResult.length);
                }

                System.arraycopy(arrResult, 0, arrBuffer, 1, arrBuffer[0]);
                bResult = true;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AddLog(e.getMessage());
        }
        return bResult;
    }

    @Override
    public boolean callBackReadWriteProcess(long nDeviceHandle, boolean bRead, byte[] arrBuffer) {
        int nResult = 0;
        boolean bResult = false;
        int nBufferLength = arrBuffer.length;
        int nWaitCount = 0;
        if (null == m_UsbDeviceConnection)
            return false;
        if (null == m_OutEndpoint)
            return false;
        if (null == m_InEndpoint)
            return false;
        //AddLog("callBackReadWriteProcess nBufferLength:" + nBufferLength);
        if (nBufferLength < 65)
            return false;
        if (true == bRead) {
            arrBuffer[0] = 0;
            while (true) {
                nResult = m_UsbDeviceConnection.bulkTransfer(m_InEndpoint,
                        arrBuffer, 64, 5000);
                if (nResult <= 0)
                    break;
                if (arrBuffer[0] != 0) {
                    //此处调整一下
                    System.arraycopy(arrBuffer, 0, arrBuffer, 1, nResult);
                    arrBuffer[0] = (byte) nResult;
                    break;
                }
                nWaitCount++;
                if (nWaitCount > 1000)
                    break;
            }
            if (nResult < 64) AddLog("m_InEndpoint bulkTransfer Read:" + nResult);
            if (nResult == 64) {
                bResult = true;
            } else {
                bResult = false;
            }
        } else {
            nResult = m_UsbDeviceConnection.bulkTransfer(m_OutEndpoint,
                    arrBuffer, 64, 3000);
            //AddLog("m_OutEndpoint bulkTransfer Write:"+nResult);
            if (nResult == 64) {
                bResult = true;
                //AddLog("m_OutEndpoint bulkTransfer Write Ok!");
            } else {
                bResult = false;
            }
        }
        return bResult;
    }

    public class ScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION)){
                String result = intent.getStringExtra("barcode");
                Util.play(1, 0);
                mWebView.loadUrl(index + "/check?code=" + result + "");
            }
//            showToast(result,2);
//            mEditText.append("barcode: "
//                    + bundle.getString(ScannerService.BAR_CODE) + "\ntype: "
//                    + bundle.getString(ScannerService.CODE_TYPE) + "\nlength: "
//                    + bundle.getInt(ScannerService.LENGTH) + "\n");
        }
    }

}
