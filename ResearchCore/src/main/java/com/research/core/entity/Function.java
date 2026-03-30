package com.research.core.entity;

import com.research.core.enums.FunctionTypeEnum;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Function  implements Serializable {

    private FunctionTypeEnum lineType;

    private double[] param;

    public double calculateY(double x) {
        double y = 0;
        switch (this.lineType) {
            case LINEAR->{
                y=param[0]*x+param[1];
            }
            case UNARY_QUADRATIC -> {
                y=param[0]*Math.pow(x,2)+param[1]*x+param[2];
            }
            case POWER -> {
                y=param[0]*Math.pow(x,param[1]);
            }
            case EXPONENTIAL -> {
                y = param[0] * Math.pow(x, param[1] * x);
            }
            case LOGARITHMIC -> {
                y = param[0] * Math.log(x) + param[1];
            }
        }
        return y;
    }
}
