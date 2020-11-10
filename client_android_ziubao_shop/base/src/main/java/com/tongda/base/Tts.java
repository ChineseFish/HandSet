package com.tongda.base;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.tongda.base.log.LogUtils;

import java.util.Locale;

public class Tts {
    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                LogUtils.i("Tts", "onInit: TTS引擎初始化成功");

                int supported = mSpeech.setLanguage(Locale.CHINA);

                if (supported == TextToSpeech.LANG_MISSING_DATA || supported == TextToSpeech.LANG_NOT_SUPPORTED) {
                    LogUtils.i("Tts", "onInit: TTS引擎不支持中文");
                } else {
                    LogUtils.i("Tts", "onInit: TTS引擎支持中文");
                }
            } else {
                LogUtils.i("Tts", "onInit: TTS引擎初始化失败");
            }
        }
    }

    //
    private static TextToSpeech mSpeech = null;

    //
    private static Tts instance = new Tts();

    //
    private Tts(){

    }

    //
    public static Tts getInstance(){
        return instance;
    }

    public void textToSpeech(Context context, String text) {
        if(mSpeech == null)
        {
            LogUtils.d("Tts", "init audio module");

            mSpeech = new TextToSpeech(context, new TTSListener());
        }
        else
        {
            LogUtils.d("Tts", "audio module had been inited");
        }

        //
        mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}