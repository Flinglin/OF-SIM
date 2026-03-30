package com.research.utils.numpy;

public class ArrayMathUtil {
    public static double sumWithWeight(double[] values, double... weight) {
        if (weight.length == 1) {
            double sum = 0;
            for (double value : values) {
                sum += weight[0] * value;
            }
            return sum;
        } else {
            int length = Math.min(values.length, weight.length);
            double sum = 0;
            for (int i = 0; i < length; i++) {
                sum += weight[i] * values[i];
            }
            return sum;
        }
    }
    public static double[] add(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }
}
