package ru.vsu.cs.course2.hci.task1.filter;

public class SmoothFilter implements Filter {
    private final int smoothLevel;

    public SmoothFilter(int smoothLevel) {
        this.smoothLevel = smoothLevel;
    }

    @Override
    public String getName() {
        return String.format("Примитивный сглаживающий фильтр (%d)", smoothLevel);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            int start = Math.max(0, i - smoothLevel);
            int end = Math.min(i + smoothLevel, source.length);
            int count = end - start;
            int sum = 0;
            for (int j = start; j < end; j++) {
                sum += source[j];
            }
            res[i] = sum / (double) count;
        }
        return res;
    }
}

