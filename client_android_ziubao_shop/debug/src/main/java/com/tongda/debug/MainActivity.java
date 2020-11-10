package com.tongda.debug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tongda.base.Service;
import com.tongda.base.Transfer;

public class MainActivity extends Activity {
    //
    private EditText urlTextView;
    private Button jumpButton;
    private Button bluetoothPrintButton;

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
        bluetoothPrintButton = findViewById(R.id.ziubao_debug_bluetooth_print);

        //
        urlTextView.setText(Db.getJumpUrl(mSp));
        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Db.writeJumpUrl(mSp, urlTextView.getText().toString());

                //
                Transfer.startActivity(MainActivity.this, "app/main", new Intent());

                //
                Service mainService = Transfer.obtainService("app");
                mainService.app_jumpToUrl(MainActivity.this, urlTextView.getText().toString());
            }
        });
        bluetoothPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
