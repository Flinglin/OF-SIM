package com.research.utils.numpy;

import java.util.*;
import java.util.stream.IntStream;

public class RandomUtil {
    private static final Random rand = new Random();


    public static int[] randomArrayInt(int min, int max, int num, boolean repeat) {
        if(repeat){
            int[] res = new int[num];
            for (int i = 0; i < num; i++) {
                res[i] = rand.nextInt((int)min,(int)max);
            }
            return res;
        }else {
            int[] arr = IntStream.range(min, max).toArray();
            for (int i = arr.length; i > 1; i--) {
                int j = rand.nextInt(i);
                int tmp = arr[i-1];
                arr[i-1] = arr[j];
                arr[j] = tmp;
            }
            return arr;
        }
        }
}
