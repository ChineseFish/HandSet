package com.tongda.djidcard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.os.Build.VERSION;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cc.lotuscard.*;

public class DjMainActivity extends Activity implements ILotusCallBack {
    private UsbDeviceConnection m_UsbDeviceConnection = null;
    private UsbEndpoint m_InEndpoint = null;
    private UsbEndpoint m_OutEndpoint = null;

    private LotusCardDriver mLotusCardDriver;
    private NfcAdapter m_NfcAdpater;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Activity m_MainActivity = null;

    private UsbManager m_UsbManager = null;
    private UsbDevice m_LotusCardDevice = null;
    private UsbInterface m_LotusCardInterface = null;
    private UsbDeviceConnection m_LotusCardDeviceConnection = null;
    private final int m_nVID = 1306;
    private final int m_nPID = 20763;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private Boolean m_bCanUseUsbHostApi = true;
    private String m_strDeviceNode;

    private long m_nDeviceHandle = -1;
    private int m_nSystemVersion = -1;
    private int m_nCommandInex = 0;


    private static final String Activity_TAG = "djidcard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        m_MainActivity = this;

        /**
         * 初始化NFC
         */
        try {
            m_NfcAdpater = NfcAdapter.getDefaultAdapter(this);
            if (m_NfcAdpater == null) {
                Toast.makeText(this, "Not Found NfcAdapter!", Toast.LENGTH_SHORT)
                        .show();
            } else if (!m_NfcAdpater.isEnabled()) {
                Toast.makeText(this, "Please Enabled NfcAdapter",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (java.lang.NullPointerException e) {
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        /**
         *
         */
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        /**
         * 设置NFC
         */
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addCategory("*/*");
        // 过滤器
        mFilters = new IntentFilter[]{ ndef };
        // 允许扫描的标签类型
        mTechLists = new String[][]{
                new String[]{MifareClassic.class.getName()},
                new String[]{NfcB.class.getName()},
                new String[]{IsoDep.class.getName()},
                new String[]{NfcA.class.getName()}
        };

        /**
         * 设置USB读写回调 串口可以不用此操作
         */
        m_bCanUseUsbHostApi = SetUsbCallBack();
        if (m_bCanUseUsbHostApi) {
            AddLog("Find IC Reader!");
            AddLog("Device Node:" + m_strDeviceNode);
        } else {
            AddLog("Not Find  IC Reader!");
        }

        /**
         * 初始化东集身份证驱动
         */
        mLotusCardDriver = new LotusCardDriver();
        mLotusCardDriver.m_lotusCallBack = this;

        // 区分系统版本
        m_nSystemVersion = Integer.parseInt(VERSION.SDK);
    }

    public void AddLog(String strLog) {
        Log.d(Activity_TAG, strLog);
    }

    public void showCommandIndex(int nIndex) {
        Log.d(Activity_TAG, "索引" + nIndex);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //
        Log.i(Activity_TAG, "onResume");

        //
        if (m_NfcAdpater != null) {
            m_NfcAdpater.enableForegroundDispatch(this, pendingIntent, mFilters,
                    mTechLists);
            enableReaderMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //
        Log.i(Activity_TAG, "onPause");

        //
        if (m_NfcAdpater != null) {
            m_NfcAdpater.disableForegroundDispatch(this);
            disableReaderMode();
        }
    }

    @TargetApi(19)
    private void enableReaderMode() {
        if (m_nSystemVersion < 19) {
            return;
        }

        //
        Bundle options = new Bundle();
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);

        //
        int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

        //
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
                    m_nCommandInex = 0;
                    bResult = mLotusCardDriver.GetTwoIdInfoByMcuServer(m_MainActivity, nfcbId,
                            m_nDeviceHandle, "119.29.18.30", 10019,
                            // m_nDeviceHandle, "192.168.1.21", 10019,
                            "15601582869", tTwoIdInfo, 400000, 0, 3, false);
                    if (!bResult) {
                        nErrorCode = mLotusCardDriver.GetTwoIdErrorCode(m_nDeviceHandle);
                        AddLog("Call GetTwoIdInfoByMcuServer Error! ErrorCode:" + nErrorCode);
                        AddLog("ErrorInfo:" + mLotusCardDriver.GetIdErrorInfo(nErrorCode));
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

                                final Bitmap photo = BitmapFactory.decodeByteArray(
                                        tTwoIdInfo.arrTwoIdPhotoJpeg, 0,
                                        tTwoIdInfo.unTwoIdPhotoJpegLength);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //
                                        Log.d(Activity_TAG, "get photo");
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

    @TargetApi(19)
    private void disableReaderMode() {
        if (m_nSystemVersion < 19)
            return;

        if (m_NfcAdpater != null) {
            m_NfcAdpater.disableReaderMode(this);
        }
    }

    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        boolean bResult = false;
        boolean bWlDecodeResult = false;
        String temp;
        int nErrorCode = 0;
        Log.d(Activity_TAG, intent.getAction());
        Log.i(Activity_TAG, "onNewIntent");
        if (m_nSystemVersion >= 19)
        {
            return;
        }

        //
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

                    // 处理照片
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

                                Log.d(Activity_TAG, "get photo");
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

    public boolean callBackExtendIdDeviceProcess(Object objUser,
                                                 byte[] arrBuffer) {
        // TODO Auto-generated method stub
        boolean bResult = false;
        NfcB nfcbId = (NfcB) objUser;
        if (null == nfcbId)
            return false;
        byte[] arrCommnad = new byte[arrBuffer[0]];
        System.arraycopy(arrBuffer, 1, arrCommnad, 0, arrBuffer[0]);

        //
        m_nCommandInex ++;
        showCommandIndex(m_nCommandInex);

        //
        try {
            byte[] arrResult = nfcbId.transceive(arrCommnad);
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

            // 应当对加载动画

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AddLog(e.getMessage());
            
            //
            Log.i(Activity_TAG, "身份证扫描失败, 请拿开身份证重新进行扫描");
        }
        return bResult;
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
                    // 此处调整一下
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
            if (nResult == 64) {
                bResult = true;
            } else {
                bResult = false;
            }
        }
        return bResult;
    }
}
