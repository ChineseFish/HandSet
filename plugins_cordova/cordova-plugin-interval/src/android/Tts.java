package gtzn.cordova.interval;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.tongda.putuoshanlvyoubashi.MainActivity;

import java.util.Locale;

public class Tts {
    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                Log.i("Tts", "onInit: TTS引擎初始化成功");

                int supported = mSpeech.setLanguage(Locale.CHINA);

                if (supported == TextToSpeech.LANG_MISSING_DATA || supported == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Log.i("Tts", "onInit: TTS引擎不支持中文");
                }
                else
                {
                    Log.i("Tts", "onInit: TTS引擎支持中文");
                }
            }
            else{
                Log.i("Tts", "onInit: TTS引擎初始化失败");
            }
        }
    }

    private TextToSpeech mSpeech = null;

    public Tts()
    {
        mSpeech = new TextToSpeech(MainActivity.getMainActivity(), new TTSListener());
    }

    public void textToSpeech(String text)
    {
        // 
        mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}