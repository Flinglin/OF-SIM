package com.research.apca.algorithm.optimize.utils;

import lombok.Data;

import java.util.List;

@Data
public class Information {
    private String message;
    private boolean success;
    private Object result;
    private List<Object> results;
}
