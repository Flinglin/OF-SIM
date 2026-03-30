package com.research.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class TimeUnit {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private double timeLength;

    public TimeUnit(){}

    public void calculateTimeLength() {
        this.timeLength= (double) (Duration.between(this.startTime, this.endTime).toMillis())/1000.0;
    }
}
