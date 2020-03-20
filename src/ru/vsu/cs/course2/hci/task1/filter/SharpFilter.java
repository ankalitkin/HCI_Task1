package ru.vsu.cs.course2.hci.task1.filter;

public class SharpFilter implements Filter {
    private final int sharpLevel;
    private final SmoothFilter smoothFilter;

    public SharpFilter(int smoothLevel) {
        this.sharpLevel = smoothLevel;
        this.smoothFilter = new SmoothFilter(sharpLevel);
    }

    @Override
    public String getName() {
        return String.format("Примитивный фильтр высоких частот (%d)", sharpLevel);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        double[] smooth = smoothFilter.doFilter(source);
        for (int i = 0; i < source.length; i++) {
            res[i] = source[i] - smooth[i];
        }
        return res;
    }
}

