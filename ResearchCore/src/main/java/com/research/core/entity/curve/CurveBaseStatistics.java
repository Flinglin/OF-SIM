package com.research.core.entity.curve;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurveBaseStatistics  implements Serializable{

    private List<Double> data=new ArrayList<>();
    private double[] array;
    private boolean ready=false;
    private double max=Double.MAX_VALUE;
    private double min=Double.MIN_VALUE;
    private double sum=0;
    private double mean;
    private double sumsq=0;

    public CurveBaseStatistics(double[] d) {
        for(double i:d){
            this.data.add(i);
        }
        this.array=new double[this.data.size()];
        for(int k=0;k<this.data.size();k++){
            if(this.data.get(k)>this.max){
                this.max=this.data.get(k);
            }
            if(this.data.get(k)<this.min){
                this.min=this.data.get(k);
            }
            this.array[k]=this.data.get(k);
        }
        this.ready=true;
    }
    public void add(double value){
        data.add(value);
        ready = false;
    }

}
