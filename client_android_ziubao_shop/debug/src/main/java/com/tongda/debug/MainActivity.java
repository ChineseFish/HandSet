package com.tongda.debug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tongda.base.Service;
import com.tongda.base.Transfer;
import com.tongda.base.Utils;

import org.json.JSONException;

public class MainActivity extends Activity {
    //
    private EditText urlTextView;
    private Button jumpButton;
    private Button printerSettingButton;
    private Button printerInitButton;
    private Button printerPrintButton;

    //
    public SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziubao_debug_main_activity);

        //
        mSp = getSharedPreferences("ziubao_debug", MODE_PRIVATE);

        //
        urlTextView = findViewById(R.id.ziubao_debug_url_textview);
        jumpButton = findViewById(R.id.ziubao_debug_jump_button);
        printerSettingButton = findViewById(R.id.ziubao_debug_printer_setting);
        printerInitButton = findViewById(R.id.ziubao_debug_printer_init);
        printerPrintButton = findViewById(R.id.ziubao_debug_printer_print);

        //
        urlTextView.setText(Db.getJumpUrl(mSp));
        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Db.writeJumpUrl(mSp, urlTextView.getText().toString());

                //
                Service mainService = Transfer.obtainService("app");
                mainService.app_jumpToUrl(MainActivity.this, urlTextView.getText().toString());

                //
                Transfer.startActivity(MainActivity.this, "app/main", new Intent());
            }
        });
        printerSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Transfer.startActivity(MainActivity.this, "ziubao_printer/main", new Intent());
            }
        });

        printerInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Service printService = Transfer.obtainService("printer");
                printService.printer_init(MainActivity.this);
            }
        });

        printerPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Service printService = Transfer.obtainService("printer");
                printService.printer_printBill(MainActivity.this, Utils.getJson(getApplicationContext(), "printContent.json"));
            }
        });
    }
}
