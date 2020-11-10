package com.tongda.ccb_direct_bank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.tongda.ccb_direct_bank.R;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 可以中断交易的通讯对话框
 * Created by LGC on 2017/5/18.
 */

public class LoadingDialogUtils {
    private static final String TAG = LoadingDialogUtils.class.getSimpleName();
    private static LoadingDialogUtils mCcbLoadingDialog = null;
    private Dialog dlg=null;
    private Handler handler =new Handler(Looper.getMainLooper());

    private static Queue<Context> mQueue;

    private Context mContext;

    private LoadingDialogUtils(){
        super();
    }

    public synchronized static LoadingDialogUtils getInstance() {
        if (null == mCcbLoadingDialog) {
            mCcbLoadingDialog = new LoadingDialogUtils();
            mQueue = new LinkedBlockingQueue<Context>();
        }
        return mCcbLoadingDialog;
    }

    private void createLoadingDialog(Context context) {
        mQueue.offer(context);
        Log.d(TAG, String.format("=======================%s request add=====================",context.toString()));
        printQueue();
      
        dlg = new AlertDialog.Builder(context, R.style.Ccb_Theme_Dialog).create();
        dlg.show();

        View view = LayoutInflater.from(context).inflate(R.layout.ccb_direct_bank_loading_dialog, null);
        dlg.setOnKeyListener(mCancelKeyListener);
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(true);
        dlg.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams VG_LP_FW = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(VG_LP_FW);
        LP_FW.gravity = Gravity.CENTER_HORIZONTAL;
        dlg.getWindow().setContentView(view, LP_FW);
        Log.d(TAG, "======================= show loading dialog=====================");
    }

    /**
     * 弹出loading框
     */
    public synchronized  void showLoading(final Context context){
        if (context == null)
            return;

        //非当前界面 清除所有队列
        if(mContext != context){
            reset();
        }

        mContext = context;
        if (dlg != null && dlg.isShowing()) {
            Log.d(TAG, String.format("=======================%s has loading no handle=====================", context
                    .toString()));
            mQueue.offer(context);
            Log.d(TAG, String.format("=======================%s request add=====================",context.toString()));
            printQueue();
        } else {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createLoadingDialog(context);
                }
            });
        }
      
    }

    private void reset() {
        handler.removeCallbacksAndMessages(null);
        dismiss();
        mQueue.clear();
    }

    /**
     * 关闭loading框
     * @return
     */
    public  synchronized boolean dismissLoading(){
        boolean hasRemoved = false;
        if (mQueue.size() > 0) {
            Context currentContext = mQueue.poll();
            Log.d(TAG, String.format("=======================%s request remove=====================", currentContext
                    .toString()));
            hasRemoved = true;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "=======================current queue size=====================" + mQueue.size());
                printQueue();
                if (mQueue.size() > 0) {
                    return;
                }
                dismiss();
            }
        }, 100);

        return hasRemoved;
    }

    private void dismiss(){
        if(null==dlg)
            return;
        dlg.dismiss();
        dlg.cancel();
        dlg = null;
        Log.d(TAG, "======================= dismiss loading dialog=====================");
    }

    private void printQueue(){
        for (Context context : mQueue){
            Log.d(TAG, "======mQueue======"+context.toString());
        }
    }

    /**
     * 显示loading框，同时支持按键事件
     *
     * @param keyListener
     */
    public synchronized void show(Activity activity,DialogInterface.OnKeyListener keyListener) {
        showLoading(activity);
        dlg.setOnKeyListener(keyListener);
    }


    private DialogInterface.OnKeyListener  mCancelKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

            if(keyCode != KeyEvent.KEYCODE_BACK )
                return false;
            reset();
            return  true;
        }
    };

    public boolean isShow(){
        if(null!=dlg){
            return   dlg.isShowing();
        }
        return false;
    }

    public Context getContext(){
        return mContext;
    }

    public void clearContext() {
        if (null != mContext) {
            reset();
        }
    }
}
