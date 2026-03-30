package com.research.core.util;

import com.research.core.entity.curve.CurveBaseStatistics;

public class Dichotomy {
    public static double dichotomy(double x, double[] xa,double[] xy) {
        double result = -1;
        int min=0;
        int max=xa.length;
        int temp=max/2;
        if(max==0){
            return -1;
        }
        double order=1;
        if(xa[0]>xa[max-1]){
            order=-1;
        }
        if(order*x>order*xa[max-1]){
            return xy[max-1];
        } else if (order*x<order*xa[0]) {
            return xy[0];
        }
        for(int i=0;i<xa.length;i++){
            if(x == xa[i])return xy[i];
        }
        do
        {
            if(order*x>order*xa[temp])
                min = temp;
            else
                max = temp;
            temp = (max+min)/2;
        }while(min != temp);
        if(xa[min] == xa[max])
            return xy[min];
        else
            result= xy[min] +(xy[max]-xy[min])*(x-xa[min])/(xa[max]-xa[min]);

        return result;
    }
    public static double dichotomy(double x , CurveBaseStatistics xpack, CurveBaseStatistics ypack){
        return dichotomy(x, xpack.getArray(), ypack.getArray());
    }
}
