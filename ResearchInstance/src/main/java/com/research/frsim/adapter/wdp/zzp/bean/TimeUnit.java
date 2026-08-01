package com.research.frsim.adapter.wdp.zzp.bean;

import java.util.Date;

public class TimeUnit {


	private Date startTime;

	private Date endTime;

	private double timeLength;
	
	
	public void calculateTimeLength(){
		
		timeLength = ((endTime.getTime() - startTime.getTime())/1000.0);
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public double getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
	}
	
}
