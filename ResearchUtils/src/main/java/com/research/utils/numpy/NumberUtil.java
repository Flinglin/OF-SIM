package com.research.utils.numpy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NumberUtil {
    public static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static LocalDateTime objectToLocalDateTime(Object obj) {
        return LocalDateTime.parse(obj.toString(), fmt);
    }
    public static int objectToInt(Object data) {
        if (data == null) {
            return 0;
        }
        double result = Double.parseDouble(data.toString());
        return (int) result;
    }
    public static double objectToDouble(Object data) {
        if (data == null) {
            return 0;
        }
        return Double.parseDouble(data.toString());
    }
    public static boolean objectToBoolean(Object data) {
        if (data == null) {
            return false;
        }
        return  data.toString().equals("1.0");
    }
    public static double[] objectWithDelimiterToArray(Object data) {
        if (data != null) {
            String[] paraString = data.toString().split(",");
            double[] para = new double[paraString.length];
            for (int j = 0; j < para.length; j++) {
                para[j] = objectToDouble(paraString[j]);
            }
            return para;
        }
        return new double[0];
    }
}
