package com.audiotrack;


import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;


import java.lang.Math;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.FloatBuffer;

import com.facebook.react.bridge.Callback;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
import android.os.Build;
import android.util.Base64;
import android.util.Log;


public class RNAudioTrackModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static AudioTrack audioTrack;
    private boolean isFloat = false;

    int bufferSize = 2048;

    public RNAudioTrackModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    @Override
    public String getName() {
        return "RNAudioTrack";
    }

    @ReactMethod
    public void init(final ReadableMap options) {
        int streamType = AudioManager.STREAM_MUSIC;
        int sampleRateInHz = 8000;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;
        if (options.hasKey("streamType")) {
            streamType = options.getInt("streamType");
        }
        ;
        if (options.hasKey("bitsPerChannel")) {
            int bitsPerChannel = options.getInt("bitsPerChannel");
            isFloat = false;

            if (bitsPerChannel == 8) {
                audioFormat = AudioFormat.ENCODING_PCM_8BIT;
            } else if (bitsPerChannel == 32) {
                audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
                isFloat = true;
            }
        }
        if (options.hasKey("channelsPerFrame")) {
            int channelsPerFrame = options.getInt("channelsPerFrame");

            // every other case --> CHANNEL_IN_MONO
            if (channelsPerFrame == 2) {
                channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
            }
        }
        if (options.hasKey("sampleRate")) {
            sampleRateInHz = options.getInt("sampleRate");
        }
        if (options.hasKey("bufferSize")) {
            bufferSize = options.getInt("bufferSize");
        }
//        if (isFloat) {
//            bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);
//            Log.d("recorder", "setting buffer size " + bufferSize);
//        }
        audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSize, mode);
        audioTrack.play();
    }

    @ReactMethod
    public void play() {
        if (audioTrack != null) {
            audioTrack.play();
        }
    }

    @ReactMethod
    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    @ReactMethod
    public void pause() {
        if (audioTrack != null) {
            audioTrack.pause();
        }
    }

    @ReactMethod
    public void setVolume(float gain) {
        if (audioTrack != null) {
            audioTrack.setVolume(gain);
        }
    }

    @ReactMethod
    public void write(String base64String) {
        byte[] bytesArray = Base64.decode(base64String, Base64.NO_WRAP);
        if (audioTrack != null && bytesArray != null) {
            // if (isFloat && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //     FloatBuffer fb = ByteBuffer.wrap(bytesArray).asFloatBuffer();
            //     float[] buffer = new float[fb.capacity()];
            //     ByteBuffer.wrap(bytesArray).order(ByteOrder.nativeOrder()).asFloatBuffer().get(buffer);
            //     try {
            //         audioTrack.write(buffer, 0, buffer.length, AudioTrack.WRITE_BLOCKING);
            //     } catch (Exception ignored) {
            //     }
            // } else {
                short[] buffer = byte2short(bytesArray);
                try {
                    audioTrack.write(buffer, 0, buffer.length);
                } catch (Exception ignored) {
                }
            // }
        }
    }

    public static short[] byte2short(byte[] paramArrayOfbyte) {
        short[] arrayOfShort = new short[paramArrayOfbyte.length / 2];
        for (int i = 0; ; i += 2) {
            if (i >= paramArrayOfbyte.length)
                return arrayOfShort;
            byte b1 = paramArrayOfbyte[i];
            byte b2 = paramArrayOfbyte[i + 1];
            short s = (short) ((short) ((short) b1 & 0xFF) + (short) (b2 << 8));
            arrayOfShort[i / 2] = (short) s;
        }
    }
}
