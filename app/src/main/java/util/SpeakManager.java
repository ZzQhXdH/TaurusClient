package util;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import application.IceCreamApplication;

public class SpeakManager {

    private static final String DIR = "JfDir";
    private static final String BD1 = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
    private static final String BD2 = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
    private static final String BD3 = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
    private static final String BD4 = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
    private static final String BD5 = "bd_etts_text.dat";

    private SpeechSynthesizer mSpeechSynthesizer;

    public static SpeakManager instance() {
        return InlineClass.instance;
    }

    private static void copyAssetFile() {

        File dir = new File(Environment.getExternalStorageDirectory(), DIR);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), DIR + File.separator + BD1);
        if (!file.exists()) { // 不存在这个文件
            try {
                file.createNewFile();
                copyFileForAsset(file, BD1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(Environment.getExternalStorageDirectory(), DIR + File.separator + BD2);
        if (!file.exists()) { // 不存在这个文件
            try {
                file.createNewFile();
                copyFileForAsset(file, BD2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(Environment.getExternalStorageDirectory(), DIR + File.separator + BD3);
        if (!file.exists()) { // 不存在这个文件
            try {
                file.createNewFile();
                copyFileForAsset(file, BD3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(Environment.getExternalStorageDirectory(), DIR + File.separator + BD4);
        if (!file.exists()) { // 不存在这个文件
            try {
                file.createNewFile();
                copyFileForAsset(file, BD4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(Environment.getExternalStorageDirectory(), DIR + File.separator + BD5);
        if (!file.exists()) { // 不存在这个文件
            try {
                file.createNewFile();
                copyFileForAsset(file, BD5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void copyFileForAsset(File file, String assetFile) throws IOException {

        AssetManager manager = IceCreamApplication.getAppContext().getAssets();
        FileOutputStream fos = new FileOutputStream(file);
        InputStream is = manager.open(assetFile);
        int len;
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) > 0) {
            fos.write(bytes, 0, len);
        }
        is.close();
        fos.close();
    }

    public void speak(final String text) {
        int result = mSpeechSynthesizer.speak(text);
    }

    private SpeakManager() {

        copyAssetFile();
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(IceCreamApplication.getAppContext());
        mSpeechSynthesizer.setAppId("11135431");
        mSpeechSynthesizer.setApiKey("iT8x5zrczZqXMBRjKVAsPTxA", "r6rdkB5W4VXcASWh8TcTGqfXfkR3fYi0");

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
               Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR + File.separator + BD5);

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR + File.separator + BD1);

        TtsMode ttsMode = TtsMode.ONLINE;

        mSpeechSynthesizer.initTts(ttsMode);
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        mSpeechSynthesizer.setAudioStreamType(android.media.AudioManager.MODE_IN_CALL);
    }



    private static class InlineClass {
        public static SpeakManager instance = new SpeakManager();
    }
}
