package ru.vsu.cs.course2.hci.task1.filter;

public class MedianFilter implements Filter {
    @Override
    public String getName() {
        return "Фильтр удаления постоянной состовляющей";
    }

    @Override
    public double[] doFilter(double[] source) {
        //Найдём среднее значение входных данных
        int sum = 0;
        for (double v : source) {
            sum += v;
        }
        double avg = sum / (double) source.length;
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            res[i] = source[i] - avg;
        }
        return res;
    }
}
