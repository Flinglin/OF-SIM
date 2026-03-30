package com.research.utils.numpy;

public class ArrayUtil {

    public static double[][] zip(double[]... arr) {
        double[][] result = new double[arr[0].length][arr.length];
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                result[i][j] = arr[j][i];
            }
        }
        return result;
    }
    public static double[][] concat2DArray(double[][]... a) {

        int argsLength = a.length;
        int arrayLength = 0;
        for (double[][] doubles : a) {
            arrayLength += doubles.length;
        }
        double[][] result = new double[arrayLength][];
        for (int j = 0; j < argsLength; j++) {
            System.arraycopy(a[j], 0, result, j * a[j - 1 < 0 ? j : j - 1].length, a[j].length);
        }
        return result;
    }
}
