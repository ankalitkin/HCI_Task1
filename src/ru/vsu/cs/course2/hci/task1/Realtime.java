package ru.vsu.cs.course2.hci.task1;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public class Realtime {
    private static final int SAMPLE_RATE = 48000;
    private static final int IGNORANCE_DELAY = SAMPLE_RATE / 10;
    private static final int WAITING_FOR_SECOND_DELAY = SAMPLE_RATE / 10 * 4;
    private static final int THRESHOLD = 800;
    public static Realtime INSTANCE = new Realtime();
    private TargetDataLine targetDataLine;
    private Thread captureThread;
    private boolean stopCapture = true;
    private AudioFormat audioFormat;

    {
        audioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    private Realtime() {
    }

    public void captureAudio(Consumer<Integer> consumer) {
        if (!stopCapture)
            return;
        try {
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            stopCapture = false;
            captureThread = new Thread(new CaptureThread(consumer));
            captureThread.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopCapture() {
        stopCapture = true;
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat((float) SAMPLE_RATE, 16, 1, true, false);
    }

    class CaptureThread extends Thread {
        private final Consumer<Integer> consumer;
        private byte[] tempBuffer = new byte[128];

        CaptureThread(Consumer<Integer> consumer) {
            this.consumer = consumer;
        }

        public void run() {
            int ignoreCount = 0;
            int waitingForSecond = 0;
            int sampleCount = 0;
            while (!stopCapture) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                if (cnt < 0) {
                    continue;
                }
                for (int i = 0; i < tempBuffer.length - 1; i += 2) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.put(tempBuffer[i]);
                    bb.put(tempBuffer[i + 1]);
                    short shortVal = bb.getShort(0);
                    waitingForSecond--;
                    sampleCount++;
                    if (ignoreCount-- <= 0 && Math.abs(shortVal) > THRESHOLD) {
                        ignoreCount = IGNORANCE_DELAY;
                        if (waitingForSecond <= 0) {
                            waitingForSecond = WAITING_FOR_SECOND_DELAY;
                            int result = sampleCount * 1000 / SAMPLE_RATE;
                            consumer.accept(result); //В миллисекундах
                            sampleCount = 0;
                        } else {
                            waitingForSecond = 0;
                        }
                    }
                }
            }
        }
    }
}