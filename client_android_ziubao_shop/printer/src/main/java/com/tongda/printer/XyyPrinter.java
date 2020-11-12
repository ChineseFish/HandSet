package com.tongda.printer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tongda.base.Utils;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.TaskCallback;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.BitmapProcess;
import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class XyyPrinter {
    //
    private static SharedPreferences mSp;

    //
    private static Context context;

    //
    private static Boolean isInited = false;

    //
    public static IMyBinder myBinder;

    //
    public static Boolean isConnected = false;

    //
    public static ServiceConnection mSerconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (IMyBinder) service;
            Log.e("myBinder", "connect");

            //
            isInited = true;

            //
            String addressType = Db.getAddressType(mSp);
            if(addressType.equals(""))
            {
                Toast.makeText(context, "请先到打印机设置页面连接打印机", Toast.LENGTH_SHORT).show();
            }
            else if(addressType.equals("bt"))
            {
                connectBT(context, Db.getAddress(mSp));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("myBinder", "disconnect");
        }
    };


    public static void init(Context _context) {
        if(isInited)
        {
            Toast.makeText(_context, "打印机已经初始化, 请勿反复初始化", Toast.LENGTH_SHORT).show();

            //
            return;
        }

        //
        context = _context;

        //
        mSp = context.getSharedPreferences("ziubao_printer", context.MODE_PRIVATE);

        // bind service, get imyBinder
        Intent intent = new Intent(context, PosprinterService.class);
        context.bindService(intent, mSerconnection, context.BIND_AUTO_CREATE);
    }

    /**
     * 连接蓝牙
     */
    public static void connectBT(Context context, String address) {
        if (address.equals(null) || address.equals("")) {
            Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
        } else {
            myBinder.ConnectBtPort(address, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    isConnected = true;

                    //
                    Db.writeAddressType(mSp, "bt");
                    Db.writeAddress(mSp, address);

                    //
                    Toast.makeText(context, context.getString(R.string.con_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    isConnected = false;

                    //
                    Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 网络连接
     */
    public static void connectNet(Context context, String ip) {
        if (ip.equals(null) || ip.equals("")) {
            Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
        } else {
            XyyPrinter.myBinder.ConnectNetPort(ip, 9100, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    isConnected = true;

                    //
                    Db.writeAddressType(mSp, "ip");
                    Db.writeAddress(mSp, ip);

                    //
                    Toast.makeText(context, context.getString(R.string.con_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    isConnected = false;
                    Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 连接usb
     */
    public static void connectUSB(Context context, String usbAddress) {
        //
        if (usbAddress.equals(null) || usbAddress.equals("")) {
            Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
        } else {
            XyyPrinter.myBinder.ConnectUsbPort(context, usbAddress, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    isConnected = true;

                    //
                    Db.writeAddressType(mSp, "usb");
                    Db.writeAddress(mSp, usbAddress);

                    //
                    Toast.makeText(context, context.getString(R.string.connect), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    isConnected = false;
                    Toast.makeText(context, context.getString(R.string.discon), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 断开连接
     */
    public static void disConnect(Context context) {
        //
        if (isConnected) {
            XyyPrinter.myBinder.DisconnectCurrentPort(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    isConnected = false;
                    Toast.makeText(context, "disconnect ok", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    isConnected = true;
                    Toast.makeText(context, "disconnect failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 打印消费单
     */
    public static void printBill(Context context, JSONArray content)
    {
        //
        if (isConnected) {
            myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(context, context.getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(context, context.getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    //
                    List<byte[]> list = new ArrayList<>();

                    //
                    try
                    {
                        //
                        for(int i = 0; i < content.length(); i ++)
                        {
                            //
                            JSONObject line = content.getJSONObject(i);

                            // fetch line type
                            String type = line.getString("type");

                            // init printer
                            list.add(DataForSendToPrinterPos58.initializePrinter());

                            //
                            if(type.equals("text"))
                            {
                                //
                                JSONArray lineDetail = line.getJSONArray("content");

                                //
                                for(int j = 0; j < lineDetail.length(); j ++)
                                {
                                    JSONObject item = lineDetail.getJSONObject(j);

                                    //
                                    int posM = item.getInt("posM");
                                    int posN = item.getInt("posN");
                                    int charSize = item.getInt("charSize");
                                    String text = item.getString("text");

                                    //
                                    list.add(DataForSendToPrinterPos58.setAbsolutePrintPosition(posM, posN)); // 设置初始位置
                                    list.add(DataForSendToPrinterPos58.selectCharacterSize(charSize)); // 设置字体大小
                                    list.add(StringUtils.strTobytes(text)); // 设置打印内容
                                }
                            }
                            else if(type.equals("bitmap"))
                            {
                                //
                                String url = line.getString("url");

                                // fetch bitmap
                                Bitmap bitmap = Utils.getBitmap(url);

                                // 按照你给定的宽度来压缩图片 图片宽度大于给定的宽度则压缩, 否则不压缩
                                bitmap = BitmapProcess.compressBmpByYourWidth(bitmap, 300);

                                // 切割图片方法, 等高切割图片, 返回List
                                List<Bitmap> blist = BitmapProcess.cutBitmap(50, bitmap);
                                for (int j = 0; j < blist.size(); j++) {
                                    list.add(DataForSendToPrinterPos80.printRasterBmp(0, blist.get(j), BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Center, 384));
                                }
                            }
                            else if(type.equals("newLine"))
                            {

                            }

                            // 打印并换行
                            list.add(DataForSendToPrinterPos58.printAndFeedLine());
                        }
                    } catch (JSONException e)
                    {
                        Toast.makeText(context, "打印数据解析出现错误, " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    //
                    return list;
                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }
    }
}
