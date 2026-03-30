package com.research.core.entity.curve;

import com.research.core.util.Dichotomy;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DoubleCurve  implements Serializable {
    private CurveBaseStatistics v0;
    private CurveBaseStatistics v1;
    private double[][] curveData;
    public DoubleCurve(double[][] data){
        curveData = new double[data.length][data[0].length];

        double[] tempV0=new double[data.length];
        double[] tempV1=new double[data.length];
        for(int i=0;i<data.length;i++){
            tempV0[i]=data[i][0];
            tempV1[i]=data[i][1];
        }
        v0=new CurveBaseStatistics(tempV0);
        v1=new CurveBaseStatistics(tempV1);
        for(int i=0;i<data.length;i++){
            System.arraycopy(data[i], 0, curveData[i], 0, data[0].length);
        }
    }
    public double getV1ByV0(double value){
        return Dichotomy.dichotomy(value,v0,v1);
    }
    public double getV0ByV1(double value){
        return Dichotomy.dichotomy(value,v1,v0);
    }
}
