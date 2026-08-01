package com.research.apca.core.enums;

public enum FloodSimDirectEnum {

    FORWARD("forward"),
    REVERSE("reverse"),
    NORMAL("normal");
    private final String type;
    private FloodSimDirectEnum(String type) {
        this.type = type;
    }
}
