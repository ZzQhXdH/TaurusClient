package task;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.InputStream;

import application.IceCreamApplication;

public class HintTask implements Runnable {

    private byte[] mBytes;

    public HintTask(final String goodsType) {

        String row = goodsType.split("-")[0];
        try {
            InputStream in = IceCreamApplication.getAppContext().getAssets().open("hint/hint" + row + ".pcm");
            mBytes = new byte[in.available()];
            in.read(mBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        if (mBytes == null) {
            return;
        }

        AudioTrack track = new AudioTrack(AudioManager.STREAM_SYSTEM,
                16000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, mBytes.length, AudioTrack.MODE_STREAM);

        track.play();
        for (int i = 0; i < 5; i ++) {
            track.write(mBytes, 0, mBytes.length);
        }
        track.stop();
   }
}
