package ru.vsu.cs.course2.hci.task1.filter;

public class AbsoluteFilter implements Filter {
    @Override
    public String getName() {
        return "Фильтр абсолютных значений";
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            res[i] = Math.abs(source[i]);
        }
        return res;
    }
}
