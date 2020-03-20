package ru.vsu.cs.course2.hci.task1.filter;

public class ThresholdFilter implements Filter {
    private final int threshold;

    public ThresholdFilter(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return String.format("Пороговый фильтр (%d)", threshold);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            res[i] = (source[i] >= threshold) ? threshold : 0;
        }
        return res;
    }
}

