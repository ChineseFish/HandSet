package com.tongda.printer.ReceiptPrinter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tongda.base.Utils;
import com.tongda.printer.MainActivity;
import com.tongda.printer.R;
import com.tongda.printer.XyyPrinter;

import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.TaskCallback;
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

public class R58Activity extends Activity implements View.OnClickListener {


    private Button sample, text, barcode, qrcode, bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        setContentView(R.layout.ziubao_printer_activity_r58);
        initview();
    }

    private void initview() {
        sample = findViewById(R.id.bt_rcp);
        text = findViewById(R.id.bt_58text);
        barcode = findViewById(R.id.bt_58barcode);
        qrcode = findViewById(R.id.bt_58qr);
        bitmap = findViewById(R.id.bt_58bitmap);

        sample.setOnClickListener(this);
        text.setOnClickListener(this);
        barcode.setOnClickListener(this);
        qrcode.setOnClickListener(this);
        bitmap.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.bt_rcp) {
            Thread printBillThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    //
                    Looper.prepare();

                    //
                    String content = Utils.getJson(getApplicationContext(), "printContent.json");

                    //
                    try {
                        printBill(new JSONArray(content));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //
                    Looper.loop();
                }
            });

            printBillThread.start();
        }

        if (id == R.id.bt_58text) {
            printText();
        }

        if (id == R.id.bt_58barcode) {
            printBarcode();
        }

        if (id == R.id.bt_58qr) {
            printqr();
        }

        if (id == R.id.bt_58bitmap) {

            printBitmap();

        }
    }

    /**
     * 打印消费单
     */
    public void printBill(JSONArray content)
    {
        if (XyyPrinter.isConnected) {
            XyyPrinter.myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "打印数据解析出现错误, " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    //
                    return list;
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打印文本
     */
    private void printText() {

        if (XyyPrinter.isConnected) {
            XyyPrinter.myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    List<byte[]> list = new ArrayList<>();
                    list.add(DataForSendToPrinterPos58.initializePrinter());
                    list.add(StringUtils.strTobytes("1234567890qwertyuiopakjbdscm nkjdv mcdskjb"));
                    list.add(DataForSendToPrinterPos58.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 打印一维条码
     */
    private void printBarcode() {
        if (XyyPrinter.isConnected) {
            XyyPrinter.myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    List<byte[]> list = new ArrayList<>();
                    //初始化打印机，清除缓存
                    list.add(DataForSendToPrinterPos58.initializePrinter());
                    //选择对齐方式
                    list.add(DataForSendToPrinterPos58.selectAlignment(1));
                    //选择HRI文字文字
                    list.add(DataForSendToPrinterPos58.selectHRICharacterPrintPosition(02));
                    //设置条码宽度
                    list.add(DataForSendToPrinterPos58.setBarcodeWidth(2));
                    //设置高度
                    list.add(DataForSendToPrinterPos58.setBarcodeHeight(80));
                    //条码的类型和内容，73是code128的类型，请参考说明手册每种类型的规则
                    list.add(DataForSendToPrinterPos58.printBarcode(73, 10, "{B12345678"));
                    //打印指令
                    list.add(DataForSendToPrinterPos58.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打印二维条码
     */
    private void printqr() {
        if (XyyPrinter.isConnected) {
            XyyPrinter.myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    List<byte[]> list = new ArrayList<>();
                    //初始化打印机，清除缓存
                    list.add(DataForSendToPrinterPos58.initializePrinter());
                    //选择对齐方式
                    list.add(DataForSendToPrinterPos58.selectAlignment(1));
                    list.add(DataForSendToPrinterPos80.printQRcode(3, 48, "www.xprinter.net"));
                    list.add(DataForSendToPrinterPos58.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }
    }

    private void printBitmap() {

        final Bitmap bitmap1 = BitmapProcess.compressBmpByYourWidth
                (BitmapFactory.decodeResource(getResources(), R.drawable.bitmap), 300);

        if (XyyPrinter.isConnected) {
            XyyPrinter.myBinder.WriteSendData(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void OnFailed() {
                    Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                }
            }, () -> {
                List<byte[]> list = new ArrayList<>();
                list.add(DataForSendToPrinterPos80.initializePrinter());
                List<Bitmap> blist = new ArrayList<>();
                blist = BitmapProcess.cutBitmap(50, bitmap1);
                for (int i = 0; i < blist.size(); i++) {
                    list.add(DataForSendToPrinterPos80.printRasterBmp(0, blist.get(i), BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Center, 384));
                }
                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                return list;
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_first), Toast.LENGTH_SHORT).show();
        }
    }

}
