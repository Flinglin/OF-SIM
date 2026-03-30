package com.research.core.entity;

import com.research.core.entity.canal.CanalEntity;
import com.research.core.enums.EntityTypeEnum;
import com.research.core.enums.NodeTypeEnum;
import com.research.utils.numpy.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Entity  implements Serializable{

    protected String name;

    protected String id;

    protected boolean statusDecision;

    protected EntityTypeEnum entityType;

    protected NodeTypeEnum nodeTypeEnum;

    protected List<CanalEntity> upCanalEntities=new ArrayList<>();

    protected List<CanalEntity> downCanalEntities=new ArrayList<>();

    protected double[] watershedRate;

    @Override
    public String toString() {
        return this.id+"-"+this.name;
    }
    protected void buildId(Object param){
        this.id=param.toString();
    }
    protected void buildName(Object param){
        this.name=param.toString();
    }
    protected void buildStatusDecision(Object param){
        this.statusDecision=NumberUtil.objectToBoolean(param);
    }

}
