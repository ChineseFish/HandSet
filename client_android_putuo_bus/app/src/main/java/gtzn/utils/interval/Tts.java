package gtzn.utils.interval;

import android.speech.tts.TextToSpeech;

import com.tongda.putuoshanlvyoubashi.MainActivity;
import com.tongda.putuoshanlvyoubashi.MyApplication;

import java.util.Locale;

import gtzn.utils.log.LogUtils;

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

    private static TextToSpeech mSpeech = null;

    public Tts() {
        if(mSpeech == null)
        {
            LogUtils.d("Tts", "init audio module");

            mSpeech = new TextToSpeech(MyApplication.getContext(), new TTSListener());
        }
        else
        {
            LogUtils.d("Tts", "audio module had been inited");
        }
    }

    public void textToSpeech(String text) {
        //
        mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}