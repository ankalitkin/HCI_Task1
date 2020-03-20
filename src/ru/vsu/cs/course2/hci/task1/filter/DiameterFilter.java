package ru.vsu.cs.course2.hci.task1.filter;

public class DiameterFilter implements Filter {
    private final int width;
    private final LocalMinFilter localMinFilter;
    private final LocalMaxFilter localMaxFilter;


    public DiameterFilter(int width) {
        this.width = width;
        localMinFilter = new LocalMinFilter(width);
        localMaxFilter = new LocalMaxFilter(width);
    }

    @Override
    public String getName() {
        return String.format("Примитивный фильтр диаметра (%d)", width);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        double[] min = localMinFilter.doFilter(source);
        double[] max = localMaxFilter.doFilter(source);
        for (int i = 0; i < source.length; i++) {
            res[i] = max[i] - min[i];
        }
        return res;
    }
}
