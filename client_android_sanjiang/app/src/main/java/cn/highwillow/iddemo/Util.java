package cn.highwillow.iddemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;


public class Util {


    public static SoundPool soundPool ;
    public static HashMap<Integer, Integer> soundPoolMap;
    public static Context context;

    //init sound pool
    public static void initSoundPool(Context context){
        Util.context = context;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(context, R.raw.beep51, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.beep51, 2));
    }

    //play sound
    public static  void play(int sound, int loop){
        AudioManager mgr = (AudioManager)Util.context.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent/streamVolumeMax;
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
    }

}
