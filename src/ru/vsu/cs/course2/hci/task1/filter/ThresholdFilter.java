package ru.vsu.cs.course2.hci.task1.filter;

public class ThresholdFilter implements Filter {
    private final int threshold;
    private final int low;
    private final int high;

    public ThresholdFilter(int threshold, int low, int high) {
        this.threshold = threshold;
        this.low = low;
        this.high = high;
    }

    public ThresholdFilter(int threshold) {
        this(threshold, 0, 100);
    }

    @Override
    public String getName() {
        return String.format("Пороговый фильтр (%d)", threshold);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            res[i] = (source[i] >= threshold) ? high : low;
        }
        return res;
    }
}

