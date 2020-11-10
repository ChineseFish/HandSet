package com.tongda.base;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;

public class DownLoadDialog extends Dialog {
    public DownLoadDialog(Context context) {
        super(context);
    }

    public DownLoadDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DownLoadDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private View contentView;
        private ZzHorizontalProgressBar pb;
        public Builder(Context context) {
            this.context = context;
        }
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }
        public DownLoadDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DownLoadDialog dialog = new DownLoadDialog(context, R.style.ziubao_base_MyProgressDialog);
            View layout = inflater.inflate(R.layout.ziubao_base_download_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            pb = layout.findViewById(R.id.download_pb);
            dialog.setContentView(layout);
            return dialog;
        }

        public ZzHorizontalProgressBar getPb() {
            return pb;
        }

        public void setPb(ZzHorizontalProgressBar pb) {
            this.pb = pb;
        }
    }

}
