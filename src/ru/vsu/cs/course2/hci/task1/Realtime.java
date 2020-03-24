package ru.vsu.cs.course2.hci.task1;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public class Realtime {
    private static final int SAMPLE_RATE = 48000;
    private static final int IGNORANCE_DELAY = 20000;
    private static final int BUFFER_SIZE = 1280;
    public static int THRESHOLD = 800;
    public static Realtime INSTANCE = new Realtime();
    private TargetDataLine targetDataLine;
    private boolean stopCapture = true;
    private AudioFormat audioFormat;

    {
        audioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Мониторинг недоступен");
        }

    }

    private Realtime() {
    }

    public void captureAudio(Consumer<Integer> consumer, Consumer<Integer> maxConsumer) {
        if (!stopCapture)
            return;
        try {
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            stopCapture = false;
            Thread captureThread = new Thread(new CaptureThread(consumer, maxConsumer));
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
        private final Consumer<Integer> maxConsumer;
        private byte[] tempBuffer = new byte[BUFFER_SIZE];

        CaptureThread(Consumer<Integer> consumer, Consumer<Integer> maxConsumer) {
            this.consumer = consumer;
            this.maxConsumer = maxConsumer;
        }

        public void run() {
            int sampleCount = 0;
            while (!stopCapture) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                if (cnt < 0) {
                    continue;
                }
                int max = 0;
                for (int i = 0; i < tempBuffer.length - 1; i += 2) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    bb.put(tempBuffer[i]);
                    bb.put(tempBuffer[i + 1]);
                    short shortVal = bb.getShort(0);
                    if (max < shortVal)
                        max = shortVal;
                    //System.out.println(shortVal);
                    if (sampleCount++ >= IGNORANCE_DELAY && Math.abs(shortVal) > THRESHOLD) {
                        int result = sampleCount * 1000 / SAMPLE_RATE;
                        consumer.accept(result); //В миллисекундах
                        sampleCount = 0;
                    }
                }
                maxConsumer.accept(max);
            }
        }
    }
}