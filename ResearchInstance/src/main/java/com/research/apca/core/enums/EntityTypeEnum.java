package com.research.apca.core.enums;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public enum EntityTypeEnum  implements Serializable {
    CANAL("01"),
    GATE("02"),
    PUMP("03"),
    INTAKE("04"),
    CATCHMENT("05"),
    LAKE("06"),
    RESERVOIR("07");
    private final String id;
    private EntityTypeEnum(String code) {
        this.id = code;
    }
}
