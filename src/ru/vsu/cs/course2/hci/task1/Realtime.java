package ru.vsu.cs.course2.hci.task1;

import java.util.Scanner;
import java.util.function.Consumer;

public class Realtime {
    public static Realtime INSTANCE = new Realtime();
    private CaptureThread captureThread;

    public void startCapture(Consumer<Integer> consumer) {
        if (captureThread != null && !captureThread.stopCapture)
            stopCapture();
        captureThread = new CaptureThread(consumer);
        captureThread.start();
    }

    public void stopCapture() {
        if (captureThread == null)
            return;
        captureThread.stopCapture = true;
        captureThread = null;
    }

    class CaptureThread extends Thread {
        boolean stopCapture;
        private Consumer<Integer> consumer;

        CaptureThread(Consumer<Integer> consumer) {
            this.consumer = consumer;
            stopCapture = true;
        }

        public void run() {
            stopCapture = false;
            Scanner scanner = new Scanner(System.in);
            while (!stopCapture && scanner.hasNext()) {
                try {
                    int t = Integer.parseInt(scanner.next());
                    if (!stopCapture) {
                        consumer.accept(t);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}