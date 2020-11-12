package com.tongda.printer;
import android.content.Context;
import android.widget.Toast;

import com.tongda.base.Service;

import org.json.JSONArray;

public class MainService extends Service {
    @Override
    public void printer_printBill(final Context context, String content) {
        try {
            JSONArray contentJson = new JSONArray(content);

            //
            XyyPrinter.printBill(context, contentJson);
        } catch(Exception e)
        {
            Toast.makeText(context, "printer_printBill throw exception, " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void printer_init(final Context context)
    {
        XyyPrinter.init(context);
    }
}
